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
public class ConsumerTokenValidationScriptTest {
  private static final String TEST_FLEET_ENGINE_ADDRESS = "test.fleetengine.address:123";
  private static final String TEST_PROVIDER_ID = "test-provider-id";
  private static final String TEST_TRIP_ID = "test-trip-id";

  private UnitTestSampleScriptRuntime runtime;
  private SampleScriptConfiguration configuration;
  private CommandsFactory commandsFactory;
  private TripCommands tripCommands;
  private VehicleCommands vehicleCommands;

  @Before
  public void setup() throws IOException {
    AuthTokenMinter minter = AuthTokenMinter.builder().build();
    runtime = new UnitTestSampleScriptRuntime();
    commandsFactory = mock(CommandsFactory.class);
    tripCommands = mock(TripCommands.class);
    vehicleCommands = mock(VehicleCommands.class);
    configuration =
        SampleScriptConfiguration.builder()
            .setFleetEngineAddress(TEST_FLEET_ENGINE_ADDRESS)
            .setProviderId(TEST_PROVIDER_ID)
            .setMinter(minter)
            .build();

    when(commandsFactory.createTripCommands(
            eq(TEST_FLEET_ENGINE_ADDRESS), eq(TEST_PROVIDER_ID), any()))
        .thenReturn(tripCommands);
    when(commandsFactory.createVehicleCommands(
            eq(TEST_FLEET_ENGINE_ADDRESS), eq(TEST_PROVIDER_ID), any()))
        .thenReturn(vehicleCommands);
  }

  @Test
  public void run_callsGetTrip_withAndWithoutTokenTripId() throws Throwable {
    ConsumerTokenValidationScript script =
        new ConsumerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_TRIP_ID);

    assertTrue(runtime.wasExpectPermissionDeniedCalled());
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(tripCommands, times(2)).getTrip(captor.capture());
    assertEquals(TEST_TRIP_ID, captor.getAllValues().get(0));
    assertNotEquals(TEST_TRIP_ID, captor.getAllValues().get(1));
  }

  @Test
  public void run_searchVehicles() throws Throwable {
    ConsumerTokenValidationScript script =
        new ConsumerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_TRIP_ID);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(vehicleCommands).searchVehicles(captor.capture());
    assertEquals(TEST_TRIP_ID, captor.getValue());
  }

  @Test
  public void run_callsCreateTrip() throws Throwable {
    ConsumerTokenValidationScript script =
        new ConsumerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_TRIP_ID);

    verify(tripCommands).createTrip(eq(TEST_TRIP_ID));
  }
}
