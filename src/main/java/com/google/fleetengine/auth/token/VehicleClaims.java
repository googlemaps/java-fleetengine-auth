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

package com.google.fleetengine.auth.token;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/** Custom authorization claims for vehicle endpoints. */
public class VehicleClaims implements FleetEngineTokenClaims {
  private static final String WILDCARD = "*";
  static final String CLAIM_VEHICLE_ID = "vehicleid";
  private final boolean isWildcard;

  private final ImmutableMap<String, String> map;

  /**
   * Creates a vehicle claim that works with any vehicle.
   *
   * @return custom auth claim
   */
  public static VehicleClaims create() {
    return new VehicleClaims(WILDCARD);
  }

  /** Indicates whether the claim restricts access to just one vehicle. */
  @Override
  public boolean isWildcard() {
    return isWildcard;
  }

  /**
   * Creates a vehicle claim with an id.
   *
   * @param vehicleId id of the vehicle id on the token scope
   * @return custom auth claim
   * @throws IllegalArgumentException when vehicleId is null or empty
   */
  public static VehicleClaims create(String vehicleId) {
    if (Strings.isNullOrEmpty(vehicleId)) {
      throw new IllegalArgumentException("vehicleId must have a value");
    }
    return new VehicleClaims(vehicleId);
  }

  private VehicleClaims(String vehicleId) {
    map = ImmutableMap.of(CLAIM_VEHICLE_ID, vehicleId);
    isWildcard = WILDCARD.equals(vehicleId);
  }

  /** {@inheritDoc} */
  @Override
  public ImmutableMap<String, String> toMap() {
    return map;
  }
}
