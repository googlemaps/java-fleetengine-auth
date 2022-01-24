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

package com.google.fleetengine.auth.token;

/** Representation of the different kinds of roles accepted by Fleet Engine on tokens. */
public enum FleetEngineTokenType {
  /**
   * Consumer tokens are usually service accounts associated with the Fleet Engine Consumer SDK User
   * role on the Google Cloud project.
   */
  CONSUMER,

  /**
   * Server tokens are usually service accounts associated with the Fleet Engine Super User role on
   * the Google Cloud project.
   */
  SERVER,

  /**
   * Driver tokens are usually service accounts associated with the Fleet Engine Driver SDK User
   * role on the Google Cloud project.
   */
  DRIVER,

  /**
   * Delivery server tokens are usually service accounts associated with the Fleet Engine Delivery
   * Super User role on the Google Cloud project.
   */
  DELIVERY_SERVER,

  /**
   * Driver tokens are usually service accounts associated with the Fleet Engine Delivery Consumer
   * User role on the Google Cloud project.
   */
  DELIVERY_CONSUMER,

  /**
   * Driver tokens are usually service accounts associated with the Fleet Engine Delivery Untrusted
   * Driver User role on the Google Cloud project.
   */
  UNTRUSTED_DELIVERY_DRIVER,

  /**
   * Driver tokens are usually service accounts associated with the Fleet Engine Delivery Trusted
   * Driver User role on the Google Cloud project.
   */
  TRUSTED_DELIVERY_DRIVER,

  /**
   * Fleet reader tokens are usually service accounts associated with the Fleet Engine Delivery
   * Fleet Reader User role on the Google Cloud project.
   */
  DELIVERY_FLEET_READER,

  /** Custom token type associated with any Fleet Engine Role on the Google Cloud project. */
  CUSTOM,
}
