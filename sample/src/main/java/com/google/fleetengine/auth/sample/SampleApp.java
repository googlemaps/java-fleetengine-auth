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

package com.google.fleetengine.auth.sample;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Scanner;

/** Sample app for auth library. */
public final class SampleApp {
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_RESET = "\u001B[0m";

  private SampleApp() {}

  /** Entry point. */
  public static void main(String[] args) throws Throwable {
    while (true) {
      System.out.println(
        "\n\n\n=== Choose example: ===\n"
            + "0. Validate Configured ODRD Roles\n"
            + "1. Create Vehicle\n"
            + "2. Create Trip\n"
            + "3. List Vehicles\n"
            + "4. Search for Vehicles\n"
            + "5. Search for Trips\n"
            + "----------------------------------------\n"
            + "10. Validate Configured LMFS Roles\n"
            + "11. Create Delivery Vehicle\n"
            + "12. Create Task\n"
            + "13. List Delivery Vehicles\n"
            + "----------------------------------------\n"
            + "100. Run all commands\n");
      Scanner scanner = new Scanner(System.in, UTF_8.name());
      int choice = scanner.nextInt();
      switch (choice) {
        case 0:
          ValidateOdrdRoles.run();
          break;
        case 1:
          OdrdSampleCommands.createVehicle();
          break;
        case 2:
          OdrdSampleCommands.createTrip();
          break;
        case 3:
          OdrdSampleCommands.listVehicles();
          break;
        case 4:
          OdrdSampleCommands.searchVehicles();
          break;
        case 5:
          OdrdSampleCommands.searchTrips();
          break;
        case 10:
          ValidateLmfsRoles.run();
          break;
        case 11:
          LmfsSampleCommands.createDeliveryVehicle();
          break;
        case 12:
          LmfsSampleCommands.createTask();
          break;
        case 13:
          LmfsSampleCommands.listDeliveryVehicles();
          break;
        case 100:
          runAll();
          break;
        default:
          throw new IllegalArgumentException("Invalid choice provided.");
      }
    }
  }

  private static void runAll() throws Throwable {
    try {
      ValidateOdrdRoles.run();
      OdrdSampleCommands.createVehicle();
      OdrdSampleCommands.createTrip();
      OdrdSampleCommands.listVehicles();
      OdrdSampleCommands.searchVehicles();
      OdrdSampleCommands.searchTrips();
      ValidateLmfsRoles.run();
      LmfsSampleCommands.createDeliveryVehicle();
      LmfsSampleCommands.createTask();
      LmfsSampleCommands.listDeliveryVehicles();
    } catch (Exception ex) {
      System.out.printf("%sRUN ALL FAILED, EXITING...%s\n", ANSI_RED, ANSI_RESET);
      throw ex;
    }
    System.out.printf("%sRUN ALL SUCCEEDED%s\n", ANSI_GREEN, ANSI_RESET);
  }
}
