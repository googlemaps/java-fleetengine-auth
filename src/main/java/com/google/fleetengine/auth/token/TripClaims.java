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

/** Custom authorization claims for trip endpoints. */
public class TripClaims implements FleetEngineTokenClaims {
  private static final String WILDCARD = "*";
  static final String CLAIM_TRIP_ID = "tripid";
  private final boolean isWildcard;

  private final ImmutableMap<String, String> map;

  /**
   * Creates a trip claim that works with any trip.
   *
   * @return custom auth claim
   */
  public static TripClaims create() {
    return new TripClaims(WILDCARD);
  }

  /**
   * Creates a trip claim with an id.
   *
   * @param tripId id of the trip id on the token scope
   * @return custom auth claim
   * @throws IllegalArgumentException when tripId is null or empty
   */
  public static TripClaims create(String tripId) {
    if (Strings.isNullOrEmpty(tripId)) {
      throw new IllegalArgumentException("tripId must have a value");
    }
    return new TripClaims(tripId);
  }

  /** Indicates whether the claim restricts access to just one trip. */
  @Override
  public boolean isWildcard() {
    return isWildcard;
  }

  private TripClaims(String tripId) {
    map = ImmutableMap.of(CLAIM_TRIP_ID, tripId);
    isWildcard = WILDCARD.equals(tripId);
  }

  /** {@inheritDoc} */
  @Override
  public ImmutableMap<String, String> toMap() {
    return map;
  }
}
