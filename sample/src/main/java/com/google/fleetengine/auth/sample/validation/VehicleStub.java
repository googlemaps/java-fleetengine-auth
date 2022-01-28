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

import google.maps.fleetengine.v1.CreateVehicleRequest;
import google.maps.fleetengine.v1.GetVehicleRequest;
import google.maps.fleetengine.v1.SearchVehiclesRequest;
import google.maps.fleetengine.v1.SearchVehiclesResponse;
import google.maps.fleetengine.v1.UpdateVehicleRequest;
import google.maps.fleetengine.v1.Vehicle;
import google.maps.fleetengine.v1.VehicleServiceClient;

/**
 * Wraps {@link google.maps.fleetengine.v1.VehicleServiceClient} for
 * testing purposes.
 */
public interface VehicleStub {
  /** see: {@link VehicleServiceClient#createVehicle(CreateVehicleRequest)} */
  Vehicle createVehicle(CreateVehicleRequest request);

  /** see: {@link VehicleServiceClient#getVehicle(GetVehicleRequest)} */
  Vehicle getVehicle(GetVehicleRequest request);

  /** see: {@link VehicleServiceClient#updateVehicle(UpdateVehicleRequest)} */
  Vehicle updateVehicle(UpdateVehicleRequest request);

  /** see: {@link VehicleServiceClient#searchVehicles(SearchVehiclesRequest)} */
  SearchVehiclesResponse searchVehicles(SearchVehiclesRequest request);
}
