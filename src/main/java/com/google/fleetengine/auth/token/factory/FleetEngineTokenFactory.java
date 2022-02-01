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
import com.google.common.collect.ImmutableMap;
import com.google.fleetengine.auth.token.DeliveryFleetReaderClaims;
import com.google.fleetengine.auth.token.DeliveryServerTokenClaims;
import com.google.fleetengine.auth.token.DeliveryVehicleClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenClaims;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.ServerTokenClaims;
import com.google.fleetengine.auth.token.TaskClaims;
import com.google.fleetengine.auth.token.TrackingClaims;
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
  public FleetEngineToken createServerToken() {
    return createToken(FleetEngineTokenType.SERVER, ServerTokenClaims.create());
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createDriverToken(VehicleClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.DRIVER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createConsumerToken(TripClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.CONSUMER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createDeliveryServerToken() {
    return createToken(FleetEngineTokenType.DELIVERY_SERVER, DeliveryServerTokenClaims.create());
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createDeliveryConsumerToken(TaskClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.DELIVERY_CONSUMER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createDeliveryConsumerToken(TrackingClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.DELIVERY_CONSUMER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createUntrustedDeliveryDriverToken(DeliveryVehicleClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.UNTRUSTED_DELIVERY_DRIVER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createTrustedDeliveryDriverToken(DeliveryVehicleClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.TRUSTED_DELIVERY_DRIVER, claims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createTrustedDeliveryDriverToken(
      DeliveryVehicleClaims vehicleClaims, TaskClaims taskClaims) {
    Objects.requireNonNull(vehicleClaims);
    Objects.requireNonNull(taskClaims);
    return createToken(FleetEngineTokenType.TRUSTED_DELIVERY_DRIVER, vehicleClaims, taskClaims);
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createDeliveryFleetReaderToken() {
    return createToken(
        FleetEngineTokenType.DELIVERY_FLEET_READER, DeliveryFleetReaderClaims.create());
  }

  /** {@inheritDoc} */
  @Override
  public FleetEngineToken createCustomToken(FleetEngineTokenClaims claims) {
    Objects.requireNonNull(claims);
    return createToken(FleetEngineTokenType.CUSTOM, claims);
  }

  private FleetEngineToken createToken(
      FleetEngineTokenType tokenType, FleetEngineTokenClaims... claims) {
    Instant creationInstant = Instant.now(clock);
    Instant expirationInstant = creationInstant.plus(TOKEN_EXPIRATION);

    return FleetEngineToken.builder()
        .setTokenType(tokenType)
        .setCreationTimestamp(Date.from(creationInstant))
        .setExpirationTimestamp(Date.from(expirationInstant))
        .setAudience(settings.audience())
        .setAuthorizationClaims(mergeClaims(claims))
        .build();
  }

  private FleetEngineTokenClaims mergeClaims(FleetEngineTokenClaims[] claims) {
    if (claims.length == 1) {
      return claims[0];
    }

    boolean isWildcard = true;
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

    for (FleetEngineTokenClaims c : claims) {
      builder.putAll(c.toMap());

      // Flag as wildcard if all claims are wildcards
      isWildcard = isWildcard && c.isWildcard();
    }
    ImmutableMap<String, String> map = builder.buildOrThrow();
    return new MergedClaims(map, isWildcard);
  }

  private static class MergedClaims implements FleetEngineTokenClaims {
    private final ImmutableMap<String, String> map;
    private final boolean isWildcard;

    public MergedClaims(ImmutableMap<String, String> map, boolean isWildcard) {
      this.map = map;
      this.isWildcard = isWildcard;
    }

    @Override
    public ImmutableMap<String, String> toMap() {
      return map;
    }

    @Override
    public boolean isWildcard() {
      return isWildcard;
    }
  };
}
