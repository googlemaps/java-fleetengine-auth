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

import com.google.fleetengine.auth.token.VehicleClaims;

/** Validates that driver tokens provide the correct level of access. */
public class DriverTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor. */
  public DriverTokenValidationScript(
      SampleScriptRuntime runtime,
      SampleScriptConfiguration configuration,
      CommandsFactory commandsFactory) {
    this.runtime = runtime;
    this.configuration = configuration;
    this.commandsFactory = commandsFactory;
  }

  /**
   * Run validation script.
   *
   * @param vehicleId existing vehicle id
   */
  public void run(String vehicleId) throws Throwable {
    // Tokens are minted with the driver role and authorized to access vehicleId.
    VehicleCommands vehicleCommands =
        commandsFactory.createVehicleCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getDriverToken(VehicleClaims.create(vehicleId)));

    // Vehicle id matches the vehicle id with the authorization claim
    runtime.runCommand(
        "Get vehicle with same vehicle id on driver token",
        () -> vehicleCommands.getVehicle(vehicleId));

    // Rejected when trying to get a vehicle with an id other than the other one specified.
    String randomVehicleId = ScriptUtils.generateRandomVehicleId();
    runtime.expectPermissionDenied(
        "Get vehicle fails with different driver token vehicle id",
        () -> vehicleCommands.getVehicle(randomVehicleId));

    runtime.expectPermissionDenied(
        "Driver token does not have permission to create vehicle",
        () -> vehicleCommands.createVehicle(vehicleId));

    runtime.runCommand("Bring vehicle offline", () -> vehicleCommands.bringOffline(vehicleId));
  }
}
