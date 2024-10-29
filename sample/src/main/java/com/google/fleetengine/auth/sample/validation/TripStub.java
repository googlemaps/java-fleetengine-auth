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

package com.google.fleetengine.auth.sample.validation;

import com.google.maps.fleetengine.v1.CreateTripRequest;
import com.google.maps.fleetengine.v1.GetTripRequest;
import com.google.maps.fleetengine.v1.Trip;
import com.google.maps.fleetengine.v1.UpdateTripRequest;

/** Wraps {@link com.google.maps.fleetengine.v1.TripServiceClient} for testing purposes. */
public interface TripStub {

  /** see: {@link com.google.maps.fleetengine.v1.TripServiceClient#createTrip(CreateTripRequest)} */
  Trip createTrip(CreateTripRequest createTripRequest);

  /** see: {@link com.google.maps.fleetengine.v1.TripServiceClient#getTrip(GetTripRequest)} */
  Trip getTrip(GetTripRequest getTripRequest);

  /** see: {@link com.google.maps.fleetengine.v1.TripServiceClient#updateTrip(UpdateTripRequest)} */
  Trip updateTrip(UpdateTripRequest updateTripRequest);
}
