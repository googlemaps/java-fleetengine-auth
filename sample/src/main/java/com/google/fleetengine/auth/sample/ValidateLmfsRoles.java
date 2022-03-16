package com.google.fleetengine.auth.sample;

import com.google.fleetengine.auth.AuthTokenMinter;
import com.google.fleetengine.auth.sample.validation.DeliveryConsumerTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.DeliveryFleetReaderTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.DeliveryServerTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.DeliveryServerTokenValidationScript.Ids;
import com.google.fleetengine.auth.sample.validation.DeliveryTrustedDriverTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.DeliveryUntrustedDriverTokenValidationScript;
import com.google.fleetengine.auth.sample.validation.SampleScriptConfiguration;
import com.google.fleetengine.auth.sample.validation.SampleScriptRuntime;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory;
import com.google.fleetengine.auth.token.factory.FleetEngineTokenFactorySettings;
import com.google.fleetengine.auth.token.factory.signer.ImpersonatedSigner;

public class ValidateLmfsRoles {
  private static final String SERVER = "server";
  private static final String CONSUMER = "deliver consumer";
  private static final String UNTRUSTED_DRIVER = "untrusted driver";
  private static final String TRUSTED_DRIVER = "trusted driver";
  private static final String FLEET_READER = "fleet reader";

  public static void run() throws Throwable {
    AuthTokenMinter minter =
        AuthTokenMinter.deliveryBuilder()
            .setDeliveryServerSigner(
                ImpersonatedSigner.create(LmfsConfiguration.DELIVERY_SERVER_TOKEN_ACCOUNT))
            .setDeliveryConsumerSigner(
                ImpersonatedSigner.create(LmfsConfiguration.DELIVERY_CONSUMER_TOKEN_ACCOUNT))
            .setTrustedDeliveryDriverSigner(
                ImpersonatedSigner.create(LmfsConfiguration.DELIVERY_TRUSTED_DRIVER_TOKEN_ACCOUNT))
            .setUntrustedDeliveryDriverSigner(
                ImpersonatedSigner.create(
                    LmfsConfiguration.DELIVERY_UNTRUSTED_DRIVER_TOKEN_ACCOUNT))
            .setDeliveryFleetReaderSigner(
                ImpersonatedSigner.create(LmfsConfiguration.DELIVERY_FLEET_READER_TOKEN_ACCOUNT))
            .setTokenFactory(
                new FleetEngineTokenFactory(
                    FleetEngineTokenFactorySettings.builder()
                        .setAudience(LmfsConfiguration.FLEET_ENGINE_AUDIENCE)
                        .build()))
            .build();

    SampleScriptConfiguration configuration =
        SampleScriptConfiguration.builder()
            .setFleetEngineAddress(LmfsConfiguration.FLEET_ENGINE_ADDRESS)
            .setProviderId(LmfsConfiguration.PROVIDER_ID)
            .setMinter(minter)
            .build();

    CommandsFactoryServiceClient clientFactory = new CommandsFactoryServiceClient();
    SampleScriptRuntime runtime = CommandLineRuntime.create();
    CommandLineRuntime.printRunScriptMessage(SERVER);
    Ids ids = new DeliveryServerTokenValidationScript(runtime, configuration, clientFactory).run();

    if (configuration.getMinter().deliveryConsumerSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(CONSUMER);
      new DeliveryConsumerTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getTrackingId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(CONSUMER);
    }

    if (configuration.getMinter().untrustedDeliveryDriverSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(UNTRUSTED_DRIVER);
      new DeliveryUntrustedDriverTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getDeliveryVehicleId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(UNTRUSTED_DRIVER);
    }

    if (configuration.getMinter().trustedDeliveryDriverSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(TRUSTED_DRIVER);
      new DeliveryTrustedDriverTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getDeliveryVehicleId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(TRUSTED_DRIVER);
    }

    if (configuration.getMinter().deliveryFleetReaderSigner() != null) {
      CommandLineRuntime.printRunScriptMessage(FLEET_READER);
      new DeliveryFleetReaderTokenValidationScript(runtime, configuration, clientFactory)
          .run(ids.getDeliveryVehicleId());
    } else {
      CommandLineRuntime.printSkipScriptMessage(TRUSTED_DRIVER);
    }
  }
}
