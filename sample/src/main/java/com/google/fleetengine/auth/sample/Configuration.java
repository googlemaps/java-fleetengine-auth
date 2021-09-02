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

class Configuration {

  // XXX_TOKEN_ACCOUNT are the names of the service accounts with the Fleet
  // Engine pre defined roles.  The name is always in the format of:
  // <service account name>@<project id>.iam.gserviceaccount.com

  // Set to service account with the Fleet Engine Super User role.
  public static final String SERVER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Consumer SDK role.
  public static final String CONSUMER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Driver SDK role.
  public static final String DRIVER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";


  // Provider Id is the same as your GCP Project Id.
  public static final String PROVIDER_ID = "<project id>";

  // Endpoint of the Fleet Engine address.
  // In most cases, the default of fleetengine.googleapis.com:443 is correct
  public static final String FLEET_ENGINE_ADDRESS =
      "fleetengine.googleapis.com:443";

  // Audience of the JWT token.
  // The address is the same as the FLEET_ENGINE_ADDRESS without the port number.
  public static final String FLEET_ENGINE_AUDIENCE =
      "https://fleetengine.googleapis.com/";

  private Configuration() {}
}
