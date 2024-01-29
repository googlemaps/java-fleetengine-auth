package com.google.fleetengine.auth.sample.validation;

/** Validates that fleet reader tokens provide the correct access. */
public class ConsumerFleetReaderTokenValidationScript {

  private final SampleScriptRuntime runtime;
  private final SampleScriptConfiguration configuration;
  private final CommandsFactory commandsFactory;

  public ConsumerFleetReaderTokenValidationScript(
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
    // Tokens are minted with the fleet reader role and authorized to access tripId.
    TripCommands tripCommands =
        commandsFactory.createTripCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getFleetReaderToken());

    VehicleCommands vehicleCommands =
        commandsFactory.createVehicleCommands(
            configuration.getFleetEngineAddress(),
            configuration.getProviderId(),
            () -> configuration.getMinter().getFleetReaderToken());

    runtime.runCommand(
        "Get trip with trip id on fleet reader token", () -> tripCommands.getTrip(tripId));

    runtime.runCommand(
        "Search for vehicles with fleet reader token",
        () -> vehicleCommands.searchVehicles(tripId));
  }
}
