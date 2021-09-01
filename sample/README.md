# Fleet Engine Auth Sample Scripts

When the application is run, a menu option appears. The first option validates
that the configured service accounts are working as expected. This is helpful
when first setting up your GCP project.

The other options run hardcoded commands such as:

1. Create a vehicle with a random id
1. Search for a random set of trips and display to the terminal

You can use these examples as a starting point when first using the generated
client libraries.

## Configuring

The scripts are configured with a service account to run under. Each service
account must be configured according to the instructions in the auth library
[README](../README.md).

NOTE: Whatever account the application runs under, MUST have the
`iam.serviceAccounts.signBlob` permission. This is typically acquired through
the `Service Account Token Creator` role.

The service accounts with other configuration options are configured here:
`src/main/java/com/google/fleetengine/auth/sample/Configuration.java`

## Running

Once `src/main/java/com/google/fleetengine/auth/sample/Configuration.java` is
setup, simply run:

```
./gradlew run
```
