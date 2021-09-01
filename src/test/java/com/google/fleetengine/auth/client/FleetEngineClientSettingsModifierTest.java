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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.fleetengine.auth.EmptyFleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class FleetEngineClientSettingsModifierTest {

  private FleetEngineTokenProvider fleetEngineTokenProvider;
  private FakeClientSettings.Builder clientSettingsBuilder;
  private FleetEngineToken fleetEngineToken;
  private HeaderProvider underlyingHeaderProvider;

  @Before
  public void setUp() {
    fleetEngineTokenProvider = mock(FleetEngineTokenProvider.class);
    underlyingHeaderProvider = mock(HeaderProvider.class);

    clientSettingsBuilder = mock(FakeClientSettings.Builder.class);
    when(clientSettingsBuilder.setHeaderProvider(any())).thenReturn(clientSettingsBuilder);
    when(clientSettingsBuilder.setCredentialsProvider(any())).thenReturn(clientSettingsBuilder);

    fleetEngineToken =
        FleetEngineToken.builder()
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .setTokenType(FleetEngineTokenType.SERVER)
            .build();
  }

  @Test
  public void updateBuilder_addsTokenProviderAsHeaderProvider() throws SigningTokenException {
    when(fleetEngineTokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    FleetEngineClientSettingsModifier<FakeClientSettings, FakeClientSettings.Builder> modifier =
        new FleetEngineClientSettingsModifier<>(fleetEngineTokenProvider);

    modifier.updateBuilder(clientSettingsBuilder);

    ArgumentCaptor<HeaderProvider> headerProviderArgumentCaptor =
        ArgumentCaptor.forClass(HeaderProvider.class);
    verify(clientSettingsBuilder, times(1))
        .setHeaderProvider(headerProviderArgumentCaptor.capture());
    FleetEngineAuthClientHeaderProvider headerProvider =
        (FleetEngineAuthClientHeaderProvider) headerProviderArgumentCaptor.getValue();
    assertThat(headerProvider.tokenProvider).isEqualTo(fleetEngineTokenProvider);
  }

  @Test
  public void updateBuilder_addsExistingHeaderProvider() throws SigningTokenException {
    when(fleetEngineTokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    when(clientSettingsBuilder.getHeaderProvider()).thenReturn(underlyingHeaderProvider);
    FleetEngineClientSettingsModifier<FakeClientSettings, FakeClientSettings.Builder> modifier =
        new FleetEngineClientSettingsModifier<>(fleetEngineTokenProvider);

    modifier.updateBuilder(clientSettingsBuilder);

    ArgumentCaptor<HeaderProvider> headerProviderArgumentCaptor =
        ArgumentCaptor.forClass(HeaderProvider.class);
    verify(clientSettingsBuilder, times(1))
        .setHeaderProvider(headerProviderArgumentCaptor.capture());
    FleetEngineAuthClientHeaderProvider headerProvider =
        (FleetEngineAuthClientHeaderProvider) headerProviderArgumentCaptor.getValue();
    assertThat(headerProvider.underlyingHeaderProvider).isEqualTo(underlyingHeaderProvider);
  }

  @Test
  public void updateBuilder_addsNullCredentialsProvider() throws SigningTokenException {
    when(fleetEngineTokenProvider.getSignedToken()).thenReturn(fleetEngineToken);
    FleetEngineClientSettingsModifier<FakeClientSettings, FakeClientSettings.Builder> modifier =
        new FleetEngineClientSettingsModifier<>(fleetEngineTokenProvider);

    modifier.updateBuilder(clientSettingsBuilder);

    ArgumentCaptor<CredentialsProvider> credentialsProviderArgumentCaptor =
        ArgumentCaptor.forClass(CredentialsProvider.class);
    verify(clientSettingsBuilder, times(1))
        .setCredentialsProvider(credentialsProviderArgumentCaptor.capture());
    FixedCredentialsProvider fixedCredentialsProvider =
        (FixedCredentialsProvider) credentialsProviderArgumentCaptor.getValue();
    assertThat(fixedCredentialsProvider.getCredentials()).isNull();
  }

  /** Mimics a generated ClientSettings class. */
  abstract static class FakeClientSettings extends ClientSettings<FakeClientSettings> {
    protected FakeClientSettings(Builder builder) throws IOException {
      super(builder);
    }

    public abstract static class Builder
        extends ClientSettings.Builder<FakeClientSettings, Builder> {}
  }
}
