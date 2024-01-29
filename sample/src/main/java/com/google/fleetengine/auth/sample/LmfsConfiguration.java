package com.google.fleetengine.auth.sample;

public class LmfsConfiguration {
  // XXX_TOKEN_ACCOUNT are the names of the service accounts with the Fleet
  // Engine pre defined roles.  The name is always in the format of:
  // <service account name>@<project id>.iam.gserviceaccount.com

  // Set to service account with the Fleet Engine Delivery Super User SDK role.
  public static final String DELIVERY_SERVER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Delivery Consumer User SDK role.
  public static final String DELIVERY_CONSUMER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Delivery Untrusted Driver SDK role.
  public static final String DELIVERY_UNTRUSTED_DRIVER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Delivery Trusted Driver SDK role.
  public static final String DELIVERY_TRUSTED_DRIVER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Delivery Fleet Read SDK role.
  public static final String DELIVERY_FLEET_READER_TOKEN_ACCOUNT =
      "<service account name>@<project id>.iam.gserviceaccount.com";

  // Set to service account with the Fleet Engine Fleet Read SDK role.
  public static final String FLEET_READER_TOKEN_ACCOUNT =
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

  private LmfsConfiguration() {}
}
