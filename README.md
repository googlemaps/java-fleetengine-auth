# Fleet Engine Auth Library for Java

Fleet Engine use JSON Web Tokens (JWTs) to both authenticate and authorize
incoming requests. This process has several aspects to it and is non-trivial to
set up. This Fleet Engine Auth Library provides a set of tools to simplify the
setup process.

This library provides the following benefits:

* Simplifies the process of creating Fleet Engine Tokens.
* Provides token signing mechanisms other than using credential files (such as
  impersonating a service account.)
* Attaches signed tokens to outbound requests made from either a GAPIC client or
  gRPC stub.

Sample scripts are provided in the sample directory. To learn more, see:
[sample/README.md](sample/README.md).

## Installation

You can add the library to your project via Maven or Gradle.

### Maven
```xml
<dependency>
  <groupId>com.google.maps</groupId>
  <artifactId>fleetengine-auth</artifactId>
  <version>(insert latest version)</version>
</dependency>
<dependency>
  <groupId>com.auth0</groupId>
  <artifactId>java-jwt</artifactId>
  <version>3.10.2</version>
</dependency>
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>1.55.0</version>
</dependency>
<dependency>
  <groupId>com.google.auth</groupId>
  <artifactId>google-auth-library-oauth2-http</artifactId>
  <version>0.26.0</version>
</dependency>
```


### Gradle
```groovy
dependencies {
  implementation 'com.google.maps:fleetengine-auth:(insert latest version here)'
  implementation 'com.auth0:java-jwt:3.10.2'
  implementation 'com.google.guava:guava:1.55.0'
  implementation 'com.google.auth:google-auth-library-oauth2-http:0.26.0'
}
```

## Concepts

### Fleet Engine Roles

Fleet Engine Requests are always made on behalf of a service account defined in
your GCP project. Each service account is tied to a pre-defined Fleet Engine
Role.

The supported Fleet Engine roles are:

Role                           | Description
:----------------------------- | :---------:
Fleet Engine Consumer SDK User | Grants permission to search for vehicles and to retrieve information about vehicles and trips. Tokens minted by a service account with this role can be used from your ridesharing consumer app mobile devices.
Fleet Engine Driver SDK User   | Grants permission to update vehicle locations and routes and to retrieve information about vehicles and trips. Tokens minted by a service account with this role can be used from your ridesharing driver app mobile devices.
FleetEngine Service Super User | Grants permission to all vehicles and trips APIs. Tokens minted by a service account with this role can be used from your backend servers.
Fleet Engine Delivery Consumer SDK User | Grants permission to search for tasks using a tracking ID, and to read but not update task information. Tokens minted by a service account with this role are typically used from a delivery consumer's web browser.
Fleet Engine Delivery Untrusted Driver User | Grants permission to update delivery vehicle location. Tokens minted by a service account with this role are typically used from your delivery driver's mobile devices.
Fleet Engine Delivery Trusted Driver User | Grants permission to create and update delivery vehicles and tasks, including updating the delivery vehicle location and task status or outcome. Tokens minted by a service account with this role are typically used from your delivery driver's mobile devices or from your backend servers.
Fleet Engine Delivery Fleet Reader | Grants permission to read delivery vehicles and tasks and to search for tasks using a tracking ID. Tokens minted by a service account with this role are typically used from a delivery fleet operator's web browser.
Fleet Engine Delivery Super User| Grants permission to all delivery vehicles and tasks APIs. Tokens minted by a service account with this role are typically used from your backend servers.

Each role is tied to a `com.google.fleetengine.auth.token.FleetEngineTokenType`,
and each type of token can be constrained to a specific resource:

Role                                | Token Type                      | Resource Constraint
:---------------------------------- | :-----------------------------: | :-----------------:
Fleet Engine Consumer SDK User      | `FleetEngineTokenType#CONSUMER` | trip id
Fleet Engine Driver SDK User        | `FleetEngineTokenType#DRIVER`   | vehicle id
Fleet Engine Service Super SDK User | `FleetEngineTokenType#SERVER`   | (no constraint)
Fleet Engine Delivery Consumer SDK User      | `FleetEngineTokenType#DELIVERY_CONSUMER` | task id OR tracking id
Fleet Engine Delivery Untrusted Driver SDK User        | `FleetEngineTokenType#UNTRUSTED_DELIVERY_DRIVER`   | delivery vehicle id
Fleet Engine Delivery Trusted Driver SDK User | `FleetEngineTokenType#TRUSTED_DELIVERY_DRIVER`   | delivery vehicle id and task id
Fleet Engine Delivery Fleet Reader SDK User | `FleetEngineTokenType#DELIVERY_FLEET_READER`   | (no constraint)
Fleet Engine Delivery Super SDK User | `FleetEngineTokenType#DELIVERY_SERVER`   | (no constraint)


### JWT Signers

The Fleet Engine Auth Library signs JWTs through classes that implement the
`com.google.fleetengine.auth.token.factory.signer.Signer` interface. The Library
comes loaded with three predefined Signers which handle the common use cases
(the set of pre-defined signers are located below the
`com.google.fleetengine.auth.token.factory.signer` package):

Signer                        | GCP Required | Description
:---------------------------: | :----------: | :---------:
`DefaultServiceAccountSigner` | Yes          | Signs tokens with the service account running the application. This signer is typically used to sign `FleetEngineTokenType#SERVER` tokens. The service account <b>MUST</b> have the `iam.serviceAccounts.signBlob` permission in order to use this Signer. This is typically acquired through the `Service Account Token Creator` role.
`ImpersonatedSigner`          | Yes          | Signs tokens by impersonating a different service account. The account hosting the application <b>MUST</b> have the `iam.serviceAccounts.signBlob` permission. This permission is typically acquired through the `Service Account Token Creator` role.
`LocalSigner`                 | No           | Signs tokens with a private key file generated by a given service account. **Storing private key files in any form presents a security risk and should be a last resort.**

Note: GCP Required denotes that the Signer works with applications that are
hosted on GCP or are otherwise authenticated with GCP. For more information,
see: https://cloud.google.com/docs/authentication/getting-started.

## Using the library

### Minting Tokens

`com.google.fleetengine.auth.AuthTokenMinter` ties signers with roles to make
minting tokens straightforward.

To use, first associate a `Signer` with a service account and a type of token.

For example, when creating tokens for use with the On Demand Rides and Deliveries APIs:

```java
AuthTokenMinter minter = AuthTokenMinter.builder()
  .setServerTokenSigner(DefaultServiceAccountSigner.create())
  .setDriverSigner(ImpersonatedAccountSignerCredentials.create("driver@gcp-project.com")
  .setConsumerSigner(ImpersonatedAccountSignerCredentials.create("consumer@gcp-project.iam.gserviceaccount.com")
  .build();
```

When creating tokens for use with the Last Mile Fleet Services, use:

```java
AuthTokenMinter minter = AuthTokenMinter.deliveryBuilder()
  .setDeliveryServerSigner(DefaultServiceAccountSigner.create())
  .setDeliveryConsumerSigner(ImpersonatedAccountSignerCredentials.create("delivery-consumer@gcp-project.com")
  .setUntrustedDeliveryDriverSigner(ImpersonatedAccountSignerCredentials.create("untrusted-delivery-driver-signer@gcp-project.iam.gserviceaccount.com")
  .setTrustedDeliveryDriverSigner(ImpersonatedAccountSignerCredentials.create("trusted-delivery-driver-signer@gcp-project.iam.gserviceaccount.com")
  .setDeliveryFleetReaderSigner(ImpersonatedAccountSignerCredentials.create("delivery-fleet-reader@gcp-project.iam.gserviceaccount.com")
  .build();
```

The minter provides a getter method for each of the token types. Each getter
returns an instance of `com.google.fleetengine.auth.token.FleetEngineToken`:

```java
FleetEngineToken serverToken = minter.getServerToken();

FleetEngineToken consumerToken = minter.getConsumerToken(TripClaims.create("trip-id-123"));

FleetEngineToken driverToken = minter.getDriverToken(VehicleClaims.create("vehicle-id-123"));
```

A `FleetEngineToken` has several attributes, but in most cases, only the base64
encoded JWT is needed `com.google.fleetengine.auth.token.FleetEngineToken#jwt`.

```java
System.out.println("Base64 encoded JWT:");
System.out.println(serverToken.jwt());

System.out.println("HTTP Header:");
System.out.println(String.format("Authorization: Bearer %s", token.jwt()));
```

Output:

```
Base64 encoded JWT:
xxxxx.yyyyy.zzzzz

HTTP Header:
Authorization: Bearer xxxxx.yyyyy.zzzzz
```

## Working with Generated Clients

Alongside token minting functionality, the library also provides a set of
components that work with Fleet Engine protobuf generated classes. There are two
sets of generated classes that make requests to the fleet engine server, GAPIC
clients and gRPC stubs. Using GAPIC clients over gRPC stubs is recommended.

To learn more about GAPIC clients, see:
https://developers.google.com/maps/documentation/transportation-logistics/on-demand-rides-deliveries-solution/trip-order-progress/fleet-engine/gapic_client
https://developers.google.com/maps/documentation/transportation-logistics/last-mile-fleet-solution/shipment-tracking/fleet-engine/gapic_client

Both mechanisms require a FleetEngineTokenProvider which is an interface that
has just one method:

```java
package com.google.fleetengine.auth.client;

/** Provides non-expired, signed JWTs. */
public interface FleetEngineTokenProvider {
  /** Returns a non-expired {@link FleetEngineToken} with a base64 signed JWT. */
  FleetEngineToken getSignedToken() throws SigningTokenException;
}
```

For convenience, `com.google.fleetengine.auth.AuthTokenMinter` implements
`FleetEngineTokenProvider` and returns server tokens when called through
`FleetEngineTokenProvider#getSignedToken`. Any type of token can be returned.

### Sharing Minters

By default, `AuthTokenMinter` caches signed tokens with a five-minute
expiration. In order to take advantage of its internal cache, it must be shared
across uses. Having one singleton instance is recommended.

### Integrating with GAPIC Clients

GAPIC clients are configured using `com.google.api.gax.rpc.ClientSettings` which
are created with a builder. The
`com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier` updates
`ClientSettings.Builder`s such that outbound requests made from the
corresponding client have valid authorization headers.

```java
FleetEngineClientSettingsModifier<VehicleServiceSettings, VehicleServiceSettings.Builder> modifier =
  //In most cases, tokenProvider will be a singleton instance of AuthTokenMinter
  new FleetEngineClientSettingsModifier<>(tokenProvider); 

VehicleServiceSettings.Builder builder = VehicleServiceSettings.newBuilder();
VehicleServiceSettings settings = modifier.updateBuilder(builder).build();

try (VehicleServiceClient client = VehicleServiceClient.create(settings)) {
  // make request
}
```

For more information around the Fleet Engine GAPIC clients, see:
https://developers.google.com/maps/documentation/transportation-logistics/on-demand-rides-deliveries-solution/trip-order-progress/fleet-engine/gapic_client
https://developers.google.com/maps/documentation/transportation-logistics/last-mile-fleet-solution/shipment-tracking/fleet-engine/gapic_client
### Generated gRPC Stubs

gRPC stubs are initialized from `io.grpc.ManagedChannel`s and allows
functionality to be injected using interceptors.

Using GAPIC clients in lieu of gRPC stubs is recommended.

```java
FleetEngineTokenProvider fleetEngineTokenProvider = getTokenProvider();

ManagedChannel channel = ManagedChannelBuilder.forTarget(fleetEngineAddress)
  //In most cases, tokenProvider will be a singleton instance of AuthTokenMinter
  .intercept(FleetEngineAuthClientInterceptor.create(fleetEngineTokenProvider))
  .build();

VehicleServiceBlockingStub stub = VehicleServiceGrpc.newBlockingStub(channel);
```
