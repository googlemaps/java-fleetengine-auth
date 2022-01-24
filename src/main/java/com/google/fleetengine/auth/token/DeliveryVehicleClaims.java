package com.google.fleetengine.auth.token;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/** Custom authorization claims for delivery vehicle endpoint. */
public class DeliveryVehicleClaims implements FleetEngineTokenClaims {
  private static final String WILDCARD = "*";
  static final String CLAIM_DELIVERY_VEHICLE_ID = "deliveryvehicleid";
  private final ImmutableMap<String, String> map;
  private final boolean isWildcardClaim;

  /**
   * Creates a delivery vehicles claim that works with any delivery vehicle.
   *
   * @return custom auth claim
   */
  public static DeliveryVehicleClaims create() {
    return new DeliveryVehicleClaims(WILDCARD, true);
  }

  /**
   * Creates a delivery vehicles claim with an id.
   *
   * @param deliveryVehicleId id of the delivery vehicle on the token scope
   * @return custom auth claim
   * @throws IllegalArgumentException when deliveryVehicleId is null or empty
   */
  public static DeliveryVehicleClaims create(String deliveryVehicleId) {
    if (Strings.isNullOrEmpty(deliveryVehicleId)) {
      throw new IllegalArgumentException("deliveryVehicleId must have a value");
    }
    return new DeliveryVehicleClaims(deliveryVehicleId, false);
  }

  private DeliveryVehicleClaims(String deliveryVehicleId, boolean isWildcard) {
    map = ImmutableMap.of(CLAIM_DELIVERY_VEHICLE_ID, deliveryVehicleId);
    isWildcardClaim = isWildcard;
  }

  /** {@inheritDoc} */
  @Override
  public ImmutableMap<String, String> toMap() {
    return map;
  }

  @Override
  public boolean isWildcard() {
    return isWildcardClaim;
  }
}
