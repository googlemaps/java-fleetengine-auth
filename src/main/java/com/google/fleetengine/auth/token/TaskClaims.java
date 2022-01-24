package com.google.fleetengine.auth.token;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/** Custom authorization claims for task endpoint. */
public class TaskClaims implements FleetEngineTokenClaims {
  private static final String WILDCARD = "*";
  static final String CLAIM_TASK_ID = "taskid";

  private final ImmutableMap<String, String> map;
  private final boolean isWildcardClaim;

  /**
   * Creates a task claim that works with any task.
   *
   * @return custom auth claim
   */
  public static TaskClaims create() {
    return new TaskClaims(WILDCARD, true);
  }

  /**
   * Creates a task claim with an id.
   *
   * @param taskId id of the task id on the token scope
   * @return custom auth claim
   * @throws IllegalArgumentException when taskId is null or empty
   */
  public static TaskClaims create(String taskId) {
    if (Strings.isNullOrEmpty(taskId)) {
      throw new IllegalArgumentException("taskId must have a value");
    }
    return new TaskClaims(taskId, false);
  }

  private TaskClaims(String taskId, boolean isWildcard) {
    map = ImmutableMap.of(CLAIM_TASK_ID, taskId);
    isWildcardClaim = isWildcard;
  }

  /** {@inheritDoc} */
  @Override
  public ImmutableMap<String, String> toMap() {
    return map;
  }

  @Override
  public boolean isWildcard() {
    return isWildcardClaim;
  }
}
