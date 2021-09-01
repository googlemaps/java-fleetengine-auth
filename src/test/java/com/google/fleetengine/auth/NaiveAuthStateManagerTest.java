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

import static com.google.common.truth.Truth.assertThat;
import static com.google.fleetengine.auth.FleetEngineAuthTokenStateManager.EXPIRATION_WINDOW_DURATION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.Signer;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.time.Instant;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NaiveAuthStateManagerTest {
  private static final String TEST_JWT = "i_am_a.test_jwt.signed";
  private FleetEngineTokenClaims wildcardClaim;
  private FleetEngineTokenClaims nonWildcardClaim;

  private Signer signer;
  private FleetEngineToken token;
  private FleetEngineToken token2;
  private FleetEngineTokenExpiryValidator expiryValidator;

  @Before
  public void setup() {
    signer = mock(Signer.class);
    expiryValidator = mock(FleetEngineTokenExpiryValidator.class);

    wildcardClaim = mock(FleetEngineTokenClaims.class);
    when(wildcardClaim.isWildcard()).thenReturn(true);
    when(wildcardClaim.toMap()).thenReturn(ImmutableMap.of());

    nonWildcardClaim = mock(FleetEngineTokenClaims.class);
    when(nonWildcardClaim.isWildcard()).thenReturn(false);
    when(nonWildcardClaim.toMap()).thenReturn(ImmutableMap.of());

    token =
        FleetEngineToken.builder()
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setTokenType(FleetEngineTokenType.SERVER)
            .setAuthorizationClaims(wildcardClaim)
            .build();

    // Make a second instance of an unsigned token
    token2 =
        token.toBuilder().setExpirationTimestamp(Date.from(Instant.EPOCH.plusSeconds(1))).build();
  }

  @Test
  public void signWildcardToken_returnsNewToken() throws SigningTokenException {
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    FleetEngineToken returnedToken = manager.signToken(signer, token);

    assertThat(returnedToken).isEqualTo(signedToken);
    // Throw exception on any interaction that wasn't stubbed
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void signWildcardToken_whenTokenNotExpired_returnsCachedToken()
      throws SigningTokenException {
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    when(expiryValidator.isTokenExpired(eq(signedToken), eq(EXPIRATION_WINDOW_DURATION)))
        .thenReturn(false);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    // Sets the new token
    FleetEngineToken returnedToken = manager.signToken(signer, token);
    // Since the token hasn't expired, the first token is returned
    FleetEngineToken returnedToken2 = manager.signToken(signer, token2);

    verify(signer, times(1)).sign(eq(token));
    verify(signer, times(0)).sign(eq(token2));
    assertThat(returnedToken).isEqualTo(signedToken);
    assertThat(returnedToken2).isEqualTo(signedToken);
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void signNonServerWildcardToken_whenTokenNotExpired_returnsCachedToken()
      throws SigningTokenException {
    token = token.toBuilder().setTokenType(FleetEngineTokenType.CONSUMER).build();
    token2 = token2.toBuilder().setTokenType(FleetEngineTokenType.CONSUMER).build();
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    when(expiryValidator.isTokenExpired(eq(signedToken), eq(EXPIRATION_WINDOW_DURATION)))
        .thenReturn(false);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    // Sets the new token
    FleetEngineToken returnedToken = manager.signToken(signer, token);
    // Since the token hasn't expired, the first token is returned
    FleetEngineToken returnedToken2 = manager.signToken(signer, token2);

    verify(signer, times(1)).sign(eq(token));
    verify(signer, times(0)).sign(eq(token2));
    assertThat(returnedToken).isEqualTo(signedToken);
    assertThat(returnedToken2).isEqualTo(signedToken);
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void signWildcardToken_whenTokensDifferentType_returnsNewToken()
      throws SigningTokenException {
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    token2 = token.toBuilder().setTokenType(FleetEngineTokenType.CONSUMER).build();
    FleetEngineToken signedToken2 = token2.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token2))).thenReturn(signedToken2);
    when(expiryValidator.isTokenExpired(eq(signedToken), eq(EXPIRATION_WINDOW_DURATION)))
        .thenReturn(false);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    // Sets the new token
    FleetEngineToken returnedToken = manager.signToken(signer, token);
    // Since the token hasn't expired, the first token is returned
    FleetEngineToken returnedToken2 = manager.signToken(signer, token2);

    verify(signer, times(1)).sign(eq(token));
    verify(signer, times(1)).sign(eq(token2));
    assertThat(returnedToken).isEqualTo(signedToken);
    assertThat(returnedToken2).isEqualTo(signedToken2);
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void signWildcardToken_whenTokenExpired_returnsNewToken() throws SigningTokenException {
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    FleetEngineToken signedToken2 = token2.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token2))).thenReturn(signedToken2);
    when(expiryValidator.isTokenExpired(eq(signedToken), eq(EXPIRATION_WINDOW_DURATION)))
        .thenReturn(true);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    // Sets the new token
    FleetEngineToken returnedToken = manager.signToken(signer, token);
    // Since the token hasn't expired, the first token is returned
    FleetEngineToken returnedToken2 = manager.signToken(signer, token2);

    verify(signer, times(1)).sign(eq(token));
    verify(signer, times(1)).sign(eq(token2));
    assertThat(returnedToken).isEqualTo(signedToken);
    assertThat(returnedToken2).isEqualTo(signedToken2);
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void signConsumerToken_returnsNewToken() throws SigningTokenException {
    token = token.toBuilder().setTokenType(FleetEngineTokenType.CONSUMER).build();
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    NaiveAuthStateManager manager =
        new NaiveAuthStateManager(FleetEngineTokenExpiryValidator.getInstance());

    FleetEngineToken returnedToken = manager.signToken(signer, token);

    assertThat(returnedToken).isEqualTo(signedToken);
    verify(signer, times(1)).sign(eq(token));
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  @Test
  public void findOrCreateDriverToken_returnsNewToken() throws SigningTokenException {
    token = token.toBuilder().setTokenType(FleetEngineTokenType.DRIVER).build();
    FleetEngineToken signedToken = token.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(token))).thenReturn(signedToken);
    NaiveAuthStateManager manager =
        new NaiveAuthStateManager(FleetEngineTokenExpiryValidator.getInstance());

    FleetEngineToken returnedToken = manager.signToken(signer, token);

    assertThat(returnedToken).isEqualTo(signedToken);
    verify(signer, times(1)).sign(eq(token));
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }

  /**
   * Thorough check that verifies that only server tokens are cached. No need go through every type
   * of token.
   */
  @Test
  public void signNonWildcardToken_neverCaches() throws SigningTokenException {
    FleetEngineToken wildcardToken = token.toBuilder().setJwt(TEST_JWT).build();
    FleetEngineToken signedWildcardToken = wildcardToken.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(wildcardToken))).thenReturn(signedWildcardToken);

    FleetEngineToken nonWildcardToken =
        token.toBuilder().setAuthorizationClaims(nonWildcardClaim).build();
    FleetEngineToken signedNonWildcardToken = nonWildcardToken.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(nonWildcardToken))).thenReturn(signedNonWildcardToken);

    FleetEngineToken consumerToken2 =
        token2.toBuilder().setTokenType(FleetEngineTokenType.CONSUMER).build();
    FleetEngineToken signedConsumerToken2 = consumerToken2.toBuilder().setJwt(TEST_JWT).build();
    when(signer.sign(eq(consumerToken2))).thenReturn(signedConsumerToken2);
    when(expiryValidator.isTokenExpired(eq(signedNonWildcardToken), eq(EXPIRATION_WINDOW_DURATION)))
        .thenReturn(false);
    NaiveAuthStateManager manager = new NaiveAuthStateManager(expiryValidator);

    // Sets the server token
    FleetEngineToken returnedServerToken = manager.signToken(signer, wildcardToken);
    // Sets the new consumerToken
    FleetEngineToken returnedConsumerToken = manager.signToken(signer, nonWildcardToken);
    // Since the consumerToken hasn't expired, the first consumerToken is returned
    FleetEngineToken returnedConsumerToken2 = manager.signToken(signer, consumerToken2);

    verify(signer, times(1)).sign(eq(wildcardToken));
    verify(signer, times(1)).sign(eq(nonWildcardToken));
    verify(signer, times(1)).sign(eq(consumerToken2));
    assertThat(returnedServerToken).isEqualTo(signedWildcardToken);
    assertThat(returnedConsumerToken).isEqualTo(signedNonWildcardToken);
    assertThat(returnedConsumerToken2).isEqualTo(signedConsumerToken2);
    verifyNoMoreInteractions(ignoreStubs(expiryValidator));
  }
}
