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

package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.sample.validation.TripStub;
import com.google.maps.fleetengine.v1.CreateTripRequest;
import com.google.maps.fleetengine.v1.GetTripRequest;
import com.google.maps.fleetengine.v1.Trip;
import com.google.maps.fleetengine.v1.TripServiceClient;
import com.google.maps.fleetengine.v1.UpdateTripRequest;

public class TripStubClient implements TripStub {

  private final TripServiceClient client;

  /** Constructor. */
  public TripStubClient(TripServiceClient client) {
    this.client = client;
  }

  /** {@inheritDoc} */
  @Override
  public Trip createTrip(CreateTripRequest request) {
    return client.createTrip(request);
  }

  /** {@inheritDoc} */
  @Override
  public Trip getTrip(GetTripRequest request) {
    return client.getTrip(request);
  }

  /** {@inheritDoc} */
  @Override
  public Trip updateTrip(UpdateTripRequest request) {
    return client.updateTrip(request);
  }
}
