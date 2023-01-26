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

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Map;

/**
 * Updates a fleet engine {@link ClientSettings.Builder} so that a valid Fleet Engine JWT is
 * attached to any outgoing request made by the subsequent client.
 *
 * <p>Works with generated GAPIC classes. Usage:
 *
 * <pre>{@code
 * FleetEngineClientSettingsModifier<VehicleServiceSettings, VehicleServiceSettings.Builder>
 *   modifier =
 *     // AuthTokenMinter implements token provider by providing the server token
 *     new FleetEngineClientSettingsModifier<>(tokenProvider);
 *
 * VehicleServiceSettings.Builder builder = VehicleServiceSettings.newBuilder();
 * VehicleServiceSettings settings = modifier.updateBuilder(builder).build();
 * VehicleServiceClient client = VehicleServiceClient.create(settings);
 * }</pre>
 */
public class FleetEngineClientSettingsModifier<
    SettingsT extends ClientSettings<SettingsT>, B extends ClientSettings.Builder<SettingsT, B>> {

  private final FleetEngineTokenProvider tokenProvider;

  /**
   * Constructor.
   *
   * @param tokenProvider provides valid Fleet Engine JWTs for an outgoing request.
   */
  public FleetEngineClientSettingsModifier(FleetEngineTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  public B updateBuilder(B builder) {
    if (!(builder.getTransportChannelProvider() instanceof InstantiatingGrpcChannelProvider)) {
      throw new IllegalArgumentException(
          "Transport channel provider must be of type InstantiatingGrpcChannelProvider");
    }

    Map<String, String> headers = new HashMap<>();
    if (builder.getHeaderProvider() != null && builder.getHeaderProvider().getHeaders() != null) {
      headers.putAll(builder.getHeaderProvider().getHeaders());
    }
    String libraryUserAgent = "java-fleetengine-auth/";
    if (this.getClass().getPackage().getImplementationVersion() != null) {
      libraryUserAgent = libraryUserAgent + this.getClass().getPackage().getImplementationVersion();
    }
    String userAgent = headers.get("user-agent");
    if (Strings.isNullOrEmpty(userAgent)) {
      userAgent = libraryUserAgent;
    } else {
      userAgent = userAgent + " " + libraryUserAgent;
    }
    headers.put("user-agent", userAgent);

    // Reuse existing channel provider
    InstantiatingGrpcChannelProvider provider =
        (InstantiatingGrpcChannelProvider) builder.getTransportChannelProvider();
    return builder
        .setCredentialsProvider(FixedCredentialsProvider.create(null))
        .setHeaderProvider(FixedHeaderProvider.create(headers))
        .setTransportChannelProvider(
            provider.toBuilder()
                .setInterceptorProvider(
                    () ->
                        Lists.newArrayList(
                            FleetEngineAuthClientInterceptor.create(this.tokenProvider)))
                .build());
  }
}
