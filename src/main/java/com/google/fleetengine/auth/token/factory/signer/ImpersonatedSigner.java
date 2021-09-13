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
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.factory.signer.util.CommonConstants;
import java.io.IOException;
import java.util.Arrays;

/**
 * Signs a Fleet Engine Token by impersonating a GCP service account.
 *
 * <p>The impersonated service account should match the role according to the {@link
 * com.google.fleetengine.auth.token.FleetEngineTokenType} of the token being signed.
 */
public class ImpersonatedSigner implements Signer {
  private static final String ALGORITHM_NAME = "RS256";
  private static final String ALGORITHM_DESCRIPTION = "SHA256withRSA";
  static final ImmutableList<String> IAM_SCOPE =
      ImmutableList.of("https://www.googleapis.com/auth/iam");

  @VisibleForTesting final ImpersonatedAccountSignerCredentials impersonatedCredentials;

  /**
   * Creates signer that impersonates a given service account.
   *
   * <p>The default service account that the application is running under <b>MUST</b> have the
   * iam.serviceAccounts.signBlob permission. This is typically acquired through the "Service
   * Account Token Creator" role.
   *
   * @param serviceAccount service account to impersonate that will sign Fleet Engine tokens
   */
  public static ImpersonatedSigner create(String serviceAccount)
      throws SignerInitializationException {
    GoogleCredentials defaultCredentials;
    try {
      defaultCredentials = GoogleCredentials.getApplicationDefault().createScoped(IAM_SCOPE);
    } catch (IOException e) {
      throw new SignerInitializationException(
          "Could not retrieve credentials for default application.", e);
    }
    return create(serviceAccount, defaultCredentials);
  }

  /**
   * Creates signer that impersonates a given service account using authenticated credentials
   *
   * <p>The <code>credentials</code> provided <b>MUST</b> have the iam.serviceAccounts.signBlob
   * permission. This is typically acquired through the "Service Account Token Creator" role.
   *
   * @param serviceAccount service account to impersonate that will sign Fleet Engine tokens
   * @param credentials used to sign on behalf of <code>serviceAccount</code>
   */
  public static ImpersonatedSigner create(String serviceAccount, GoogleCredentials credentials) {
    ImpersonatedCredentials impersonatedCredentials =
        createImpersonatedCredentials(serviceAccount, credentials).build();
    return new ImpersonatedSigner(
        new ImpersonatedAccountSignerCredentials(impersonatedCredentials));
  }

  @VisibleForTesting
  ImpersonatedSigner(ImpersonatedAccountSignerCredentials credentials) {
    this.impersonatedCredentials = credentials;
  }

  /**
   * Creates the impersonated credentials builder that will be used to sign a provided Fleet Engine
   * token.
   *
   * <p>Strictly exists for unit testing purposes since {@code AutoValue} classes cannot be mocked.
   */
  private static ImpersonatedCredentials.Builder createImpersonatedCredentials(
      String serviceAccount, GoogleCredentials credentials) {
    return ImpersonatedCredentials.newBuilder()
        .setSourceCredentials(credentials)
        // Sets the service account that the signer was created with.
        .setTargetPrincipal(serviceAccount)
        .setScopes(IAM_SCOPE);
  }

  /**
   * Signs a provided Fleet Engine token.
   *
   * <p>The impersonated service account should match the role according to the {@link
   * com.google.fleetengine.auth.token.FleetEngineTokenType} of the provided token.
   *
   * @param token Fleet Engine token to sign
   * @return same value as the {@code token} parameter except with the JWT property set to a valid
   *     JWT
   */
  @Override
  public FleetEngineToken sign(FleetEngineToken token) {
    String signedToken =
        JWT.create()
            .withIssuer(impersonatedCredentials.getAccount())
            .withSubject(impersonatedCredentials.getAccount())
            .withAudience(token.audience())
            .withExpiresAt(token.expirationTimestamp())
            .withIssuedAt(token.creationTimestamp())
            .withClaim(
                CommonConstants.JWT_CLAIM_AUTHORIZATION_PROPERTY,
                token.authorizationClaims().toMap())
            .sign(new ImpersonatedSignerAlgorithm(impersonatedCredentials));
    return token.toBuilder().setJwt(signedToken).build();
  }

  /** Signs the JWT with impersonated credentials */
  static class ImpersonatedSignerAlgorithm extends Algorithm {

    private final ImpersonatedAccountSignerCredentials impersonatedCredentials;

    public ImpersonatedSignerAlgorithm(
        ImpersonatedAccountSignerCredentials impersonatedCredentials) {
      super(ALGORITHM_NAME, ALGORITHM_DESCRIPTION);
      this.impersonatedCredentials = impersonatedCredentials;
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
      // Equivalent of calling signBlob through gcloud
      return impersonatedCredentials.sign(headerBytes, payloadBytes);
    }
  }
  /** Exists for unit testing purposes. */
  static class ImpersonatedAccountSignerCredentials {

    private final ImpersonatedCredentials credentials;

    public ImpersonatedCredentials getUnderlyingCredentials() {
      return credentials;
    }

    ImpersonatedAccountSignerCredentials(ImpersonatedCredentials credentials) {
      this.credentials = credentials;
    }

    public String getAccount() {
      return credentials.getAccount();
    }

    /**
     * Signs JWT with underlying service account credentials.
     *
     * @param headerBytes JWT header
     * @param payloadBytes JWT payload
     * @return signed bytes
     */
    byte[] sign(byte[] headerBytes, byte[] payloadBytes) {
      byte[] contentBytes =
          Arrays.copyOf(headerBytes, headerBytes.length + 1 + payloadBytes.length);
      contentBytes[headerBytes.length] = (byte) '.';
      System.arraycopy(payloadBytes, 0, contentBytes, headerBytes.length + 1, payloadBytes.length);
      return credentials.sign(contentBytes);
    }
  }
}
