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

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.fleetengine.auth.sample.validation.ConsumerTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.DriverTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.ConsumerFleetReaderTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.SampleScriptConfiguration;
import com.google.fleetengine.auth.sample.validation.SampleScriptRuntime;
import com.google.fleetengine.auth.sample.validation.ServerTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.ServerTokenValidationScript.Ids;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner;

/** Validates that the roles configured in {@link OdrdConfiguration} are setup correctly. */
final class ValidateOdrdRoles {
  private static final String SERVER = "server";
  private static final String CONSUMER = "consumer";
  private static final String DRIVER = "driver";
  private static final String FLEET_READER = "fleet reader";

  public static void run() throws Throwable {
    AuthTokenMinter minter =
        AuthTokenMinter.builder()
            .setServerSigner(ImpersonatedSigner.create(OdrdConfiguration.SERVER_TOKEN_ACCOUNT))
            .setConsumerSigner(ImpersonatedSigner.create(OdrdConfiguration.CONSUMER_TOKEN_ACCOUNT))
            .setDriverSigner(ImpersonatedSigner.create(OdrdConfiguration.DRIVER_TOKEN_ACCOUNT))
            .setFleetReaderSigner(
                ImpersonatedSigner.create(OdrdConfiguration.FLEET_READER_TOKEN_ACCOUNT))
            .setTokenFactory(
                new FleetEngineTokenFactory(
                    FleetEngineTokenFactorySettings.builder()
                        .setAudience(OdrdConfiguration.FLEET_ENGINE_AUDIENCE)
                        .build()))
            .build();

    SampleScriptConfiguration configuration =
        SampleScriptConfiguration.builder()
            .setFleetEngineAddress(OdrdConfiguration.FLEET_ENGINE_ADDRESS)
            .setProviderId(OdrdConfiguration.PROVIDER_ID)
            .setMinter(minter)
            .build();

    CommandsFactoryServiceClient clientFactory = new CommandsFactoryServiceClient();
    SampleScriptRuntime runtime = CommandLineRuntime.create();
    CommandLineRuntime.printRunScriptMessage(SERVER);
    Ids ids = new ServerTokenValidationScript(runtime, configuration, clientFactory).run();

    if (configuration.getMinter().consumerSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(CONSUMER);
      new ConsumerTokenValidationScript(runtime, configuration, clientFactory).run(ids.getTripId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(CONSUMER);
    }

    if (configuration.getMinter().driverSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(DRIVER);
      new DriverTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getVehicleId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(DRIVER);
    }

    if (configuration.getMinter().fleetReaderSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(FLEET_READER);
      new ConsumerFleetReaderTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getTripId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(FLEET_READER);
    }
  }

  private ValidateOdrdRoles() {}
}
