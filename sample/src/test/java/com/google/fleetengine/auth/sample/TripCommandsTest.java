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

package com.google.fleetengine.auth.sample.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.maps.fleetengine.v1.CreateTripRequest;
import com.google.maps.fleetengine.v1.GetTripRequest;
import com.google.maps.fleetengine.v1.UpdateTripRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

@RunWith(JUnit4.class)
public class TripCommandsTest {
  private static final String TEST_PROVIDER_ID = "test-provider-id";
  private static final String TEST_PROVIDER_NAME = "providers/test-provider-id";
  private static final String TEST_TRIP_ID = "test-trip-id";
  private static final String TEST_TRIP_NAME = "providers/test-provider-id/trips/test-trip-id";

  private TripStub tripStub;

  @Before
  public void setup() {
    tripStub = mock(TripStub.class);
  }

  @Test
  public void create_hasCorrectAttributesOnRequest() {
    TripCommands tripCom = new TripCommands(tripStub, TEST_PROVIDER_ID);

    tripCom.createTrip(TEST_TRIP_ID);

    ArgumentCaptor<CreateTripRequest> requestCaptor =
        ArgumentCaptor.forClass(CreateTripRequest.class);
    verify(tripStub).createTrip(requestCaptor.capture());
    assertEquals(TEST_TRIP_ID, requestCaptor.getValue().getTripId());
    assertEquals(TEST_PROVIDER_NAME, requestCaptor.getValue().getParent());
    assertEquals(TEST_TRIP_NAME, requestCaptor.getValue().getTrip().getName());
  }

  @Test
  public void get_hasCorrectAttributesOnRequest() {
    TripCommands tripCommands = new TripCommands(tripStub, TEST_PROVIDER_ID);

    tripCommands.getTrip(TEST_TRIP_ID);

    ArgumentCaptor<GetTripRequest> requestCaptor = ArgumentCaptor.forClass(GetTripRequest.class);
    verify(tripStub).getTrip(requestCaptor.capture());
    assertEquals(TEST_TRIP_NAME, requestCaptor.getValue().getName());
  }

  @Test
  public void complete_hasCorrectAttributesOnRequest() {
    TripCommands tripCommands = new TripCommands(tripStub, TEST_PROVIDER_ID);

    tripCommands.completeTrip(TEST_TRIP_ID);

    ArgumentCaptor<UpdateTripRequest> requestCaptor =
        ArgumentCaptor.forClass(UpdateTripRequest.class);
    verify(tripStub).updateTrip(requestCaptor.capture());
    assertEquals(TEST_TRIP_NAME, requestCaptor.getValue().getName());
  }
}
