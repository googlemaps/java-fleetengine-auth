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

package com.google.fleetengine.auth.token.factory.signer;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.factory.signer.util.CommonConstants;
import java.io.IOException;

/**
 * Signs Fleet Engine tokens with the GCP service account that is running the application.
 *
 * <p>The default service account that the application is running under <b>MUST</b> have the
 * iam.serviceAccounts.signBlob permission. This is typically acquired through the "Service Account
 * Token Creator" role.
 *
 * <p>The default service account <b>MUST</b> have the same role as the {@link
 * com.google.fleetengine.auth.token.FleetEngineTokenType} of the token being signed.
 */
public class DefaultServiceAccountSigner implements Signer {
  private static final String ALGORITHM_NAME = "RS256";
  private static final String ALGORITHM_DESCRIPTION = "SHA256withRSA";
  private final ServiceAccountSignerCredentials serviceAccountCredentials;

  /**
   * Creates signer that uses the currently running service account.
   *
   * @throws SignerInitializationException if the credentials cannot be created in the current
   *     environment OR if the default user is not a service account.
   */
  public static DefaultServiceAccountSigner create() throws SignerInitializationException {
    GoogleCredentials defaultCredentials;
    try {
      defaultCredentials = GoogleCredentials.getApplicationDefault();
    } catch (IOException e) {
      throw new SignerInitializationException(
          "Could not retrieve credentials for default application.", e);
    }

    return create(defaultCredentials);
  }

  @VisibleForTesting
  static DefaultServiceAccountSigner create(GoogleCredentials defaultCredentials)
      throws SignerInitializationException {
    if (!(defaultCredentials instanceof ServiceAccountCredentials)) {
      throw new SignerInitializationException(
          "GCP default user account running the application is a NOT a service account.");
    }
    return new DefaultServiceAccountSigner(
        new ServiceAccountSignerCredentials((ServiceAccountCredentials) defaultCredentials));
  }

  /** Constructor. */
  @VisibleForTesting
  DefaultServiceAccountSigner(ServiceAccountSignerCredentials serviceAccountCredentials) {
    this.serviceAccountCredentials = serviceAccountCredentials;
  }

  /**
   * Signs a provided Fleet Engine token with the default service account.
   *
   * <p>The default service account that the application is running under <b>MUST</b> have the
   * iam.serviceAccounts.signBlob permission. This is typically acquired through the "Service
   * Account Token Creator" role.
   *
   * <p>The default service account <b>MUST</b> have the same role as the {@link
   * com.google.fleetengine.auth.token.FleetEngineTokenType} of the token being signed.
   *
   * @param token Fleet Engine token to sign
   * @return same value as the {@code token} parameter except with the JWT property set to a valid
   *     JWT
   */
  @Override
  public FleetEngineToken sign(FleetEngineToken token) {
    String signedToken =
        JWT.create()
            .withIssuer(serviceAccountCredentials.getClientEmail())
            .withSubject(serviceAccountCredentials.getClientEmail())
            .withAudience(token.audience())
            .withExpiresAt(token.expirationTimestamp())
            .withIssuedAt(token.creationTimestamp())
            .withClaim(
                CommonConstants.JWT_CLAIM_AUTHORIZATION_PROPERTY,
                token.authorizationClaims().toMap())
            .sign(new ServiceAccountSignerAlgorithm(serviceAccountCredentials));
    return token.toBuilder().setJwt(signedToken).build();
  }

  /** Signs with the service account from the default credentials. */
  static class ServiceAccountSignerAlgorithm extends Algorithm {
    private final ServiceAccountSignerCredentials serviceAccountCredentials;

    public ServiceAccountSignerAlgorithm(
        ServiceAccountSignerCredentials serviceAccountCredentials) {
      super(ALGORITHM_NAME, ALGORITHM_DESCRIPTION);
      this.serviceAccountCredentials = serviceAccountCredentials;
    }

    @Override
    public void verify(DecodedJWT jwt) {
      // Since this is a private inner class, this will never be called.
      throw new SignatureVerificationException(
          this, new RuntimeException("Verify not implemented"));
    }

    @Override
    public byte[] sign(byte[] contentBytes) {
      throw new SignatureVerificationException(
          this, new RuntimeException("sign(byte[]) is deprecated and not implemented"));
    }

    @Override
    public byte[] sign(byte[] headerBytes, byte[] payloadBytes) {
      return serviceAccountCredentials.sign(headerBytes, payloadBytes);
    }
  }

  /** Wraps {@link ServiceAccountCredentials} in order to mock final methods. */
  static class ServiceAccountSignerCredentials {

    private final ServiceAccountCredentials credentials;

    ServiceAccountSignerCredentials(ServiceAccountCredentials credentials) {
      this.credentials = credentials;
    }

    /** @return client email from underlying service account credentials */
    String getClientEmail() {
      return credentials.getClientEmail();
    }

    /**
     * Signs JWT with underlying service account credentials.
     *
     * @param headerBytes JWT header
     * @param payloadBytes JWT payload
     * @return signed bytes
     */
    byte[] sign(byte[] headerBytes, byte[] payloadBytes) {
      byte[] contentBytes = new byte[headerBytes.length + 1 + payloadBytes.length];
      System.arraycopy(headerBytes, 0, contentBytes, 0, headerBytes.length);
      contentBytes[headerBytes.length] = (byte) '.';
      System.arraycopy(payloadBytes, 0, contentBytes, headerBytes.length + 1, payloadBytes.length);
      return credentials.sign(contentBytes);
    }
  }
}
