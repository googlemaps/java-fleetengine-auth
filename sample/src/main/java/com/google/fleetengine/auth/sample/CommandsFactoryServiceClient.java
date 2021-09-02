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

import com.google.fleetengine.auth.client.FleetEngineTokenProvider;
import com.google.fleetengine.auth.sample.validation.CommandsFactory;
import com.google.fleetengine.auth.sample.validation.TripCommands;
import com.google.fleetengine.auth.sample.validation.TripStub;
import com.google.fleetengine.auth.sample.validation.VehicleCommands;
import com.google.fleetengine.auth.sample.validation.VehicleStub;
import google.maps.fleetengine.v1.TripServiceClient;
import google.maps.fleetengine.v1.TripServiceSettings;
import google.maps.fleetengine.v1.VehicleServiceClient;
import google.maps.fleetengine.v1.VehicleServiceSettings;
import java.io.IOException;

class CommandsFactoryServiceClient implements CommandsFactory {
  /** {@inheritDoc} */
  public TripCommands createTripCommands(
      String fleetEngineAddress, String providerId, FleetEngineTokenProvider tokenProvider)
      throws IOException {
    TripServiceSettings settings =
        new com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier<
                TripServiceSettings, TripServiceSettings.Builder>(tokenProvider)
            .updateBuilder(TripServiceSettings.newBuilder())
            .setEndpoint(fleetEngineAddress)
            .build();

    TripServiceClient client = TripServiceClient.create(settings);
    TripStub tripStub = new TripStubClient(client);
    return new TripCommands(tripStub, providerId);
  }

  /** {@inheritDoc} */
  public VehicleCommands createVehicleCommands(
      String fleetEngineAddress, String providerId, FleetEngineTokenProvider tokenProvider)
      throws IOException {
    VehicleServiceSettings settings =
        new com.google.fleetengine.auth.client.FleetEngineClientSettingsModifier<
                VehicleServiceSettings, VehicleServiceSettings.Builder>(tokenProvider)
            .updateBuilder(VehicleServiceSettings.newBuilder())
            .setEndpoint(fleetEngineAddress)
            .build();

    VehicleServiceClient client = VehicleServiceClient.create(settings);
    VehicleStub vehicleStub = new VehicleStubClient(client);
    return new VehicleCommands(vehicleStub, providerId);
  }
}
