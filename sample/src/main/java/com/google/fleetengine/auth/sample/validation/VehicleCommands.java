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

import com.google.protobuf.FieldMask;
import com.google.type.LatLng;
import google.maps.fleetengine.v1.CreateVehicleRequest;
import google.maps.fleetengine.v1.GetVehicleRequest;
import google.maps.fleetengine.v1.SearchVehiclesRequest;
import google.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrder;
import google.maps.fleetengine.v1.SearchVehiclesResponse;
import google.maps.fleetengine.v1.TerminalLocation;
import google.maps.fleetengine.v1.TripType;
import google.maps.fleetengine.v1.UpdateVehicleRequest;
import google.maps.fleetengine.v1.Vehicle;
import google.maps.fleetengine.v1.Vehicle.VehicleType;
import google.maps.fleetengine.v1.Vehicle.VehicleType.Category;
import google.maps.fleetengine.v1.VehicleLocation;
import google.maps.fleetengine.v1.VehicleMatch;
import google.maps.fleetengine.v1.VehicleState;
import java.util.List;

/** Runs a series of hardcoded examples against the Vehicle Service. */
public class VehicleCommands {

  private static final int MAX_CAPACITY = 4;
  private static final int SPEED_KMS = 30 * 1000;
  private static final double SPEED_ACCURACY = 1.0;
  private static final LatLng LOCATION = ProtosUtil.getLatLng(37.419645, -122.073884);

  private final VehicleStub vehicleStub;
  private final String providerId;

  /** Constructor. */
  public VehicleCommands(VehicleStub vehicleStub, String providerId) {
    this.vehicleStub = vehicleStub;
    this.providerId = providerId;
  }

  /** Create vehicle with a given id and hardcoded values. */
  public Vehicle createVehicle(String vehicleId) {
    Vehicle vehicle =
        Vehicle.newBuilder()
            .setName(getVehicleName(vehicleId))
            .setMaximumCapacity(MAX_CAPACITY)
            .setVehicleState(VehicleState.ONLINE)
            .setVehicleType(VehicleType.newBuilder().setCategory(Category.AUTO))
            .addSupportedTripTypes(TripType.EXCLUSIVE)
            .setLastLocation(getVehicleLocation())
            .build();

    CreateVehicleRequest request =
        CreateVehicleRequest.newBuilder()
            .setVehicleId(vehicleId)
            .setVehicle(vehicle)
            .setParent(ScriptUtils.getProviderName(providerId))
            .build();

    return vehicleStub.createVehicle(request);
  }

  /** Get an existing vehicle. */
  public Vehicle getVehicle(String vehicleId) {
    GetVehicleRequest request =
        GetVehicleRequest.newBuilder().setName(getVehicleName(vehicleId)).build();
    return vehicleStub.getVehicle(request);
  }

  /** Brings a vehicle offline. */
  public Vehicle bringOffline(String vehicleId) {
    UpdateVehicleRequest request =
        UpdateVehicleRequest.newBuilder()
            .setName(getVehicleName(vehicleId))
            // Specifies which values are changing on the vehicle
            .setUpdateMask(FieldMask.newBuilder().addPaths("vehicle_state"))
            .setVehicle(Vehicle.newBuilder().setVehicleState(VehicleState.OFFLINE).build())
            .build();
    return vehicleStub.updateVehicle(request);
  }

  /** Search for vehicles around a trip pick up point. */
  public List<VehicleMatch> searchVehicles(String tripId) {
    SearchVehiclesRequest request =
        SearchVehiclesRequest.newBuilder()
            .setParent(ScriptUtils.getProviderName(providerId))
            .setTripId(tripId)
            .setPickupPoint(TerminalLocation.newBuilder().setPoint(LOCATION).build())
            .setMinimumCapacity(1)
            .addTripTypes(TripType.EXCLUSIVE)
            .setPickupRadiusMeters(400)
            .addVehicleTypes(VehicleType.newBuilder().setCategory(Category.AUTO))
            .setCount(5)
            .setOrderBy(VehicleMatchOrder.PICKUP_POINT_DISTANCE)
            .build();
    SearchVehiclesResponse response = vehicleStub.searchVehicles(request);
    return response.getMatchesList();
  }

  private static VehicleLocation getVehicleLocation() {
    return VehicleLocation.newBuilder()
        .setLocation(LOCATION)
        .setHeading(ProtosUtil.getIntValue(0))
        .setSpeed(ProtosUtil.getDoubleValue(SPEED_KMS))
        .setAltitude(ProtosUtil.getDoubleValue(0))
        .setSpeedAccuracy(ProtosUtil.getDoubleValue(SPEED_ACCURACY))
        .build();
  }

  /** Vehicle names must be in the given format. */
  private String getVehicleName(String vehicleId) {
    return String.format("providers/%s/vehicles/%s", providerId, vehicleId);
  }
}
