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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VehicleClaimsTest {
  private static final String WILDCARD = "*";
  private static final String CLAIM_VEHICLE_ID = "vehicleid";
  private static final String TEST_VEHICLE_ID = "test_vehicle_id";

  @Test
  public void create_withWildcard() {
    VehicleClaims claims = VehicleClaims.create();

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_VEHICLE_ID, WILDCARD);
    assertThat(claims.isWildcard()).isTrue();
  }

  @Test
  public void create_withId() {
    VehicleClaims claims = VehicleClaims.create(TEST_VEHICLE_ID);

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_VEHICLE_ID, TEST_VEHICLE_ID);
    assertThat(claims.isWildcard()).isFalse();
  }
}
