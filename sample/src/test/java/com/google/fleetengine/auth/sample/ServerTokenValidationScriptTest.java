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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.maps.fleetengine.v1.VehicleMatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServerTokenValidationScriptTest {
  private static final String TEST_FLEET_ENGINE_ADDRESS = "test.fleetengine.address:123";
  private static final String TEST_PROVIDER_ID = "test-provider-id";

  private SampleScriptRuntime runtime;
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

    List<VehicleMatch> matches = new ArrayList<>(1);
    matches.add(VehicleMatch.newBuilder().build());
    when(vehicleCommands.searchVehicles(any())).thenReturn(matches);
  }

  @Test
  public void run_callsTripCommands() throws Throwable {
    ServerTokenValidationScript script =
        new ServerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run();

    verify(tripCommands).getTrip(any());
    verify(tripCommands).createTrip(any());
    verify(tripCommands).completeTrip(any());
  }

  @Test
  public void run_callsVehicleCommands() throws Throwable {
    ServerTokenValidationScript script =
        new ServerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run();

    verify(vehicleCommands).getVehicle(any());
    verify(vehicleCommands).createVehicle(any());
    verify(vehicleCommands).bringOffline(any());
    verify(vehicleCommands).searchVehicles(any());
  }
}
