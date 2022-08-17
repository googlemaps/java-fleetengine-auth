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

import com.google.fleetengine.auth.token.FleetEngineToken;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import java.util.concurrent.Executor;

/** Adds an athorization header containing a Fleet Engine JWT to the request metadata. */
public class FleetEngineAuthCallCredentials extends CallCredentials {

  /** Authorization header name. */
  private static final Metadata.Key<String> AUTHORIZATION_HEADER =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  private final FleetEngineTokenProvider tokenProvider;

  public static FleetEngineAuthCallCredentials create(FleetEngineTokenProvider tokenProvider) {
    return new FleetEngineAuthCallCredentials(tokenProvider);
  }

  private FleetEngineAuthCallCredentials(FleetEngineTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void applyRequestMetadata(
      RequestInfo requestInfo, Executor appExecutor, CallCredentials.MetadataApplier applier) {
    Metadata headers = new Metadata();
    try {
      FleetEngineToken token = tokenProvider.getSignedToken();
      headers.put(AUTHORIZATION_HEADER, String.format("Bearer %s", token.jwt()));
    } catch (Exception e) {
      applier.fail(Status.UNAUTHENTICATED.withDescription("Unable to create token").withCause(e));
      return;
    }

    applier.apply(headers);
  }

  @Override
  public void thisUsesUnstableApi() {}
}
