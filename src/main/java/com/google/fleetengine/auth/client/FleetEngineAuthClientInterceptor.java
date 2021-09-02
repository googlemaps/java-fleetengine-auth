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

import com.google.common.annotations.VisibleForTesting;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * Intercepts an outgoing gRPC request and attaches a valid Fleet Engine JWT to the header.
 *
 * <p>Works with generated gRPC stubby classes:
 *
 * <pre>{@code channel = ManagedChannelBuilder.forTarget(fleetEngineAddress)
 *   .intercept(FleetEngineAuthClientInterceptor#create(fleetEngineTokenProvider))
 *   .build();
 * VehicleServiceBlockingStub stub = VehicleServiceGrpc.newBlockingStub(channel);}</pre>
 */
public class FleetEngineAuthClientInterceptor implements ClientInterceptor {

  /** Authorization header name. */
  private static final Metadata.Key<String> AUTHORIZATION_HEADER =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  private final FleetEngineTokenProvider tokenProvider;

  /**
   * Creates a gRPC client interceptor that attaches tokens from {@code tokenProvider} to outgoing
   * gRPC requests.
   *
   * @param tokenProvider provides valid Fleet Engine JWTs for an outgoing gRPC request.
   */
  public static FleetEngineAuthClientInterceptor create(FleetEngineTokenProvider tokenProvider) {
    return new FleetEngineAuthClientInterceptor(tokenProvider);
  }

  /** Constructor. */
  private FleetEngineAuthClientInterceptor(FleetEngineTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  /**
   * Overrides {@link ClientInterceptor#interceptCall(MethodDescriptor, CallOptions, Channel)} and
   * attaches a Fleet Engine JWT to the header of the outgoing gRPC request.
   */
  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        addAuthorizationHeader(headers);
        super.start(responseListener, headers);
      }
    };
  }

  /** Adds the signed base64 encode JWT to the header. */
  @VisibleForTesting
  void addAuthorizationHeader(Metadata headers) {
    try {
      FleetEngineToken token = tokenProvider.getSignedToken();
      headers.put(AUTHORIZATION_HEADER, String.format("Bearer %s", token.jwt()));
    } catch (SigningTokenException e) {
      throw new WritingAuthorizationHeaderException("Exception while getting token.", e);
    }
  }
}
