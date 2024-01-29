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

package com.google.fleetengine.auth;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.token.DeliveryVehicleClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import com.google.fleetengine.auth.token.TaskClaims;
import com.google.fleetengine.auth.token.TripClaims;
import com.google.fleetengine.auth.token.VehicleClaims;
import com.google.fleetengine.auth.token.factory.TokenFactory;
import com.google.fleetengine.auth.token.factory.signer.Signer;
import com.google.fleetengine.auth.token.factory.signer.SigningTokenException;
import java.time.Instant;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AuthTokenMinterTest {
  private static final VehicleClaims FAKE_VEHICLE = VehicleClaims.create("fakeVehicleId");
  private static final TripClaims FAKE_TRIP = TripClaims.create("fakeTripId");
  private static final TaskClaims FAKE_TASK = TaskClaims.create("fakeTaskId");
  private static final TaskClaims FAKE_TRACKING = TaskClaims.create("fakeTrackingId");
  private static final DeliveryVehicleClaims FAKE_DELIVERY_VEHICLE =
      DeliveryVehicleClaims.create("fakeDeliveryVehicleId");

  private Signer serverSigner;
  private Signer driverSigner;
  private Signer consumerSigner;
  private Signer deliveryServerSigner;
  private Signer deliveryConsumerSigner;
  private Signer untrustedDeliveryDriverSigner;
  private Signer trustedDeliveryDriverSigner;
  private Signer deliveryFleetReaderSigner;
  private Signer fleetReaderSigner;
  private Signer customSigner;
  private FleetEngineAuthTokenStateManager authStateManager;
  private AuthTokenMinter.Builder defaultFleetEngineAuthBuilder;
  private TokenFactory tokenFactory;
  private FleetEngineToken fleetEngineToken;

  @Before
  public void setup() {
    this.serverSigner = mock(Signer.class);
    this.driverSigner = mock(Signer.class);
    this.consumerSigner = mock(Signer.class);
    this.deliveryServerSigner = mock(Signer.class);
    this.deliveryConsumerSigner = mock(Signer.class);
    this.untrustedDeliveryDriverSigner = mock(Signer.class);
    this.trustedDeliveryDriverSigner = mock(Signer.class);
    this.deliveryFleetReaderSigner = mock(Signer.class);
    this.fleetReaderSigner = mock(Signer.class);
    this.customSigner = mock(Signer.class);
    this.authStateManager = mock(FleetEngineAuthTokenStateManager.class);
    this.tokenFactory = mock(TokenFactory.class);

    this.defaultFleetEngineAuthBuilder =
        AuthTokenMinter.builder()
            .setServerSigner(serverSigner)
            .setDeliveryServerSigner(deliveryServerSigner)
            .setDeliveryFleetReaderSigner(deliveryFleetReaderSigner)
            .setTokenStateManager(authStateManager)
            .setTokenFactory(tokenFactory);

    fleetEngineToken =
        FleetEngineToken.builder()
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .setTokenType(FleetEngineTokenType.SERVER)
            .build();
  }

  @Test
  public void getSignedToken_whenServerSignerSet_signsWithSetServerSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setServerSigner(serverSigner).build();
    when(tokenFactory.createServerToken()).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getServerToken();

    verify(authStateManager, times(1)).signToken(eq(serverSigner), eq(fleetEngineToken));
  }

  @Test
  public void getSignedToken_whenDeliveryBuilder_signsWithSetDeliveryServerSigner()
      throws SigningTokenException {
    AuthTokenMinter.Builder defaultDeliveryFleetEngineAuthBuilder =
        // Use delivery builder method
        AuthTokenMinter.deliveryBuilder()
            .setDeliveryServerSigner(deliveryServerSigner)
            .setTokenStateManager(authStateManager)
            .setTokenFactory(tokenFactory);

    AuthTokenMinter baseFleetEngineAuth =
        defaultDeliveryFleetEngineAuthBuilder.setDeliveryServerSigner(deliveryServerSigner).build();
    when(tokenFactory.createDeliveryServerToken()).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getSignedToken();

    verify(authStateManager, times(1)).signToken(eq(deliveryServerSigner), eq(fleetEngineToken));
  }

  @Test
  public void getServerToken_whenServerSignerSet_signsWithSetServerSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setServerSigner(serverSigner).build();
    when(tokenFactory.createServerToken()).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getServerToken();

    verify(authStateManager, times(1)).signToken(eq(serverSigner), eq(fleetEngineToken));
  }

  @Test
  public void getDriverToken_whenDriverSignerSet_signsWithSetDriverSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDriverSigner(driverSigner).build();
    when(tokenFactory.createDriverToken(eq(FAKE_VEHICLE))).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getDriverToken(FAKE_VEHICLE);

    verify(tokenFactory, times(1)).createDriverToken(eq(FAKE_VEHICLE));
    verify(authStateManager, times(1)).signToken(driverSigner, fleetEngineToken);
  }

  @Test
  public void getConsumerToken_whenConsumerSignerSet_signsWithSetConsumerSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setConsumerSigner(consumerSigner).build();
    when(tokenFactory.createConsumerToken(eq(FAKE_TRIP))).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getConsumerToken(FAKE_TRIP);

    verify(tokenFactory, times(1)).createConsumerToken(eq(FAKE_TRIP));
    verify(authStateManager, times(1)).signToken(consumerSigner, fleetEngineToken);
  }

  @Test
  public void getDeliveryServerToken_whenDeliveryServerSignerSet_signsWithSetDeliveryServerSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDeliveryServerSigner(deliveryServerSigner).build();
    when(tokenFactory.createDeliveryServerToken()).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getDeliveryServerToken();

    verify(authStateManager, times(1)).signToken(eq(deliveryServerSigner), eq(fleetEngineToken));
  }

  @Test
  public void
      getDeliveryConsumerToken_whenDeliveryConsumerSignerSet_signsWithSetDeliveryConsumerSigner()
          throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDeliveryConsumerSigner(deliveryConsumerSigner).build();
    when(tokenFactory.createDeliveryConsumerToken(eq(FAKE_TASK))).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getDeliveryConsumerToken(FAKE_TASK);

    verify(tokenFactory, times(1)).createDeliveryConsumerToken(eq(FAKE_TASK));
    verify(authStateManager, times(1)).signToken(deliveryConsumerSigner, fleetEngineToken);
  }

  @Test
  public void
  getDeliveryConsumerToken_whenDeliveryConsumerSignerSet_signsWithSetDeliveryConsumerSignerAndTrackingId()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDeliveryConsumerSigner(deliveryConsumerSigner).build();
    when(tokenFactory.createDeliveryConsumerToken(eq(FAKE_TRACKING))).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getDeliveryConsumerToken(FAKE_TRACKING);

    verify(tokenFactory, times(1)).createDeliveryConsumerToken(eq(FAKE_TRACKING));
    verify(authStateManager, times(1)).signToken(deliveryConsumerSigner, fleetEngineToken);
  }

  @Test
  public void
      getUntrustedDeliveryDriverToken_whenUntrustedDeliveryDriverSignerSet_signsWithSetDeliveryDriverSigner()
          throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder
            .setUntrustedDeliveryDriverSigner(untrustedDeliveryDriverSigner)
            .build();
    when(tokenFactory.createUntrustedDeliveryDriverToken(eq(FAKE_DELIVERY_VEHICLE)))
        .thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getUntrustedDeliveryVehicleToken(FAKE_DELIVERY_VEHICLE);

    verify(tokenFactory, times(1)).createUntrustedDeliveryDriverToken(eq(FAKE_DELIVERY_VEHICLE));
    verify(authStateManager, times(1)).signToken(untrustedDeliveryDriverSigner, fleetEngineToken);
  }

  @Test
  public void
  getTrustedDeliveryDriverToken_whenTrustedDeliveryDriverSignerSet_signsWithSetDeliveryDriverSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder
            .setTrustedDeliveryDriverSigner(trustedDeliveryDriverSigner)
            .build();
    when(tokenFactory.createTrustedDeliveryDriverToken(eq(FAKE_DELIVERY_VEHICLE)))
        .thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getTrustedDeliveryVehicleToken(FAKE_DELIVERY_VEHICLE);

    verify(tokenFactory, times(1)).createTrustedDeliveryDriverToken(eq(FAKE_DELIVERY_VEHICLE));
    verify(authStateManager, times(1)).signToken(trustedDeliveryDriverSigner, fleetEngineToken);
  }

  @Test
  public void
      getDeliveryFleetReaderToken_whenDeliveryFleetReaderSignerSet_signsWithSetDeliveryFleetReaderSigner()
          throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder
            .setDeliveryFleetReaderSigner(deliveryFleetReaderSigner)
            .build();
    when(tokenFactory.createDeliveryFleetReaderToken()).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getDeliveryFleetReaderToken();

    verify(authStateManager, times(1))
        .signToken(eq(deliveryFleetReaderSigner), eq(fleetEngineToken));
  }

  @Test
  public void getFleetReaderToken_whenFleetReaderSignerSet_signsWithSetFleetReaderSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setFleetReaderSigner(fleetReaderSigner).build();
    when(tokenFactory.createFleetReaderToken()).thenReturn(fleetEngineToken);

    var unused = baseFleetEngineAuth.getFleetReaderToken();

    verify(authStateManager, times(1)).signToken(eq(fleetReaderSigner), eq(fleetEngineToken));
  }

  @Test
  public void getCustomToken_whenCustomSignerSet_signsWithSetCustomSigner()
      throws SigningTokenException {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setCustomSigner(customSigner).build();
    when(tokenFactory.createCustomToken(eq(FAKE_TRIP))).thenReturn(fleetEngineToken);

    baseFleetEngineAuth.getCustomToken(FAKE_TRIP);

    verify(tokenFactory, times(1)).createCustomToken(eq(FAKE_TRIP));
    verify(authStateManager, times(1)).signToken(customSigner, fleetEngineToken);
  }

  @Test
  public void getDriverToken_whenDriverSignerNull_throwsSigningTokenException() {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDriverSigner(null).build();

    Assert.assertThrows(
        SigningTokenException.class, () -> baseFleetEngineAuth.getDriverToken(FAKE_VEHICLE));
  }

  @Test
  public void getConsumerToken_whenConsumerSignerNull_throwsSigningTokenException() {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setConsumerSigner(null).build();

    Assert.assertThrows(
        SigningTokenException.class, () -> baseFleetEngineAuth.getConsumerToken(FAKE_TRIP));
  }

  @Test
  public void getCustomToken_whenCustomSignerNull_throwsSigningTokenException() {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setCustomSigner(null).build();

    Assert.assertThrows(
        SigningTokenException.class, () -> baseFleetEngineAuth.getCustomToken(FAKE_TRIP));
  }

  @Test
  public void
      getDeliveryConsumerToken_whenDeliveryConsumerSignerNull_throwsSigningTokenException() {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setDeliveryConsumerSigner(null).build();

    Assert.assertThrows(
        SigningTokenException.class, () -> baseFleetEngineAuth.getDeliveryConsumerToken(FAKE_TASK));
  }

  @Test
  public void
      getUntrustedDeliveryDriverToken_whenUntrustedDeliveryDriverSignerNull_throwsSigningTokenException() {
    AuthTokenMinter baseFleetEngineAuth =
        defaultFleetEngineAuthBuilder.setUntrustedDeliveryDriverSigner(null).build();

    Assert.assertThrows(
        SigningTokenException.class,
        () -> baseFleetEngineAuth.getUntrustedDeliveryVehicleToken(null));
  }
}
