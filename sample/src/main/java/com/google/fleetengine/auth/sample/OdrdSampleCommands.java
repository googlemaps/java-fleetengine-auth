package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier;
import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner;
import com.google.fleetengine.auth.token.factory.signer.SignerInitializationException;
import com.google.maps.fleetengine.v1.CreateTripRequest;
import com.google.maps.fleetengine.v1.CreateVehicleRequest;
import com.google.maps.fleetengine.v1.ListVehiclesRequest;
import com.google.maps.fleetengine.v1.SearchTripsRequest;
import com.google.maps.fleetengine.v1.SearchVehiclesRequest;
import com.google.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrder;
import com.google.maps.fleetengine.v1.SearchVehiclesResponse;
import com.google.maps.fleetengine.v1.TerminalLocation;
import com.google.maps.fleetengine.v1.Trip;
import com.google.maps.fleetengine.v1.TripServiceClient;
import com.google.maps.fleetengine.v1.TripServiceClient.SearchTripsPagedResponse;
import com.google.maps.fleetengine.v1.TripServiceSettings;
import com.google.maps.fleetengine.v1.TripType;
import com.google.maps.fleetengine.v1.Vehicle;
import com.google.maps.fleetengine.v1.Vehicle.VehicleType;
import com.google.maps.fleetengine.v1.Vehicle.VehicleType.Category;
import com.google.maps.fleetengine.v1.VehicleLocation;
import com.google.maps.fleetengine.v1.VehicleMatch;
import com.google.maps.fleetengine.v1.VehicleServiceClient;
import com.google.maps.fleetengine.v1.VehicleServiceClient.ListVehiclesPagedResponse;
import com.google.maps.fleetengine.v1.VehicleServiceSettings;
import com.google.maps.fleetengine.v1.VehicleState;
import com.google.type.LatLng;
import java.io.IOException;
import java.util.UUID;

final class OdrdSampleCommands {
  private static final LatLng EXAMPLE_LAT_LNG =
      LatLng.newBuilder().setLatitude(37.4194945).setLongitude(-122.0761644).build();

  private static final LatLng DEST_EXAMPLE_LAT_LNG =
      LatLng.newBuilder().setLatitude(37.419645).setLongitude(-122.073884).build();

  private OdrdSampleCommands() {}

  private static FleetEngineTokenProvider minter;

  static void createVehicle() throws SignerInitializationException, IOException {
    String randomVehicleId = String.format("vehicle-%s", UUID.randomUUID());

    // Construct the vehicle object
    Vehicle vehicle =
        Vehicle.newBuilder()

            // Set the vehicle name to the specified format
            .setName(
                String.format(
                    "providers/%s/vehicles/%s", OdrdConfiguration.PROVIDER_ID, randomVehicleId))

            // Set maximum capacity of vehicle to 1
            .setMaximumCapacity(1)

            // Set the vehicle to be online
            .setVehicleState(VehicleState.ONLINE)

            // Set the vehicle type to be an automobile
            .setVehicleType(VehicleType.newBuilder().setCategory(Category.AUTO))

            // Set the vehicle to only support exclusive trips
            .addSupportedTripTypes(TripType.EXCLUSIVE)

            // Set the last vehicle location to a hardcoded value
            .setLastLocation(VehicleLocation.newBuilder().setLocation(EXAMPLE_LAT_LNG).build())
            .build();

    // Create the created vehicle request with the vehicle object above
    CreateVehicleRequest request =
        CreateVehicleRequest.newBuilder()
            // Set the randomly generated vehicle id
            .setVehicleId(randomVehicleId)

            // Set the vehicle to the one constructed above
            .setVehicle(vehicle)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))
            .build();

    VehicleServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(getMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    VehicleServiceClient client = VehicleServiceClient.create(settings);
    client.createVehicle(request);
    System.out.printf("Vehicle with id '%s' created\n", randomVehicleId);
  }

  static void createTrip() throws SignerInitializationException, IOException {
    String randomTripId = String.format("trip-%s", UUID.randomUUID());
    Trip trip =
        Trip.newBuilder()
            // Set the trip name to the specified format
            .setName(
                String.format("providers/%s/trips/%s", OdrdConfiguration.PROVIDER_ID, randomTripId))

            // Set the trip type to be exclusive as opposed to SHARED
            .setTripType(TripType.EXCLUSIVE)

            // Set the number of passengers to just 1
            .setNumberOfPassengers(1)

            // Set the pick up and drop off points
            .setPickupPoint(TerminalLocation.newBuilder().setPoint(EXAMPLE_LAT_LNG).build())
            .setDropoffPoint(TerminalLocation.newBuilder().setPoint(DEST_EXAMPLE_LAT_LNG).build())
            .build();

    CreateTripRequest request =
        CreateTripRequest.newBuilder()
            // Set the randomly generated trip id
            .setTripId(randomTripId)

            // Set the trip to the one constructed above
            .setTrip(trip)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))
            .build();

    TripServiceSettings settings =
        new FleetEngineClientSettingsModifier<TripServiceSettings, TripServiceSettings.Builder>(
                getMinter())
            .updateBuilder(TripServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    try (TripServiceClient client = TripServiceClient.create(settings)) {
      client.createTrip(request);
    }
    System.out.printf("Trip with id '%s' created\n", randomTripId);

  }

  static void listVehicles() throws SignerInitializationException, IOException {
    ListVehiclesRequest request =
        ListVehiclesRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))
            .build();

    VehicleServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(getMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    ListVehiclesPagedResponse response;
    try (VehicleServiceClient client = VehicleServiceClient.create(settings)) {
      response = client.listVehicles(request);
    }

    for (Vehicle vehicle : response.getPage().getValues()) {
      System.out.printf("Vehicle Name: %s\n", vehicle.getName());
      System.out.printf("Vehicle State: %s\n", vehicle.getVehicleState());
      System.out.printf("Vehicle Type: %s\n", vehicle.getVehicleType().getCategory());
      System.out.println();
    }
  }

  static void searchVehicles() throws SignerInitializationException, IOException {
    SearchVehiclesRequest request =
        SearchVehiclesRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))

            // Look for vehicles around a specific Lat \ Lng
            .setPickupPoint(TerminalLocation.newBuilder().setPoint(EXAMPLE_LAT_LNG).build())
            // Look for vehicles that can handle at least 1 passenger with trip type exclusive
            .setMinimumCapacity(1)
            .addTripTypes(TripType.EXCLUSIVE)

            // Look 400 meters around the pick up point
            .setPickupRadiusMeters(400)

            // Look only for vehicles of types AUTOs
            .addVehicleTypes(VehicleType.newBuilder().setCategory(Category.AUTO))

            // Get the top 5 results sorted by the vehicles distance to the pick up point
            .setCount(5)
            .setOrderBy(VehicleMatchOrder.PICKUP_POINT_DISTANCE)
            .build();

    VehicleServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(getMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    SearchVehiclesResponse response;
    try (VehicleServiceClient client = VehicleServiceClient.create(settings)) {
      response = client.searchVehicles(request);
    }
    for (VehicleMatch vehicleMatch : response.getMatchesList()) {
      Vehicle vehicle = vehicleMatch.getVehicle();
      System.out.printf("Vehicle Name: %s\n", vehicle.getName());
      System.out.printf("Vehicle State: %s\n", vehicle.getVehicleState());
      System.out.printf("Vehicle Type: %s\n", vehicle.getVehicleType().getCategory());
      System.out.println();
    }
  }

  static void searchTrips() throws SignerInitializationException, IOException {
    SearchTripsRequest request =
        SearchTripsRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))

            // Look for both active and inactive trips
            .setActiveTripsOnly(false)
            .build();

    TripServiceSettings settings =
        new FleetEngineClientSettingsModifier<TripServiceSettings, TripServiceSettings.Builder>(
                getMinter())
            .updateBuilder(TripServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    SearchTripsPagedResponse response;
    try (TripServiceClient client = TripServiceClient.create(settings)) {
      response = client.searchTrips(request);
    }

    for (Trip trip : response.getPage().getValues()) {
      System.out.printf("Trip Name: %s\n", trip.getName());
      System.out.printf("Drop off Location: %s\n", trip.getDropoffPoint().getPoint());
      System.out.printf("Pick up Location: %s\n", trip.getPickupPoint().getPoint());
      System.out.println();
    }
  }

  private static FleetEngineTokenProvider getMinter() throws SignerInitializationException {
    // Only create one minter across all calls to Fleet Engine
    if (minter == null) {
      minter = AuthTokenMinter.builder()
          // Only the account for the server signer is needed in this example
          .setServerSigner(ImpersonatedSigner.create(OdrdConfiguration.SERVER_TOKEN_ACCOUNT))

          // When the audience is not set, it defaults to https://fleetengine.googleapis.com/.
          // This is fine in the vast majority of cases.
          .setTokenFactory(
              new FleetEngineTokenFactory(
                  FleetEngineTokenFactorySettings.builder()
                      .setAudience(OdrdConfiguration.FLEET_ENGINE_AUDIENCE)
                      .build()))

          // Build the minter
          .build();
    }
    return minter;
  }
}
