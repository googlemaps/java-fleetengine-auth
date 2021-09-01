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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.AuthTokenMinter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class DriverTokenValidationScriptTest {
  private static final String TEST_FLEET_ENGINE_ADDRESS = "test.fleetengine.address:123";
  private static final String TEST_PROVIDER_ID = "test-provider-id";
  private static final String TEST_VEHICLE_ID = "test-vehicle-id";

  private UnitTestSampleScriptRuntime runtime;
  private SampleScriptConfiguration configuration;
  private CommandsFactory commandsFactory;
  private VehicleCommands vehicleCommands;
  private TripCommands tripCommands;

  @Before
  public void setup() throws IOException {
    AuthTokenMinter minter = AuthTokenMinter.builder().build();
    runtime = new UnitTestSampleScriptRuntime();
    commandsFactory = mock(CommandsFactory.class);
    vehicleCommands = mock(VehicleCommands.class);
    tripCommands = mock(TripCommands.class);
    configuration =
        SampleScriptConfiguration.builder()
            .setFleetEngineAddress(TEST_FLEET_ENGINE_ADDRESS)
            .setProviderId(TEST_PROVIDER_ID)
            .setMinter(minter)
            .build();

    when(commandsFactory.createVehicleCommands(
            eq(TEST_FLEET_ENGINE_ADDRESS), eq(TEST_PROVIDER_ID), any()))
        .thenReturn(vehicleCommands);
    when(commandsFactory.createTripCommands(
            eq(TEST_FLEET_ENGINE_ADDRESS), eq(TEST_PROVIDER_ID), any()))
        .thenReturn(tripCommands);
  }

  @Test
  public void run_callsGetVehicle_withAndWithoutTokenVehicleId() throws Throwable {
    DriverTokenValidationScript script =
        new DriverTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_VEHICLE_ID);

    assertTrue(runtime.wasExpectPermissionDeniedCalled());
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(vehicleCommands, times(2)).getVehicle(captor.capture());
    assertEquals(TEST_VEHICLE_ID, captor.getAllValues().get(0));
    assertNotEquals(TEST_VEHICLE_ID, captor.getAllValues().get(1));
  }

  @Test
  public void run_callsCreateVehicle() throws Throwable {
    DriverTokenValidationScript script =
        new DriverTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_VEHICLE_ID);

    verify(vehicleCommands).createVehicle(eq(TEST_VEHICLE_ID));
  }

  @Test
  public void run_callsBringOffline() throws Throwable {
    DriverTokenValidationScript script =
        new DriverTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_VEHICLE_ID);

    verify(vehicleCommands).bringOffline(eq(TEST_VEHICLE_ID));
  }
}
