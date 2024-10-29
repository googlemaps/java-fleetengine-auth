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

import com.google.maps.fleetengine.v1.VehicleMatch;
import java.util.List;

/** Validates that server tokens provide the correct level of access. */
public class ServerTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor */
  public ServerTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  /** Run validation script. */
  public Ids run() throws Throwable {
    Ids ids = new Ids(ScriptUtils.generateRandomVehicleId(), ScriptUtils.generateRandomTripId());

    VehicleCommands vehicleCommands =
        commandsFactory.createVehicleCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            configuration.getMinter());

    TripCommands tripCommands =
        commandsFactory.createTripCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            configuration.getMinter());

    runtime.runCommand(
        "Create vehicle with server token",
        () -> vehicleCommands.createVehicle(ids.getVehicleId()));

    runtime.runCommand(
        "Get vehicle with server token", () -> vehicleCommands.getVehicle(ids.getVehicleId()));

    runtime.runCommand(
        "Create trip with server token", () -> tripCommands.createTrip(ids.getTripId()));

    runtime.runCommand("Get trip with server token", () -> tripCommands.getTrip(ids.getTripId()));

    runtime.runCommand(
        "Search for vehicles with server token",
        () -> {
          List<VehicleMatch> matches = vehicleCommands.searchVehicles(ids.getTripId());
          if (!matches.stream().findAny().isPresent()) {
            throw new SampleScriptException("No vehicle match found.");
          }
        });

    runtime.runCommand(
        "Set trip status to complete with server token",
        () -> tripCommands.completeTrip(ids.getTripId()));

    runtime.runCommand(
        "Bring vehicle offline with server token",
        () -> vehicleCommands.bringOffline(ids.getVehicleId()));

    return ids;
  }

  /** Ids of newly created entities. */
  public static class Ids {

    private final String vehicleId;
    private final String tripId;

    Ids(String vehicleId, String tripId) {
      this.vehicleId = vehicleId;
      this.tripId = tripId;
    }

    /** Id of the new Vehicle created. */
    public String getVehicleId() {
      return vehicleId;
    }

    /** Id of the new Trip created. */
    public String getTripId() {
      return tripId;
    }
  }
}
