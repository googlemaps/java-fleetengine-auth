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

import com.google.fleetengine.auth.token.TripClaims;

/** Validates that consumer tokens provide the correct level of access. */
public class ConsumerTokenValidationScript {
  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  /** Constructor. */
  public ConsumerTokenValidationScript(
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
   * @param tripId existing trip id
   */
  public void run(String tripId) throws Throwable {
    // Tokens are minted with the consumer role and authorized to access tripId.
    TripCommands tripCommands =
        commandsFactory.createTripCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getConsumerToken(TripClaims.create(tripId)));

    VehicleCommands vehicleCommands =
        commandsFactory.createVehicleCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getConsumerToken(TripClaims.create(tripId)));

    // Trip id matches the trip id with the authorization claim
    runtime.runCommand(
        "Get trip with same trip id on consumer token", () -> tripCommands.getTrip(tripId));

    runtime.runCommand(
        "Search for vehicles with consumer token", () -> vehicleCommands.searchVehicles(tripId));

    // Rejected when trying to get a trip with an id other than the other one specified.
    String randomTripId = ScriptUtils.generateRandomTripId();
    runtime.expectPermissionDenied(
        "Get trip fails when trip id different than consumer token trip id",
        () -> tripCommands.getTrip(randomTripId));

    // Create trip should always fail for consumer token
    runtime.expectPermissionDenied(
        "Consumer token does not have permission to create trip",
        () -> tripCommands.createTrip(tripId));
  }
}
