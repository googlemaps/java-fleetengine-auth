package com.google.fleetengine.auth.token;

import com.google.common.collect.ImmutableMap;

/**
 * Special claim for fleet reader claims. Authorizes a token for use with all trips, tasks,
 * vehicles, and delivery vehicles.
 */
public class FleetReaderClaims implements FleetEngineTokenClaims {

  private static final String WILDCARD = "*";
  private static final FleetReaderClaims SINGLETON = new FleetReaderClaims();
  private final ImmutableMap<String, String> map;

  /** Creates a delivery fleet reader token claims object. */
  public static FleetReaderClaims create() {
    return SINGLETON;
  }

  private FleetReaderClaims() {
    // Load all claims with a wild card.
    map =
        ImmutableMap.of(
            TripClaims.CLAIM_TRIP_ID, WILDCARD,
            TaskClaims.CLAIM_TASK_ID, WILDCARD,
            TrackingClaims.CLAIM_TRACKING_ID, WILDCARD,
            VehicleClaims.CLAIM_VEHICLE_ID, WILDCARD,
            DeliveryVehicleClaims.CLAIM_DELIVERY_VEHICLE_ID, WILDCARD);
  }

  @Override
  public ImmutableMap<String, String> toMap() {
    return map;
  }

  @Override
  public boolean isWildcard() {
    return true;
  }
}
