package com.google.fleetengine.auth.token;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/** Custom authorization claims for task endpoint with a tracking id. */
public class TrackingClaims implements FleetEngineTokenClaims {
  private static final String WILDCARD = "*";
  static final String CLAIM_TRACKING_ID = "trackingid";

  private final ImmutableMap<String, String> map;
  private final boolean isWildcardClaim;

  /**
   * Creates a tracking claim that is authorized to search across all tasks.
   *
   * @return custom auth claim
   */
  public static TrackingClaims create() {
    return new TrackingClaims(WILDCARD, true);
  }

  /**
   * Creates a tracking claim with an id.
   *
   * @param trackingId id of the tracking id on the token scope
   * @return custom auth claim
   * @throws IllegalArgumentException when trackingId is null or empty
   */
  public static TrackingClaims create(String trackingId) {
    if (Strings.isNullOrEmpty(trackingId)) {
      throw new IllegalArgumentException("trackingId must have a value");
    }
    return new TrackingClaims(trackingId, false);
  }

  private TrackingClaims(String trackingId, boolean isWildcard) {
    map = ImmutableMap.of(CLAIM_TRACKING_ID, trackingId);
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
