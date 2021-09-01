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

import static com.google.common.truth.Truth.assertThat;
import static com.google.fleetengine.auth.token.factory.signer.JwtPayload.Authorization.CLAIM_KEY_1;
import static com.google.fleetengine.auth.token.factory.signer.JwtPayload.Authorization.CLAIM_KEY_2;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import com.google.fleetengine.auth.EmptyFleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.util.CommonConstants;
import com.google.fleetengine.auth.token.factory.signer.util.RSAPrivateKeyUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocalSignerTest {
  private static final String CLIENT_EMAIL = "fake_server@fake_project.iam.gserviceaccount.com";
  private static final String TEST_AUDIENCE = "https://test.jwt.audience";
  static final String CLAIM_VALUE_1 = "LocalSigner Value 1";
  static final String CLAIM_VALUE_2 = "LocalSigner Value 2";

  // Private key values, take from ./test_data/fake_server_key.json
  private static final String FAKE_PRIVATE_KEY_ID = "1122334455aabbcc";
  private static final String FAKE_PRIVATE_KEY =
      "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAisr9GkH78pfk4l19\n"
          + "lMgQe55aLVvMf2D/WqH1WVnKPi8kQrTWOEEmMDD0pCV/Z7Kf4BxMQ96sDaTGLMCw\n"
          + "XOKwSQIDAQABAkBQJs+p1UdBnrRggWsfe7YBHb6oCSZ8vTBaT8OXmQHxjH/jb5WE\n"
          + "WFyjnlPwrkoglX3g5qO3l0z++do97kE29vyBAiEAxft8MvgoAUXgCkcs+SDK+5p6\n"
          + "n8D7dLQPFt8kBvffnTkCIQCzdyX2X+WfUXK7aeU4nNoOaUZT3dLdDDKrtoGCfHK7\n"
          + "kQIgAZwy/WMhSCleUhcUGY6XuAYgmy4BnYhdWw1DiL75VZkCIHHzGTuSMC40fPDa\n"
          + "kByeGyZDWdAubJDGCfZVb232rrLBAiEAnpgYMtqCIT+tfY/bJm8J9WdUARbWDTsi\n"
          + "nFioN+6sGTM=\n";
  private static final String FAKE_PRIVATE_INVALID_KEY =
      "-----BEGIN PRIVATE KEY-----\nBANANA\n-----END PRIVATE KEY-----\n";

  // Generated public key to verify signature with
  private static final String FAKE_PUBLIC_KEY =
      "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIrK/RpB+/KX5OJdfZTIEHueWi1bzH9g/1qh9VlZyj4vJEK01jhBJjAw9KQlf2eyn+AcTEPerA2kxizAsFzisEkCAwEAAQ==";

  private Clock creation;
  private Clock expiration;
  private FleetEngineTokenClaims claims;

  @Before
  public void setup() {
    this.creation = mock(Clock.class);
    when(this.creation.instant()).thenReturn(Instant.EPOCH);

    this.expiration = mock(Clock.class);
    when(this.expiration.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofDays(1)));

    this.claims = mock(FleetEngineTokenClaims.class);
  }

  @Test
  public void sign_returnsCorrectJwtHeader() throws SigningTokenException {
    LocalSigner localSigner =
        LocalSigner.create(CLIENT_EMAIL, FAKE_PRIVATE_KEY_ID, FAKE_PRIVATE_KEY);
    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.SERVER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            .setAudience(TEST_AUDIENCE)
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .build();

    FleetEngineToken signedToken = localSigner.sign(token);

    DecodedJWT decodedJWT = JWT.decode(signedToken.jwt());
    String header = new String(Base64.getDecoder().decode(decodedJWT.getHeader()), UTF_8);
    Gson gson = new Gson();
    JwtHeader jwtHeader = gson.fromJson(header, JwtHeader.class);
    assertThat(jwtHeader.keyId).isEqualTo(FAKE_PRIVATE_KEY_ID);
  }

  @Test
  public void sign_returnsCorrectJwtPayload() throws SigningTokenException {
    LocalSigner localSigner =
        LocalSigner.create(CLIENT_EMAIL, FAKE_PRIVATE_KEY_ID, FAKE_PRIVATE_KEY);
    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.SERVER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .build();

    FleetEngineToken signedToken = localSigner.sign(token);

    DecodedJWT decodedJWT = JWT.decode(signedToken.jwt());
    String payload = new String(Base64.getDecoder().decode(decodedJWT.getPayload()), UTF_8);
    Gson gson = new Gson();
    JwtPayload jwtPayload = gson.fromJson(payload, JwtPayload.class);

    // Payload is signed with the default jwt audience
    assertThat(jwtPayload.audience).isEqualTo(CommonConstants.DEFAULT_JWT_AUDIENCE);

    assertThat(jwtPayload.issuer).isEqualTo(CLIENT_EMAIL);
    assertThat(jwtPayload.subject).isEqualTo(CLIENT_EMAIL);
    assertThat(jwtPayload.issuedAt).isEqualTo(creation.instant().getEpochSecond());
    assertThat(jwtPayload.expiredAt).isEqualTo(expiration.instant().getEpochSecond());
  }

  @Test
  public void sign_returnsValidSignature()
      throws SigningTokenException, InvalidKeySpecException, NoSuchAlgorithmException, IOException {
    LocalSigner localSigner =
        LocalSigner.create(CLIENT_EMAIL, FAKE_PRIVATE_KEY_ID, FAKE_PRIVATE_KEY);
    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.SERVER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .build();

    FleetEngineToken signedToken = localSigner.sign(token);

    DecodedJWT decodedJWT = JWT.decode(signedToken.jwt());
    // Use the same type of algorithm used to sign the JWT to also validate the JWT
    RSAPrivateKey privateKey = RSAPrivateKeyUtils.getPrivateKey(FAKE_PRIVATE_KEY);
    RSAPublicKey publicKey = createPublicKey();
    Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
    // Throws SignatureVerificationException if the signature is incorrect
    algorithm.verify(decodedJWT);
  }

  @Test
  public void sign_whenInvalidSignature_throwsSigningTokenException() {
    LocalSigner localSigner =
        LocalSigner.create(CLIENT_EMAIL, FAKE_PRIVATE_KEY_ID, FAKE_PRIVATE_INVALID_KEY);
    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.SERVER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .build();

    Assert.assertThrows(SigningTokenException.class, () -> localSigner.sign(token));
  }

  @Test
  public void sign_customClaims_returnsSameIds() throws SigningTokenException {
    LocalSigner localSigner =
        LocalSigner.create(CLIENT_EMAIL, FAKE_PRIVATE_KEY_ID, FAKE_PRIVATE_KEY);
    when(claims.toMap())
        .thenReturn(
            ImmutableMap.of(
                CLAIM_KEY_1, CLAIM_VALUE_1,
                CLAIM_KEY_2, CLAIM_VALUE_2));

    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.CONSUMER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            // The specifics of what is in the authorization claim is irrelevant to the signer
            .setAuthorizationClaims(claims)
            .build();

    FleetEngineToken signedToken = localSigner.sign(token);

    DecodedJWT decodedJWT = JWT.decode(signedToken.jwt());
    String payload = new String(Base64.getDecoder().decode(decodedJWT.getPayload()), UTF_8);
    Gson gson = new Gson();
    JwtPayload jwtPayload = gson.fromJson(payload, JwtPayload.class);
    assertThat(jwtPayload.authorization.numberOne).isEqualTo(CLAIM_VALUE_1);
    assertThat(jwtPayload.authorization.numberTwo).isEqualTo(CLAIM_VALUE_2);
  }

  private RSAPublicKey createPublicKey()
      throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    byte[] bytes =
        Base64.getDecoder().decode(FAKE_PUBLIC_KEY.getBytes(StandardCharsets.ISO_8859_1));
    X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPublicKey) kf.generatePublic(spec);
  }
}
