package com.google.fleetengine.auth.token;

import com.google.common.collect.ImmutableMap;

/** Special claim for server token that works with all end points. Authorizes a token for use with
 * all tasks and delivery vehicles.
 */
public class DeliveryServerTokenClaims implements FleetEngineTokenClaims {

  private static final String WILDCARD = "*";
  private static final DeliveryServerTokenClaims SINGLETON = new DeliveryServerTokenClaims();
  private final ImmutableMap<String, String> map;

  /** Creates a server token claims object. */
  public static DeliveryServerTokenClaims create() {
    return SINGLETON;
  }

  private DeliveryServerTokenClaims() {
    // Load all claims with a wild card.
    map =
        ImmutableMap.of(
            TaskClaims.CLAIM_TASK_ID, WILDCARD,
            TrackingClaims.CLAIM_TRACKING_ID, WILDCARD,
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
