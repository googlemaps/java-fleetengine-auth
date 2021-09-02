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

package com.google.fleetengine.auth.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier;
import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner;
import com.google.fleetengine.auth.token.factory.signer.SignerInitializationException;
import com.google.type.LatLng;
import google.maps.fleetengine.v1.CreateTripRequest;
import google.maps.fleetengine.v1.CreateVehicleRequest;
import google.maps.fleetengine.v1.ListVehiclesRequest;
import google.maps.fleetengine.v1.SearchTripsRequest;
import google.maps.fleetengine.v1.SearchVehiclesRequest;
import google.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrder;
import google.maps.fleetengine.v1.SearchVehiclesResponse;
import google.maps.fleetengine.v1.TerminalLocation;
import google.maps.fleetengine.v1.Trip;
import google.maps.fleetengine.v1.TripServiceClient;
import google.maps.fleetengine.v1.TripServiceClient.SearchTripsPagedResponse;
import google.maps.fleetengine.v1.TripServiceSettings;
import google.maps.fleetengine.v1.TripType;
import google.maps.fleetengine.v1.Vehicle;
import google.maps.fleetengine.v1.Vehicle.VehicleType;
import google.maps.fleetengine.v1.Vehicle.VehicleType.Category;
import google.maps.fleetengine.v1.VehicleLocation;
import google.maps.fleetengine.v1.VehicleMatch;
import google.maps.fleetengine.v1.VehicleServiceClient;
import google.maps.fleetengine.v1.VehicleServiceClient.ListVehiclesPagedResponse;
import google.maps.fleetengine.v1.VehicleServiceSettings;
import google.maps.fleetengine.v1.VehicleState;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

/** Sample app for auth library. */
public final class SampleApp {

  private static final double EXAMPLE_LATITUDE = 37.419645;
  private static final double EXAMPLE_LONGITUDE = -122.073884;

  private SampleApp() {}

  /** Entry point. */
  public static void main(String[] args) throws Throwable {
    System.out.println(
        "\n\n\n=== Choose example: ===\n"
            + "0. Validate Configured Roles\n"
            + "1. Create Vehicle\n"
            + "2. Create Trip\n"
            + "3. List Vehicles\n"
            + "4. Search for Vehicles\n"
            + "5. Search for Trips\n");
    Scanner scanner = new Scanner(System.in, UTF_8.name());
    int choice = scanner.nextInt();

    switch (choice) {
      case 0:
        ValidateRoles.run();
        break;
      case 1:
        createVehicle();
        break;
      case 2:
        createTrip();
        break;
      case 3:
        listVehicles();
        break;
      case 4:
        searchVehicles();
        break;
      case 5:
        searchTrips();
        break;
      case 6:
      default:
        throw new IllegalArgumentException("Invalid choice provided.");
    }
  }

  private static void createVehicle() throws SignerInitializationException, IOException {
    String randomVehicleId = String.format("vehicle-%s", UUID.randomUUID());

    // Construct the vehicle object
    Vehicle vehicle =
        Vehicle.newBuilder()

            // Set the vehicle name to the specified format
            .setName(
                String.format(
                    "providers/%s/vehicles/%s", Configuration.PROVIDER_ID, randomVehicleId))

            // Set maximum capacity of vehicle to 1
            .setMaximumCapacity(1)

            // Set the vehicle to be online
            .setVehicleState(VehicleState.ONLINE)

            // Set the vehicle type to be an automobile
            .setVehicleType(VehicleType.newBuilder().setCategory(Category.AUTO))

            // Set the vehicle to only support exclusive trips
            .addSupportedTripTypes(TripType.EXCLUSIVE)

            // Set the last vehicle location to a hardcoded value
            .setLastLocation(
                VehicleLocation.newBuilder()
                    .setLocation(
                        LatLng.newBuilder()
                            .setLatitude(EXAMPLE_LATITUDE)
                            .setLongitude(EXAMPLE_LONGITUDE)
                            .build())
                    .build())
            .build();

    // Create the created vehicle request with the vehicle object above
    CreateVehicleRequest request =
        CreateVehicleRequest.newBuilder()
            // Set the randomly generated vehicle id
            .setVehicleId(randomVehicleId)

            // Set the vehicle to the one constructed above
            .setVehicle(vehicle)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", Configuration.PROVIDER_ID))
            .build();

    VehicleServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(createMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(Configuration.FLEET_ENGINE_ADDRESS)
            .build();

    VehicleServiceClient client = VehicleServiceClient.create(settings);
    client.createVehicle(request);
    System.out.printf("Vehicle with id '%s' created\n", randomVehicleId);
  }

  private static void createTrip() throws SignerInitializationException, IOException {
    String randomTripId = String.format("trip-%s", UUID.randomUUID());
    Trip trip =
        Trip.newBuilder()
            // Set the trip name to the specified format
            .setName(
                String.format("providers/%s/trips/%s", Configuration.PROVIDER_ID, randomTripId))

            // Set the trip type to be exclusive as opposed to SHARED
            .setTripType(TripType.EXCLUSIVE)

            // Set the number of passengers to just 1
            .setNumberOfPassengers(1)

            // Set the pick up and drop off points
            .setPickupPoint(
                TerminalLocation.newBuilder()
                    .setPoint(
                        LatLng.newBuilder()
                            .setLatitude(EXAMPLE_LATITUDE)
                            .setLongitude(EXAMPLE_LONGITUDE)
                            .build()))
            .setDropoffPoint(
                TerminalLocation.newBuilder()
                    .setPoint(
                        LatLng.newBuilder()
                            .setLatitude(EXAMPLE_LATITUDE)
                            .setLongitude(EXAMPLE_LONGITUDE)
                            .build()))
            .build();

    CreateTripRequest request =
        CreateTripRequest.newBuilder()
            // Set the randomly generated trip id
            .setTripId(randomTripId)

            // Set the trip to the one constructed above
            .setTrip(trip)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", Configuration.PROVIDER_ID))
            .build();

    TripServiceSettings settings =
        new FleetEngineClientSettingsModifier<TripServiceSettings, TripServiceSettings.Builder>(
                createMinter())
            .updateBuilder(TripServiceSettings.newBuilder())
            .setEndpoint(Configuration.FLEET_ENGINE_ADDRESS)
            .build();

    TripServiceClient client = TripServiceClient.create(settings);
    client.createTrip(request);
    System.out.printf("Trip with id '%s' created\n", randomTripId);
  }

  private static void listVehicles() throws SignerInitializationException, IOException {
    ListVehiclesRequest request =
        ListVehiclesRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", Configuration.PROVIDER_ID))
            .build();

    VehicleServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(createMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(Configuration.FLEET_ENGINE_ADDRESS)
            .build();

    VehicleServiceClient client = VehicleServiceClient.create(settings);
    ListVehiclesPagedResponse response = client.listVehicles(request);

    for (Vehicle vehicle : response.getPage().getValues()) {
      System.out.printf("Vehicle Name: %s\n", vehicle.getName());
      System.out.printf("Vehicle State: %s\n", vehicle.getVehicleState());
      System.out.printf("Vehicle Type: %s\n", vehicle.getVehicleType().getCategory());
      System.out.println();
    }
  }

  private static void searchVehicles() throws SignerInitializationException, IOException {
    SearchVehiclesRequest request =
        SearchVehiclesRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", Configuration.PROVIDER_ID))

            // Look for vehicles around a specific Lat \ Lng
            .setPickupPoint(
                TerminalLocation.newBuilder()
                    .setPoint(
                        LatLng.newBuilder()
                            .setLatitude(EXAMPLE_LATITUDE)
                            .setLongitude(EXAMPLE_LONGITUDE)
                            .build()))

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
                VehicleServiceSettings, VehicleServiceSettings.Builder>(createMinter())
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(Configuration.FLEET_ENGINE_ADDRESS)
            .build();

    VehicleServiceClient client = VehicleServiceClient.create(settings);
    SearchVehiclesResponse response = client.searchVehicles(request);
    for (VehicleMatch vehicleMatch : response.getMatchesList()) {
      Vehicle vehicle = vehicleMatch.getVehicle();
      System.out.printf("Vehicle Name: %s\n", vehicle.getName());
      System.out.printf("Vehicle State: %s\n", vehicle.getVehicleState());
      System.out.printf("Vehicle Type: %s\n", vehicle.getVehicleType().getCategory());
      System.out.println();
    }
  }

  private static void searchTrips() throws SignerInitializationException, IOException {
    SearchTripsRequest request =
        SearchTripsRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", Configuration.PROVIDER_ID))

            // Look for both active and inactive trips
            .setActiveTripsOnly(false)
            .build();

    TripServiceSettings settings =
        new FleetEngineClientSettingsModifier<TripServiceSettings, TripServiceSettings.Builder>(
                createMinter())
            .updateBuilder(TripServiceSettings.newBuilder())
            .setEndpoint(Configuration.FLEET_ENGINE_ADDRESS)
            .build();

    TripServiceClient client = TripServiceClient.create(settings);
    SearchTripsPagedResponse response = client.searchTrips(request);

    for (Trip trip : response.getPage().getValues()) {
      System.out.printf("Trip Name: %s\n", trip.getName());
      System.out.printf("Drop off Location: %s\n", trip.getDropoffPoint().getPoint());
      System.out.printf("Pick up Location: %s\n", trip.getPickupPoint().getPoint());
      System.out.println();
    }
  }

  private static FleetEngineTokenProvider createMinter() throws SignerInitializationException {
    return AuthTokenMinter.builder()
        // Only the account for the server signer is needed in this example
        .setServerSigner(ImpersonatedSigner.create(Configuration.SERVER_TOKEN_ACCOUNT))

        // When the audience is not set, it defaults to https://fleetengine.googleapis.com/.
        // This is fine in the vast majority of cases.
        .setTokenFactory(
            new FleetEngineTokenFactory(
                FleetEngineTokenFactorySettings.builder()
                    .setAudience(Configuration.FLEET_ENGINE_AUDIENCE)
                    .build()))

        // Build the minter
        .build();
  }
}
