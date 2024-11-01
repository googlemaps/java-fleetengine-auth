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
import com.google.maps.fleetengine.v1.TerminalLocation;
import com.google.maps.fleetengine.v1.Trip;
import com.google.maps.fleetengine.v1.TripStatus;
import com.google.maps.fleetengine.v1.TripType;
import com.google.maps.fleetengine.v1.UpdateTripRequest;
import com.google.protobuf.FieldMask;
import com.google.type.LatLng;

/** Hardcoded commands against the trip service. */
public class TripCommands {

  private static final int NUM_PASSENGERS = 1;
  private static final LatLng PICKUP_POINT = ProtosUtil.getLatLng(37.419646, -122.073885);
  private static final LatLng DROPOFF_POINT = ProtosUtil.getLatLng(37.419647, -122.073886);

  private final TripStub tripStub;
  private final String providerId;

  /** Constructor. */
  public TripCommands(TripStub tripStub, String providerId) {
    this.tripStub = tripStub;
    this.providerId = providerId;
  }

  /** Create trip with a given id and hardcoded values. */
  public Trip createTrip(String tripId) {
    Trip trip =
        Trip.newBuilder()
            .setName(getTripName(tripId))
            .setTripType(TripType.EXCLUSIVE)
            .setNumberOfPassengers(NUM_PASSENGERS)
            .setPickupPoint(TerminalLocation.newBuilder().setPoint(PICKUP_POINT).build())
            .setDropoffPoint(TerminalLocation.newBuilder().setPoint(DROPOFF_POINT).build())
            .build();

    CreateTripRequest createTripRequest =
        CreateTripRequest.newBuilder()
            .setTrip(trip)
            .setTripId(tripId)
            .setParent(ScriptUtils.getProviderName(providerId))
            .build();
    return tripStub.createTrip(createTripRequest);
  }

  /** Get existing trip. */
  public Trip getTrip(String tripId) {
    GetTripRequest getTripRequest =
        GetTripRequest.newBuilder().setName(getTripName(tripId)).build();
    return tripStub.getTrip(getTripRequest);
  }

  /** Update trip status to complete. */
  public Trip completeTrip(String tripId) {
    UpdateTripRequest updateTripRequest =
        UpdateTripRequest.newBuilder()
            .setName(getTripName(tripId))
            .setUpdateMask(FieldMask.newBuilder().addPaths("trip_status").build())
            .setTrip(
                Trip.newBuilder()
                    .setName(getTripName(tripId))
                    .setTripStatus(TripStatus.COMPLETE)
                    .build())
            .build();
    return tripStub.updateTrip(updateTripRequest);
  }

  /** Trip names must be in the given format. */
  private String getTripName(String tripId) {
    return String.format("providers/%s/trips/%s", providerId, tripId);
  }
}
