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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import google.maps.fleetengine.v1.CreateVehicleRequest;
import google.maps.fleetengine.v1.GetVehicleRequest;
import google.maps.fleetengine.v1.UpdateVehicleRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class VehicleCommandsTest {

  private static final String TEST_PROVIDER_ID = "test-provider-id";
  private static final String TEST_PROVIDER_NAME = "providers/test-provider-id";
  private static final String TEST_VEHICLE_ID = "test-vehicle-id";
  private static final String TEST_VEHICLE_NAME =
      "providers/test-provider-id/vehicles/test-vehicle-id";

  private VehicleStub vehicleStub;

  @Before
  public void setup() {
    vehicleStub = mock(VehicleStub.class);
  }

  @Test
  public void create_hasCorrectAttributesOnRequest() {
    VehicleCommands vehicleCommands = new VehicleCommands(vehicleStub, TEST_PROVIDER_ID);

    vehicleCommands.createVehicle(TEST_VEHICLE_ID);

    ArgumentCaptor<CreateVehicleRequest> requestCaptor =
        ArgumentCaptor.forClass(CreateVehicleRequest.class);
    verify(vehicleStub).createVehicle(requestCaptor.capture());
    assertEquals(TEST_VEHICLE_ID, requestCaptor.getValue().getVehicleId());
    assertEquals(TEST_PROVIDER_NAME, requestCaptor.getValue().getParent());
    assertEquals(TEST_VEHICLE_NAME, requestCaptor.getValue().getVehicle().getName());
  }

  @Test
  public void get_hasCorrectAttributesOnRequest() {
    VehicleCommands vehicleCommands = new VehicleCommands(vehicleStub, TEST_PROVIDER_ID);

    vehicleCommands.getVehicle(TEST_VEHICLE_ID);

    ArgumentCaptor<GetVehicleRequest> requestCaptor =
        ArgumentCaptor.forClass(GetVehicleRequest.class);
    verify(vehicleStub).getVehicle(requestCaptor.capture());
    assertEquals(TEST_VEHICLE_NAME, requestCaptor.getValue().getName());
  }

  @Test
  public void bringOffline_hasCorrectAttributesOnRequest() {
    VehicleCommands vehicleCommands = new VehicleCommands(vehicleStub, TEST_PROVIDER_ID);

    vehicleCommands.bringOffline(TEST_VEHICLE_ID);

    ArgumentCaptor<UpdateVehicleRequest> requestCaptor =
        ArgumentCaptor.forClass(UpdateVehicleRequest.class);
    verify(vehicleStub).updateVehicle(requestCaptor.capture());
    assertEquals(TEST_VEHICLE_NAME, requestCaptor.getValue().getName());
  }
}
