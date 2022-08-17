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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.EmptyFleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import java.time.Instant;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class FleetEngineAuthCallCredentialsTest {

  private static final Metadata.Key<String> AUTHORIZATION_HEADER =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
  private static final String FAKE_JWT = "fake.jwt.token";
  private static final String FAKE_AUTHORIZATION_HEADER = String.format("Bearer %s", FAKE_JWT);
  @Mock private FleetEngineTokenProvider tokenProvider;
  @Mock private CallCredentials.MetadataApplier applier;
  private FleetEngineToken fleetEngineToken;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    fleetEngineToken =
        FleetEngineToken.builder()
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .setTokenType(FleetEngineTokenType.SERVER)
            .build();
  }

  @Test
  public void applyRequestMetadata_addsHeaderCorrectly() throws SigningTokenException {
    fleetEngineToken = fleetEngineToken.toBuilder().setJwt(FAKE_JWT).build();
    when(tokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    FleetEngineAuthCallCredentials callCredentials =
        FleetEngineAuthCallCredentials.create(this.tokenProvider);

    callCredentials.applyRequestMetadata(null, null, applier);

    ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
    verify(applier).apply(metadataCaptor.capture());
    assertThat(metadataCaptor.getValue().get(AUTHORIZATION_HEADER))
        .isEqualTo(FAKE_AUTHORIZATION_HEADER);
  }

  @Test
  public void applyRequestMetadata_onException_callsDail() throws SigningTokenException {
    fleetEngineToken = fleetEngineToken.toBuilder().setJwt(FAKE_JWT).build();
    when(tokenProvider.getSignedToken()).thenThrow(mock(SigningTokenException.class));
    FleetEngineAuthCallCredentials callCredentials =
        FleetEngineAuthCallCredentials.create(this.tokenProvider);

    callCredentials.applyRequestMetadata(null, null, applier);
    verify(applier).fail(any());
  }
}
