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

import com.google.maps.fleetengine.v1.CreateVehicleRequest;
import com.google.maps.fleetengine.v1.GetVehicleRequest;
import com.google.maps.fleetengine.v1.SearchVehiclesRequest;
import com.google.maps.fleetengine.v1.SearchVehiclesResponse;
import com.google.maps.fleetengine.v1.UpdateVehicleRequest;
import com.google.maps.fleetengine.v1.Vehicle;

/** Wraps {@link com.google.maps.fleetengine.v1.VehicleServiceClient} for testing purposes. */
public interface VehicleStub {
  /**
   * see: {@link
   * com.google.maps.fleetengine.v1.VehicleServiceClient#createVehicle(CreateVehicleRequest)}
   */
  Vehicle createVehicle(CreateVehicleRequest request);

  /**
   * see: {@link com.google.maps.fleetengine.v1.VehicleServiceClient#getVehicle(GetVehicleRequest)}
   */
  Vehicle getVehicle(GetVehicleRequest request);

  /**
   * see: {@link
   * com.google.maps.fleetengine.v1.VehicleServiceClient#updateVehicle(UpdateVehicleRequest)}
   */
  Vehicle updateVehicle(UpdateVehicleRequest request);

  /**
   * see: {@link
   * com.google.maps.fleetengine.v1.VehicleServiceClient#searchVehicles(SearchVehiclesRequest)}
   */
  SearchVehiclesResponse searchVehicles(SearchVehiclesRequest request);
}
