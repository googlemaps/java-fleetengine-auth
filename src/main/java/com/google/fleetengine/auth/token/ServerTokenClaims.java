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

import com.google.common.collect.ImmutableMap;

/** Special claim for server token that works with all end points. Always a wildcard. */
public class ServerTokenClaims implements FleetEngineTokenClaims {

  private static final String WILDCARD = "*";
  private static final ServerTokenClaims SINGLETON = new ServerTokenClaims();
  private final ImmutableMap<String, String> map;

  /** Creates a server token claims object. */
  public static ServerTokenClaims create() {
    return SINGLETON;
  }

  private ServerTokenClaims() {
    // Load all claims with a wild card.
    map =
        ImmutableMap.of(
            TripClaims.CLAIM_TRIP_ID, WILDCARD,
            VehicleClaims.CLAIM_VEHICLE_ID, WILDCARD);
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
