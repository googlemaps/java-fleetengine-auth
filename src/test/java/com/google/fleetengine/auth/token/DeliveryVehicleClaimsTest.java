package com.google.fleetengine.auth.token;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DeliveryVehicleClaimsTest {
  private static final String WILDCARD = "*";
  private static final String CLAIM_DELIVERY_VEHICLE_ID = "deliveryvehicleid";
  private static final String TEST_DELIVERY_VEHICLE_ID = "test_delivery_vehicle_id";

  @Test
  public void create_withWildcard() {
    DeliveryVehicleClaims claims = DeliveryVehicleClaims.create();

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_DELIVERY_VEHICLE_ID, WILDCARD);
    assertThat(claims.isWildcard()).isTrue();
  }

  @Test
  public void create_withId() {
    DeliveryVehicleClaims claims = DeliveryVehicleClaims.create(TEST_DELIVERY_VEHICLE_ID);

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_DELIVERY_VEHICLE_ID, TEST_DELIVERY_VEHICLE_ID);
    assertThat(claims.isWildcard()).isFalse();
  }
}
