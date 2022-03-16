package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier;
import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner;
import com.google.fleetengine.auth.token.factory.signer.SignerInitializationException;
import com.google.protobuf.Duration;
import com.google.type.LatLng;
import google.maps.fleetengine.delivery.v1.CreateDeliveryVehicleRequest;
import google.maps.fleetengine.delivery.v1.CreateTaskRequest;
import google.maps.fleetengine.delivery.v1.DeliveryServiceClient;
import google.maps.fleetengine.delivery.v1.DeliveryServiceClient.ListDeliveryVehiclesPagedResponse;
import google.maps.fleetengine.delivery.v1.DeliveryServiceSettings;
import google.maps.fleetengine.delivery.v1.DeliveryVehicle;
import google.maps.fleetengine.delivery.v1.DeliveryVehicleLocation;
import google.maps.fleetengine.delivery.v1.DeliveryVehicleNavigationStatus;
import google.maps.fleetengine.delivery.v1.ListDeliveryVehiclesRequest;
import google.maps.fleetengine.delivery.v1.LocationInfo;
import google.maps.fleetengine.delivery.v1.Task;
import google.maps.fleetengine.delivery.v1.Task.State;
import java.io.IOException;
import java.util.UUID;

final class LmfsSampleCommands {
  private static final LatLng EXAMPLE_LAT_LNG =
      LatLng.newBuilder().setLatitude(37.4194945).setLongitude(-122.0761644).build();

  private LmfsSampleCommands() {}

  static void createDeliveryVehicle() throws SignerInitializationException, IOException {
    String randomDeliveryVehicleId = String.format("delivery-vehicle-%s", UUID.randomUUID());

    // Construct the delivery vehicle object
    DeliveryVehicle deliveryVehicle =
        DeliveryVehicle.newBuilder()

            // Set the delivery vehicle name to the specified format
            .setName(
                String.format(
                    "providers/%s/deliveryVehicles/%s",
                    LmfsConfiguration.PROVIDER_ID, randomDeliveryVehicleId))

            // Set the last vehicle location to a hardcoded value
            .setLastLocation(
                DeliveryVehicleLocation.newBuilder().setLocation(EXAMPLE_LAT_LNG).build())

            // Set the navigation status to unknown
            .setNavigationStatus(DeliveryVehicleNavigationStatus.UNKNOWN_NAVIGATION_STATUS)
            .build();

    CreateDeliveryVehicleRequest createDeliveryVehicleRequest =
        CreateDeliveryVehicleRequest.newBuilder()

            // Set the randomly generated delivery vehicle id
            .setDeliveryVehicleId(randomDeliveryVehicleId)

            // Set the delivery vehicle to the one constructed above
            .setDeliveryVehicle(deliveryVehicle)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", LmfsConfiguration.PROVIDER_ID))
            .build();

    DeliveryServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                DeliveryServiceSettings, DeliveryServiceSettings.Builder>(createMinter())
            .updateBuilder(DeliveryServiceSettings.newBuilder())
            .setEndpoint(LmfsConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    DeliveryServiceClient client = DeliveryServiceClient.create(settings);
    client.createDeliveryVehicle(createDeliveryVehicleRequest);
    System.out.printf("Delivery Vehicle with id '%s' created\n", randomDeliveryVehicleId);
  }

  static void createTask() throws SignerInitializationException, IOException {
    String randomTaskId = String.format("task-%s", UUID.randomUUID());
    String randomTrackingId = String.format("tracking-id-%s", UUID.randomUUID());

    // Construct the task object
    Task task =
        Task.newBuilder()

            // Set the task name to the specified format
            .setName(
                String.format("providers/%s/tasks/%s", LmfsConfiguration.PROVIDER_ID, randomTaskId))

            // Set tracking id
            .setTrackingId(randomTrackingId)

            // Set the planned task location
            .setPlannedLocation(LocationInfo.newBuilder().setPoint(EXAMPLE_LAT_LNG).build())

            // Set the task duration to 1 hour
            .setTaskDuration(Duration.newBuilder().setSeconds(3600).build())

            // Set the state as an open task
            .setState(State.OPEN)

            // Set the type as a delivery task
            .setType(Task.Type.DELIVERY)
            .build();

    CreateTaskRequest createTaskRequest =
        CreateTaskRequest.newBuilder()

            // Set the randomly generated task id
            .setTaskId(randomTaskId)

            // Set the task to the one constructed above
            .setTask(task)

            // Set the parent to the specified format
            .setParent(String.format("providers/%s", LmfsConfiguration.PROVIDER_ID))
            .build();

    DeliveryServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                DeliveryServiceSettings, DeliveryServiceSettings.Builder>(createMinter())
            .updateBuilder(DeliveryServiceSettings.newBuilder())
            .setEndpoint(LmfsConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    DeliveryServiceClient client = DeliveryServiceClient.create(settings);
    client.createTask(createTaskRequest);
    System.out.printf("Task with id '%s' created\n", randomTaskId);
  }

  static void listDeliveryVehicles() throws SignerInitializationException, IOException {
    ListDeliveryVehiclesRequest request =
        ListDeliveryVehiclesRequest.newBuilder()
            // Set the parent to the format providers/{providerId}
            .setParent(String.format("providers/%s", OdrdConfiguration.PROVIDER_ID))
            .build();

    DeliveryServiceSettings settings =
        new FleetEngineClientSettingsModifier<
                DeliveryServiceSettings, DeliveryServiceSettings.Builder>(createMinter())
            .updateBuilder(DeliveryServiceSettings.newBuilder())
            .setEndpoint(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .build();

    DeliveryServiceClient client = DeliveryServiceClient.create(settings);
    ListDeliveryVehiclesPagedResponse response = client.listDeliveryVehicles(request);

    for (DeliveryVehicle vehicle : response.getPage().getValues()) {
      System.out.printf("Delivery Vehicle Name: %s\n", vehicle.getName());
      System.out.printf("Remaining seconds: %s\n", vehicle.getRemainingDuration().getSeconds());
      System.out.println();
    }
  }

  private static FleetEngineTokenProvider createMinter() throws SignerInitializationException {
    // Create minter with delivery builder in order to produce lmfs server tokens by default
    return AuthTokenMinter.deliveryBuilder()
        // Only the account for the server signer is needed in this example
        .setDeliveryServerSigner(
            ImpersonatedSigner.create(LmfsConfiguration.DELIVERY_SERVER_TOKEN_ACCOUNT))

        // When the audience is not set, it defaults to https://fleetengine.googleapis.com/.
        // This is fine in the vast majority of cases.
        .setTokenFactory(
            new FleetEngineTokenFactory(
                FleetEngineTokenFactorySettings.builder()
                    .setAudience(LmfsConfiguration.FLEET_ENGINE_AUDIENCE)
                    .build()))

        // Build the minter
        .build();
  }
}
