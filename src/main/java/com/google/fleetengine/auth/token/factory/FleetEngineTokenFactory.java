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

package com.google.fleetengine.auth.token.factory;

import com.google.common.annotations.VisibleForTesting;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.ServerTokenClaims;
import com.google.fleetengine.auth.token.TripClaims;
import com.google.fleetengine.auth.token.VehicleClaims;
import java.sql.Date;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Provides unsigned Fleet Engine JWTs with an expiration time 60 minutes in the future. */
public final class FleetEngineTokenFactory implements TokenFactory {

  private final Clock clock;
  private final FleetEngineTokenFactorySettings settings;

  /** Duration of a Fleet Engine token until expiration. */
  @VisibleForTesting static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(60);

  /** Get settings that the factory was created with. */
  public FleetEngineTokenFactorySettings getSettings() {
    return settings;
  }

  /** Constructor. */
  public FleetEngineTokenFactory(FleetEngineTokenFactorySettings settings) {
    this(Clock.systemUTC(), settings);
  }

  /**
   * Constructor.
   *
   * @param clock reference clock for "issued at" and expiration time stamps
   */
  @VisibleForTesting
  FleetEngineTokenFactory(Clock clock, FleetEngineTokenFactorySettings settings) {
    this.clock = clock;
    this.settings = settings;
  }

  /** {@inheritDoc} */
  @Override
  public final FleetEngineToken createServerToken() {
    return createToken(FleetEngineTokenType.SERVER, ServerTokenClaims.create());
  }

  /** {@inheritDoc} */
  @Override
  public final FleetEngineToken createDriverToken(VehicleClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.DRIVER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public final FleetEngineToken createConsumerToken(TripClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.CONSUMER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public final FleetEngineToken createCustomToken(FleetEngineTokenClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.CUSTOM, claims);
  }

  private FleetEngineToken createToken(
      FleetEngineTokenType tokenType, FleetEngineTokenClaims claims) {
    Instant creationInstant = Instant.now(clock);
    Instant expirationInstant = creationInstant.plus(TOKEN_EXPIRATION);

    return FleetEngineToken.builder()
        .setTokenType(tokenType)
        .setCreationTimestamp(Date.from(creationInstant))
        .setExpirationTimestamp(Date.from(expirationInstant))
        .setAudience(settings.audience())
        .setAuthorizationClaims(claims)
        .build();
  }
}
