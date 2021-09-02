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

package com.google.fleetengine.auth.client;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.HeaderProvider;
import com.google.fleetengine.auth.EmptyFleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FleetEngineAuthClientHeaderProviderTest {

  private static final String AUTHORIZATION_HEADER_NAME = "authorization";
  private static final String FAKE_JWT = "fake.jwt.token";
  private static final String FAKE_AUTHORIZATION_HEADER = String.format("Bearer %s", FAKE_JWT);
  private static final String FAKE_UNDERLYING_HEADER_NAME_1 = "some_header_name_1";
  private static final String FAKE_UNDERLYING_HEADER_VALUE_1 = "some_header_value_1";
  private static final String FAKE_UNDERLYING_HEADER_NAME_2 = "some_header_name_2";
  private static final String FAKE_UNDERLYING_HEADER_VALUE_2 = "some_header_value_2";

  private FleetEngineTokenProvider tokenProvider;
  private FleetEngineToken fleetEngineToken;
  private HeaderProvider underlyingHeaderProvider;

  @Before
  public void setUp() {
    tokenProvider = mock(FleetEngineTokenProvider.class);
    underlyingHeaderProvider = mock(HeaderProvider.class);

    fleetEngineToken =
        FleetEngineToken.builder()
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .setTokenType(FleetEngineTokenType.SERVER)
            .build();
  }

  @Test
  public void getHeaders_returnsAuthorizationHeader() throws SigningTokenException {
    fleetEngineToken = fleetEngineToken.toBuilder().setJwt(FAKE_JWT).build();
    when(tokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    FleetEngineAuthClientHeaderProvider provider =
        FleetEngineAuthClientHeaderProvider.create(tokenProvider);

    Map<String, String> headers = provider.getHeaders();

    assertThat(headers).containsEntry(AUTHORIZATION_HEADER_NAME, FAKE_AUTHORIZATION_HEADER);
  }

  @Test
  public void getHeaders_whenUnderlyingProviderNotNull_returnsAllHeadersFromProvider()
      throws SigningTokenException {
    fleetEngineToken = fleetEngineToken.toBuilder().setJwt(FAKE_JWT).build();
    when(tokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    Map<String, String> underlyingHeaders = new HashMap<>();
    underlyingHeaders.put(FAKE_UNDERLYING_HEADER_NAME_1, FAKE_UNDERLYING_HEADER_VALUE_1);
    underlyingHeaders.put(FAKE_UNDERLYING_HEADER_NAME_2, FAKE_UNDERLYING_HEADER_VALUE_2);
    when(underlyingHeaderProvider.getHeaders()).thenReturn(underlyingHeaders);
    FleetEngineAuthClientHeaderProvider provider =
        FleetEngineAuthClientHeaderProvider.create(tokenProvider, underlyingHeaderProvider);

    Map<String, String> headers = provider.getHeaders();

    assertThat(headers)
        .containsEntry(FAKE_UNDERLYING_HEADER_NAME_1, FAKE_UNDERLYING_HEADER_VALUE_1);
    assertThat(headers)
        .containsEntry(FAKE_UNDERLYING_HEADER_NAME_2, FAKE_UNDERLYING_HEADER_VALUE_2);
    assertThat(headers).containsEntry(AUTHORIZATION_HEADER_NAME, FAKE_AUTHORIZATION_HEADER);
  }

  @Test
  public void getHeaders_whenUnderlyingProviderReturnsAuthHeader_returnsFleetEngineAuthHeader()
      throws SigningTokenException {
    fleetEngineToken = fleetEngineToken.toBuilder().setJwt(FAKE_JWT).build();
    when(tokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    Map<String, String> underlyingHeaders = new HashMap<>();
    underlyingHeaders.put(AUTHORIZATION_HEADER_NAME, FAKE_UNDERLYING_HEADER_VALUE_1);
    underlyingHeaders.put(FAKE_UNDERLYING_HEADER_NAME_1, FAKE_UNDERLYING_HEADER_VALUE_1);
    when(underlyingHeaderProvider.getHeaders()).thenReturn(underlyingHeaders);
    FleetEngineAuthClientHeaderProvider provider =
        FleetEngineAuthClientHeaderProvider.create(tokenProvider, underlyingHeaderProvider);

    Map<String, String> headers = provider.getHeaders();

    assertThat(headers).containsEntry(AUTHORIZATION_HEADER_NAME, FAKE_AUTHORIZATION_HEADER);
    assertThat(headers)
        .containsEntry(FAKE_UNDERLYING_HEADER_NAME_1, FAKE_UNDERLYING_HEADER_VALUE_1);
  }
}
