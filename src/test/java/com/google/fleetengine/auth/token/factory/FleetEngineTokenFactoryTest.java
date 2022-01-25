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

package com.google.fleetengine.auth.token.factory;

import static com.google.common.truth.Truth.assertThat;
import static com.google.fleetengine.auth.token.FleetEngineTokenType.CONSUMER;
import static com.google.fleetengine.auth.token.FleetEngineTokenType.CUSTOM;
import static com.google.fleetengine.auth.token.FleetEngineTokenType.DRIVER;
import static com.google.fleetengine.auth.token.FleetEngineTokenType.SERVER;
import static com.google.fleetengine.auth.token.factory.FleetEngineTokenFactory.TOKEN_EXPIRATION;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.token.DeliveryVehicleClaims;
import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.ServerTokenClaims;
import com.google.fleetengine.auth.token.TaskClaims;
import com.google.fleetengine.auth.token.TripClaims;
import com.google.fleetengine.auth.token.VehicleClaims;
import java.time.Clock;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FleetEngineTokenFactoryTest {

  private static final Instant ISSUED_INSTANT = Instant.EPOCH;
  private static final String TEST_VEHICLE_ID = "Vehicle Id to test with";
  private static final String TEST_TRIP_ID = "Trip Id to test with";
  private static final String TEST_TASK_ID = "Task Id to test with";
  private static final String FAKE_AUDIENCE = "https://fake.crowd";
  private static final String CLAIM_DELIVERY_VEHICLE_ID = "deliveryvehicleid";
  private static final String CLAIM_TASK_ID = "taskid";


  private Clock clock;

  @Before
  public void setup() {
    clock = mock(Clock.class);
    when(this.clock.instant()).thenReturn(ISSUED_INSTANT);
  }

  @Test
  public void createServerToken_returnsSameValues() {
    // Puts the JWT on the fleet engine token
    FleetEngineTokenFactory factory =
        new FleetEngineTokenFactory(clock, FleetEngineTokenFactorySettings.builder().build());

    FleetEngineToken token = factory.createServerToken();

    assertThat(token.tokenType()).isEqualTo(SERVER);
    assertThat(token.authorizationClaims()).isEqualTo(ServerTokenClaims.create());
    assertThat(token.creationTimestamp().toInstant()).isEqualTo(ISSUED_INSTANT);
    assertThat(token.expirationTimestamp().toInstant())
        .isEqualTo(ISSUED_INSTANT.plus(TOKEN_EXPIRATION));
  }

  @Test
  public void createVehicleToken_returnsVehicleTokenType() {
    FleetEngineTokenFactory factory =
        new FleetEngineTokenFactory(clock, FleetEngineTokenFactorySettings.builder().build());

    FleetEngineToken token = factory.createDriverToken(VehicleClaims.create(TEST_VEHICLE_ID));

    assertThat(token.tokenType()).isEqualTo(DRIVER);
  }

  @Test
  public void createConsumerToken_returnsConsumerTokenType() {
    FleetEngineTokenFactory factory =
        new FleetEngineTokenFactory(clock, FleetEngineTokenFactorySettings.builder().build());

    FleetEngineToken signedToken = factory.createConsumerToken(TripClaims.create(TEST_TRIP_ID));

    assertThat(signedToken.tokenType()).isEqualTo(CONSUMER);
  }

  @Test
  public void createCustomToken_returnsCustomTokenType() {
    FleetEngineTokenFactory factory =
        new FleetEngineTokenFactory(clock, FleetEngineTokenFactorySettings.builder().build());

    FleetEngineToken signedToken = factory.createCustomToken(TripClaims.create(TEST_TRIP_ID));

    assertThat(signedToken.tokenType()).isEqualTo(CUSTOM);
  }

  @Test
  public void createTripToken_returnsAudienceFromSettings() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    FleetEngineToken signedToken = factory.createConsumerToken(TripClaims.create());

    assertThat(signedToken.audience()).isEqualTo(FAKE_AUDIENCE);
  }

  @Test
  public void createDriverToken_whenNullVehicleClaim_throwsException() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    assertThrows(NullPointerException.class, () -> factory.createDriverToken((VehicleClaims) null));
  }

  @Test
  public void createConsumerToken_whenNullTripClaim_throwsException() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    assertThrows(NullPointerException.class,
        () -> factory.createConsumerToken((TripClaims) null));
  }

  @Test
  public void createCustomToken_whenNullClaim_throwsException() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    assertThrows(NullPointerException.class,
        () -> factory.createCustomToken(null));
  }

  @Test
  public void createTrustedDeliveryDriverToken_whenVehicleClaimsOrTaskClaimsNull_throwsException() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    assertThrows(NullPointerException.class,
        () -> factory.createTrustedDeliveryDriverToken(DeliveryVehicleClaims.create(), null));
    assertThrows(NullPointerException.class,
        () -> factory.createTrustedDeliveryDriverToken(null, TaskClaims.create()));
  }

  @Test
  public void createTrustedDriverToken_whenVehicleClaimsAndTaskClaimsAreWild_claimIsWild() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    FleetEngineToken token = factory.createTrustedDeliveryDriverToken(
        DeliveryVehicleClaims.create(), TaskClaims.create());
    assertThat(token.authorizationClaims().isWildcard()).isTrue();
  }

  @Test
  public void createTrustedDriverToken_whenVehicleClaimsNOTWildAndTaskClaimsWild_claimIsNOTWild() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    FleetEngineToken token = factory.createTrustedDeliveryDriverToken(
        DeliveryVehicleClaims.create(TEST_VEHICLE_ID), TaskClaims.create());
    assertThat(token.authorizationClaims().isWildcard()).isFalse();
  }

  @Test
  public void createTrustedDriverToken_whenVehicleClaimsWildAndTaskClaimsNOTWild_claimIsNOTWild() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    FleetEngineToken token = factory.createTrustedDeliveryDriverToken(
        DeliveryVehicleClaims.create(), TaskClaims.create(TEST_TASK_ID));
    assertThat(token.authorizationClaims().isWildcard()).isFalse();
  }

  @Test
  public void createTrustedDriverToken_whenVehicleClaimsAndTaskClaimsSet_claimValuesCorrect() {
    FleetEngineTokenFactorySettings settings =
        FleetEngineTokenFactorySettings.builder().setAudience(FAKE_AUDIENCE).build();
    FleetEngineTokenFactory factory = new FleetEngineTokenFactory(clock, settings);

    FleetEngineToken token = factory.createTrustedDeliveryDriverToken(
        DeliveryVehicleClaims.create(TEST_VEHICLE_ID), TaskClaims.create(TEST_TASK_ID));
    assertThat(token.authorizationClaims().toMap()).containsEntry(CLAIM_DELIVERY_VEHICLE_ID, TEST_VEHICLE_ID);
    assertThat(token.authorizationClaims().toMap()).containsEntry(CLAIM_TASK_ID, TEST_TASK_ID);
  }
}
