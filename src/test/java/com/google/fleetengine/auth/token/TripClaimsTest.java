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
public class TripClaimsTest {
  private static final String WILDCARD = "*";
  private static final String CLAIM_TRIP_ID = "tripid";
  private static final String TEST_TRIP_ID = "test_trip_id";

  @Test
  public void create_withWildcard() {
    TripClaims claims = TripClaims.create();

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_TRIP_ID, WILDCARD);
  }

  @Test
  public void create_withId() {
    TripClaims claims = TripClaims.create(TEST_TRIP_ID);

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_TRIP_ID, TEST_TRIP_ID);
  }
}
