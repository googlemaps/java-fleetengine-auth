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

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;

/**
 * Intercepts an outgoing gRPC request and attaches a valid Fleet Engine JWT to the header by using
 * {@link FleetEngineAuthCallCredentials}.
 *
 * <p>Works with generated gRPC stubby classes:
 *
 * <pre>{@code channel = ManagedChannelBuilder.forTarget(fleetEngineAddress)
 *   .intercept(FleetEngineAuthClientInterceptor#create(fleetEngineTokenProvider))
 *   .build();
 * VehicleServiceBlockingStub stub = VehicleServiceGrpc.newBlockingStub(channel);}</pre>
 */
public class FleetEngineAuthClientInterceptor implements ClientInterceptor {

  private final FleetEngineAuthCallCredentials callCredentials;

  /**
   * Creates a gRPC client interceptor that attaches tokens from {@code tokenProvider} to outgoing
   * gRPC requests.
   *
   * @param tokenProvider provides valid Fleet Engine JWTs for an outgoing gRPC request.
   */
  public static FleetEngineAuthClientInterceptor create(FleetEngineTokenProvider tokenProvider) {
    return new FleetEngineAuthClientInterceptor(
        FleetEngineAuthCallCredentials.create(tokenProvider));
  }

  /** Constructor. */
  private FleetEngineAuthClientInterceptor(FleetEngineAuthCallCredentials callCredentials) {
    this.callCredentials = callCredentials;
  }

  /**
   * Returns a new call with the {@link FleetEngineAuthCallCredentials} added to the {@code
   * callOptions}.
   */
  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    return next.newCall(method, callOptions.withCallCredentials(this.callCredentials));
  }
}
