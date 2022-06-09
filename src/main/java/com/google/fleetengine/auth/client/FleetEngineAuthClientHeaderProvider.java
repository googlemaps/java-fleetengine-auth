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

import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.util.Map;

/**
 * Provides a valid Fleet Engine JWT on the header of an outgoing gRPC request.
 *
 * <p>A secondary header provider can be added that provides additional headers. In the event that
 * the other header provider also contains an authorization header, that header is overwritten.
 *
 * <p>Works with generated GAPIC classes. For correct usage, see {@link
 * FleetEngineClientSettingsModifier}.
 */
@Deprecated
public class FleetEngineAuthClientHeaderProvider implements HeaderProvider {
  private static final String AUTHORIZATION_HEADER_NAME = "authorization";

  @VisibleForTesting final FleetEngineTokenProvider tokenProvider;

  @VisibleForTesting final HeaderProvider underlyingHeaderProvider;

  /**
   * Creates {@link FleetEngineAuthClientHeaderProvider}.
   *
   * @param tokenProvider provides fleet engine tokens
   * @return constructed header provider
   */
  public static FleetEngineAuthClientHeaderProvider create(FleetEngineTokenProvider tokenProvider) {
    return new FleetEngineAuthClientHeaderProvider(tokenProvider, FixedHeaderProvider.create());
  }

  /**
   * Creates {@link FleetEngineAuthClientHeaderProvider}.
   *
   * @param tokenProvider provides fleet engine tokens
   * @param underlyingHeaderProvider provides additional headers to the outbound request.
   * @return constructed header provider
   */
  public static FleetEngineAuthClientHeaderProvider create(
      FleetEngineTokenProvider tokenProvider, HeaderProvider underlyingHeaderProvider) {
    return new FleetEngineAuthClientHeaderProvider(tokenProvider, underlyingHeaderProvider);
  }

  private FleetEngineAuthClientHeaderProvider(
      FleetEngineTokenProvider tokenProvider, HeaderProvider underlyingHeaderProvider) {
    this.underlyingHeaderProvider = underlyingHeaderProvider;
    this.tokenProvider = tokenProvider;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> getHeaders() {
    try {
      // If the underlying provider has an authorization header, skip.
      Map<String, String> underlyingMap =
          Maps.filterEntries(
              underlyingHeaderProvider.getHeaders(),
              entry -> !AUTHORIZATION_HEADER_NAME.equals(entry.getKey()));
      return new ImmutableMap.Builder<String, String>()
          .putAll(underlyingMap)
          .put(
              AUTHORIZATION_HEADER_NAME,
              String.format("Bearer %s", tokenProvider.getSignedToken().jwt()))
          .buildOrThrow();
    } catch (SigningTokenException e) {
      throw new WritingAuthorizationHeaderException("Exception while getting server token.", e);
    }
  }
}
