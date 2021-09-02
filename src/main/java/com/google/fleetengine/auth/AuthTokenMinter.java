// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fleetengine.auth;

import com.google.auto.value.AutoValue;
import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.TripClaims;
import com.google.fleetengine.auth.token.VehicleClaims;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.TokenFactory;
import com.google.fleetengine.auth.token.factory.signer.Signer;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import javax.annotation.Nullable;

/**
 * Wraps {@link com.google.fleetengine.auth.token.FleetEngineToken} minting for different types of
 * tokens.
 *
 * <p>Assuming the application is running under GCP, then the application would typically be running
 * under the service account that will sign server tokens, while the non-server tokens will be
 * signed by different service accounts that will need to be impersonated.
 *
 * <p>When that is the case, set the token signer to {@link
 * com.google.fleetengine.auth.token.factory.signer.DefaultServiceAccountSigner} and the other two
 * signers to {@link com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner}. NOTE:
 * Before using any signer, check that the associated account has the correct permissions. Required
 * permissions are noted in the documentation on the provided signers.
 *
 * <pre>{@code
 * AuthTokenMinter minter = AuthTokenMinter.builder()
 *  .setServerTokenSigner(DefaultServiceAccountSigner.create())
 *  .setDriverSigner(ImpersonatedAccountSignerCredentials.create("driver@gcp-project.com")
 *  .setConsumerSigner(ImpersonatedAccountSignerCredentials.create("consumer@gcp-project.iam.gserviceaccount.com")
 *  .build();
 *
 *  String serverJwtHeader = minter.getServerToken().jwt();
 *  String driverJwtHeader = minter.getDriverToken("vehicle-123").jwt();
 *  String consumerJwtHeader = minter.getConsumerToken("trip-123").jwt();
 *
 * }</pre>
 */
@AutoValue
public abstract class AuthTokenMinter implements FleetEngineTokenProvider {
  /** Signer responsible for signing JWTs with a server key. */
  @Nullable
  public abstract Signer serverSigner();

  /** Signer responsible for signing JWTs with a driver key. */
  @Nullable
  public abstract Signer driverSigner();

  /** Signer responsible for signing JWTs with a consumer key. */
  @Nullable
  public abstract Signer consumerSigner();

  /** Signer responsible for signing JWTs with that aren't tied to the standard role set. */
  @Nullable
  public abstract Signer customSigner();

  /** Gets token factory that creates unsigned JWTs. */
  public abstract TokenFactory tokenFactory();

  /** Authorization state manager responsible for caching signed JWTs. */
  public abstract FleetEngineAuthTokenStateManager tokenStateManager();

  /**
   * Provides a builder for FleetEngineToken.
   *
   * <p>{@link NaiveAuthStateManager} set as default state manager.
   */
  public static AuthTokenMinter.Builder builder() {
    FleetEngineTokenFactorySettings settings = FleetEngineTokenFactorySettings.builder().build();
    return new AutoValue_AuthTokenMinter.Builder()
        .setTokenStateManager(new NaiveAuthStateManager())
        .setTokenFactory(new FleetEngineTokenFactory(settings));
  }

  /**
   * Returns a non expired Fleet Engine Token that was signed with the server signer.
   *
   * <p>Tokens will have an expiration of at least {@link
   * FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}.
   *
   * @throws SigningTokenException if there is an issue while signing the token.
   * @return Fleet Engine token with the "Server" role, guaranteed to be valid for {@link
   *     FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}
   */
  public FleetEngineToken getServerToken() throws SigningTokenException {
    FleetEngineToken unsignedToken = tokenFactory().createServerToken();
    return tokenStateManager().signToken(serverSigner(), unsignedToken);
  }

  /**
   * Returns a non expired Fleet Engine Token that was signed with the driver signer.
   *
   * <p>Tokens will have an expiration of at least {@link
   * FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}.
   *
   * @param claims custom vehicle claims
   * @throws SigningTokenException if the driver signer was not set, or if there is an issue while
   *     signing the token.
   * @return Fleet Engine token with the driver signer is not set, guaranteed to be valid for {@link
   *     FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}
   */
  public FleetEngineToken getDriverToken(VehicleClaims claims) throws SigningTokenException {
    if (driverSigner() == null) {
      throw new SigningTokenException(
          "Unable to sign Driver tokens due to the driver signer not being set.");
    }
    FleetEngineToken unsignedToken = tokenFactory().createDriverToken(claims);
    return tokenStateManager().signToken(driverSigner(), unsignedToken);
  }

  /**
   * Returns a non expired Fleet Engine Token that was signed with the consumer signer.
   *
   * <p>Tokens will have an expiration of at least {@link
   * FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}.
   *
   * @param claims custom trip claims
   * @throws SigningTokenException if the consumer signer was not set, or if there is an issue while
   *     signing the token.
   * @return Fleet Engine token with the "Consumer" role, guaranteed to be valid for {@link
   *     FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION} minutes.
   */
  public FleetEngineToken getConsumerToken(TripClaims claims) throws SigningTokenException {
    if (consumerSigner() == null) {
      throw new SigningTokenException(
          "Unable to sign consumer tokens due to the consumer signer not being set.");
    }
    FleetEngineToken unsignedToken = tokenFactory().createConsumerToken(claims);
    return tokenStateManager().signToken(consumerSigner(), unsignedToken);
  }

  /**
   * Returns a non expired Fleet Engine Token that was signed with the custom signer.
   *
   * <p>Tokens will have an expiration of at least {@link
   * FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION}.
   *
   * @param claims custom fleet engine token claim
   * @throws SigningTokenException if the custom signer was not set, or if there is an issue while
   *     signing the token.
   * @return Fleet Engine token with the any non standard role, guaranteed to be valid for {@link
   *     FleetEngineAuthTokenStateManager#EXPIRATION_WINDOW_DURATION} minutes.
   */
  public FleetEngineToken getCustomToken(FleetEngineTokenClaims claims)
      throws SigningTokenException {
    if (customSigner() == null) {
      throw new SigningTokenException(
          "Unable to sign custom tokens due to the custom signer not being set.");
    }
    FleetEngineToken unsignedToken = tokenFactory().createCustomToken(claims);
    return tokenStateManager().signToken(customSigner(), unsignedToken);
  }

  /** Returns a non-expired server token with a base64 signed JWT. */
  @Override
  public FleetEngineToken getSignedToken() throws SigningTokenException {
    return getServerToken();
  }

  /** Builder class for FleetEngineAuth. */
  @AutoValue.Builder
  public abstract static class Builder {
    /**
     * Sets the Signer responsible for signing server keys.
     *
     * <p>By default, set to {@link com.google.auth.ServiceAccountSigner}.
     */
    public abstract Builder setServerSigner(Signer serverSigner);

    /** Sets the Signer responsible for signing driver keys. */
    public abstract Builder setDriverSigner(Signer serverSigner);

    /** Sets the Signer responsible for signing consumer keys. */
    public abstract Builder setConsumerSigner(Signer serverSigner);

    /** Sets the Signer responsible for signing tokens that are not tied to a standard role. */
    public abstract Builder setCustomSigner(Signer customSigner);

    /**
     * Sets token factory that creates unsigned tokens.
     *
     * <p>By default, uses {@link FleetEngineTokenFactory}.
     */
    public abstract Builder setTokenFactory(TokenFactory tokenFactory);

    /**
     * Sets the authorization state manager responsible for caching signed JWTs
     *
     * <p>By default, uses a naive manager that only caches server tokens.
     *
     * @param manager manager providing valid tokens
     */
    public abstract Builder setTokenStateManager(FleetEngineAuthTokenStateManager manager);

    /** Builds {@link AuthTokenMinter}. */
    public abstract AuthTokenMinter build();
  }
}
