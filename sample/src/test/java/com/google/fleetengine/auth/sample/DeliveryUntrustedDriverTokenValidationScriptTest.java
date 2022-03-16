package com.google.fleetengine.auth.sample.validation;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.AuthTokenMinter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class DeliveryUntrustedDriverTokenValidationScriptTest {
  private static final String TEST_FLEET_ENGINE_ADDRESS = "test.fleetengine.address:123";
  private static final String TEST_PROVIDER_ID = "test-provider-id";
  private static final String TEST_DELIVERY_VEHICLE_ID = "test-delivery-vehicle-id";

  private UnitTestSampleScriptRuntime runtime;
  private SampleScriptConfiguration configuration;
  private CommandsFactory commandsFactory;
  private DeliveryServiceCommands deliveryServiceCommands;

  @Before
  public void setup() throws IOException {
    AuthTokenMinter minter = AuthTokenMinter.builder().build();
    runtime = new UnitTestSampleScriptRuntime();
    commandsFactory = mock(CommandsFactory.class);
    deliveryServiceCommands = mock(DeliveryServiceCommands.class);
    configuration =
        SampleScriptConfiguration.builder()
            .setFleetEngineAddress(TEST_FLEET_ENGINE_ADDRESS)
            .setProviderId(TEST_PROVIDER_ID)
            .setMinter(minter)
            .build();

    when(commandsFactory.createDeliveryServiceCommands(
            eq(TEST_FLEET_ENGINE_ADDRESS), eq(TEST_PROVIDER_ID), any()))
        .thenReturn(deliveryServiceCommands);
  }

  @Test
  public void run_callsGetDeliveryVehicle_withDeliveryVehicleId() throws Throwable {
    DeliveryUntrustedDriverTokenValidationScript script =
        new DeliveryUntrustedDriverTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_DELIVERY_VEHICLE_ID);

    runtime.wasExpectPermissionDeniedCalled();
    verify(deliveryServiceCommands, times(1)).getDeliveryVehicle(TEST_DELIVERY_VEHICLE_ID);
  }

  @Test
  public void run_callsCreateDeliveryVehicle_withDifferentDeliveryVehicleId() throws Throwable {
    DeliveryUntrustedDriverTokenValidationScript script =
        new DeliveryUntrustedDriverTokenValidationScript(runtime, configuration, commandsFactory);
    script.run(TEST_DELIVERY_VEHICLE_ID);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(deliveryServiceCommands, times(1)).createDeliveryVehicle(captor.capture());
    assertNotEquals(TEST_DELIVERY_VEHICLE_ID, captor.getValue());
  }
}
