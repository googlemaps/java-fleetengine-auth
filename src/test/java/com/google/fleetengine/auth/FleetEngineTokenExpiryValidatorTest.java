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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.fleetengine.auth.token.FleetEngineToken;
import com.google.fleetengine.auth.token.FleetEngineTokenType;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FleetEngineTokenExpiryValidatorTest {

  private FleetEngineToken defaultToken;
  private Clock fakeNowClock;

  @Before
  public void setup() {
    this.fakeNowClock = mock(Clock.class);

    // Setup default token, expiration timestamp will be changed in each test method.
    defaultToken =
        FleetEngineToken.builder()
            .setCreationTimestamp(Date.from(Instant.EPOCH))
            .setExpirationTimestamp(Date.from(Instant.EPOCH))
            .setAuthorizationClaims(EmptyFleetEngineTokenClaims.INSTANCE)
            .setTokenType(FleetEngineTokenType.SERVER)
            .build();
  }

  @Test
  public void isTokenExpired_whenBeforeExpirationWindow_tokenIsNotExpired() {
    Duration expirationWindowDuration = Duration.ofDays(1);
    FleetEngineToken expiredToken =
        defaultToken.toBuilder().setExpirationTimestamp(parseDate("2020-06-15")).build();
    Instant now = parseDateTime("2020-06-13T23:00:00.00Z").toInstant();
    when(fakeNowClock.instant()).thenReturn(now);
    FleetEngineTokenExpiryValidator expiryValidator =
        new FleetEngineTokenExpiryValidator(fakeNowClock);

    boolean isTokenExpired = expiryValidator.isTokenExpired(expiredToken, expirationWindowDuration);

    assertThat(isTokenExpired).isFalse();
  }

  @Test
  public void isTokenExpired_whenAfterExpiration_tokenIsExpired() {
    // Expiration window irrelevant since we are testing AFTER expiration
    Duration expirationWindowDuration = Duration.ZERO;
    FleetEngineToken expiredToken =
        defaultToken.toBuilder().setExpirationTimestamp(parseDate("2020-06-15")).build();
    Instant now = parseDate("2020-06-16").toInstant();
    when(fakeNowClock.instant()).thenReturn(now);
    FleetEngineTokenExpiryValidator expiryValidator =
        new FleetEngineTokenExpiryValidator(fakeNowClock);

    boolean isTokenExpired = expiryValidator.isTokenExpired(expiredToken, expirationWindowDuration);

    assertThat(isTokenExpired).isTrue();
  }

  @Test
  public void isTokenExpired_whenInExpirationWindow_tokenIsExpired() {
    Duration expirationWindowDuration = Duration.ofDays(1);
    FleetEngineToken expiredToken =
        defaultToken.toBuilder().setExpirationTimestamp(parseDate("2020-06-15")).build();
    Instant now = parseDateTime("2020-06-15T01:00:00.00Z").toInstant();
    when(fakeNowClock.instant()).thenReturn(now);
    FleetEngineTokenExpiryValidator expiryValidator =
        new FleetEngineTokenExpiryValidator(fakeNowClock);

    boolean isTokenExpired = expiryValidator.isTokenExpired(expiredToken, expirationWindowDuration);

    assertThat(isTokenExpired).isTrue();
  }

  private static Date parseDate(String date) {
    return parseDateTime(date + "T00:00:00.00Z");
  }

  private static Date parseDateTime(String dateTime) {
    return Date.from(Instant.parse(dateTime));
  }
}
