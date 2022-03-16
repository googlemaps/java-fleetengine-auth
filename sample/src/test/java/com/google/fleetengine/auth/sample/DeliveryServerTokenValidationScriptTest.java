package com.google.fleetengine.auth.sample.validation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.AuthTokenMinter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DeliveryServerTokenValidationScriptTest {
  private static final String TEST_FLEET_ENGINE_ADDRESS = "test.fleetengine.address:123";
  private static final String TEST_PROVIDER_ID = "test-provider-id";

  private SampleScriptRuntime runtime;
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
  public void run_callsDeliveryServiceCommands() throws Throwable {
    DeliveryServerTokenValidationScript script =
        new DeliveryServerTokenValidationScript(runtime, configuration, commandsFactory);
    script.run();

    verify(deliveryServiceCommands).getDeliveryVehicle(any());
    verify(deliveryServiceCommands).createDeliveryVehicle(any());
    verify(deliveryServiceCommands).createTask(any(), any());
    verify(deliveryServiceCommands).completeTask(any());
  }
}
