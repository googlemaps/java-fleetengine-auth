package com.google.fleetengine.auth.token;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TrackingClaimsTest {
  private static final String WILDCARD = "*";
  private static final String CLAIM_TRACKING_ID = "trackingid";
  private static final String TEST_TRACKING_ID = "test_tracking_id";

  @Test
  public void create_withWildcard() {
    TrackingClaims claims = TrackingClaims.create();

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_TRACKING_ID, WILDCARD);
    assertThat(claims.isWildcard()).isTrue();
  }

  @Test
  public void create_withId() {
    TrackingClaims claims = TrackingClaims.create(TEST_TRACKING_ID);

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_TRACKING_ID, TEST_TRACKING_ID);
    assertThat(claims.isWildcard()).isFalse();
  }
}
