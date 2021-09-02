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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.fleetengine.auth.EmptyFleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner.ImpersonatedAccountSignerCredentials;
import com.google.gson.Gson;
import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ImpersonatedSignerTest {

  private static final String TEST_SERVICE_ACCOUNT = "testServiceAccount";
  private static final String TEST_AUDIENCE = "https://test.jwt.audience";
  private Clock creation;
  private Clock expiration;
  private GoogleCredentials defaultCredentials;

  @Before
  public void setup() {
    this.creation = mock(Clock.class);
    when(this.creation.instant()).thenReturn(Instant.EPOCH);

    this.expiration = mock(Clock.class);
    when(this.expiration.instant()).thenReturn(Instant.EPOCH.plus(Duration.ofDays(1)));

    this.defaultCredentials = mock(GoogleCredentials.class);
  }

  @Test
  public void sign_buildsJwtCorrectly() {
    FleetEngineToken token =
        FleetEngineToken.builder()
            .setTokenType(FleetEngineTokenType.SERVER)
            .setCreationTimestamp(Date.from(creation.instant()))
            .setExpirationTimestamp(Date.from(expiration.instant()))
            .setAudience(TEST_AUDIENCE)
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .build();
    // Mock impersonated credentials
    ImpersonatedAccountSignerCredentials impersonatedCredentials =
        mock(ImpersonatedAccountSignerCredentials.class);
    when(impersonatedCredentials.getAccount()).thenReturn(TEST_SERVICE_ACCOUNT);
    when(impersonatedCredentials.sign(any(), any()))
        .thenAnswer(
            invocation -> {
              byte[] presignedHeaderJwt = invocation.getArgument(0, byte[].class);
              byte[] presignedContentJwt = invocation.getArgument(0, byte[].class);
              return Algorithm.none().sign(presignedHeaderJwt, presignedContentJwt);
            });

    ImpersonatedSigner signer = new ImpersonatedSigner(impersonatedCredentials);

    // Sign the token with the "none" algorithm.
    FleetEngineToken signedToken = signer.sign(token);

    // Check that the payload matches what was expected
    DecodedJWT decodedJWT = JWT.decode(signedToken.jwt());
    String payload = new String(Base64.getDecoder().decode(decodedJWT.getPayload()), UTF_8);
    Gson gson = new Gson();
    JwtPayload jwtPayload = gson.fromJson(payload, JwtPayload.class);
    assertThat(jwtPayload.audience).isEqualTo(TEST_AUDIENCE);
    assertThat(jwtPayload.issuer).isEqualTo(TEST_SERVICE_ACCOUNT);
    assertThat(jwtPayload.subject).isEqualTo(TEST_SERVICE_ACCOUNT);
    assertThat(jwtPayload.issuedAt).isEqualTo(creation.instant().getEpochSecond());
    assertThat(jwtPayload.expiredAt).isEqualTo(expiration.instant().getEpochSecond());
  }

  @Test
  public void sign_createsImpersonatedCredentialsBuilderCorrectly() {
    ImpersonatedSigner signer =
        ImpersonatedSigner.create(TEST_SERVICE_ACCOUNT, this.defaultCredentials);

    ImpersonatedCredentials impersonatedCredentials =
        signer.impersonatedCredentials.getUnderlyingCredentials();

    // The only way to check that the source credentials and scopes were set correctly is through
    // getting the values off of the corresponding build
    ImpersonatedCredentials.Builder impersonatedCredentialsBuilder =
        impersonatedCredentials.toBuilder();

    assertThat(impersonatedCredentials.getAccount()).isEqualTo(TEST_SERVICE_ACCOUNT);
    assertThat(impersonatedCredentialsBuilder.getTargetPrincipal()).isEqualTo(TEST_SERVICE_ACCOUNT);
    assertThat(impersonatedCredentialsBuilder.getSourceCredentials()).isEqualTo(defaultCredentials);
  }
}
