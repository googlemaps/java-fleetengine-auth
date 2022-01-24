package com.google.fleetengine.auth.token;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TaskClaimsTest {
  private static final String WILDCARD = "*";
  private static final String CLAIM_DELIVERY_TASK_ID = "taskid";
  private static final String TEST_DELIVERY_TASK_ID = "test_delivery_task_id";

  @Test
  public void create_withWildcard() {
    TaskClaims claims = TaskClaims.create();

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_DELIVERY_TASK_ID, WILDCARD);
    assertThat(claims.isWildcard()).isTrue();
  }

  @Test
  public void create_withId() {
    TaskClaims claims = TaskClaims.create(TEST_DELIVERY_TASK_ID);

    ImmutableMap<String, String> returnedToken = claims.toMap();

    assertThat(returnedToken).containsEntry(CLAIM_DELIVERY_TASK_ID, TEST_DELIVERY_TASK_ID);
    assertThat(claims.isWildcard()).isFalse();
  }
}
