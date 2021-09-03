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

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/maps/fleetengine/v1/vehicle_api.proto

package google.maps.fleetengine.v1;

public final class VehicleApi {
  private VehicleApi() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_CreateVehicleRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_CreateVehicleRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_GetVehicleRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_GetVehicleRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_UpdateVehicleRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_UpdateVehicleRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_UpdateVehicleLocationRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_UpdateVehicleLocationRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_UpdateVehicleAttributesRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_UpdateVehicleAttributesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_UpdateVehicleAttributesResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_UpdateVehicleAttributesResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_SearchVehiclesRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_SearchVehiclesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_SearchVehiclesResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_SearchVehiclesResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_ListVehiclesRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_ListVehiclesRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_ListVehiclesResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_ListVehiclesResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_Waypoint_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_Waypoint_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_VehicleMatch_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_VehicleMatch_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_maps_fleetengine_v1_VehicleAttributeList_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_maps_fleetengine_v1_VehicleAttributeList_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n"
          + ",google/maps/fleetengine/v1/vehicle_api"
          + ".proto\022\023maps.fleetengine.v1\032\034google/api/"
          + "annotations.proto\032\037google/api/field_beha"
          + "vior.proto\032\031google/api/resource.proto\032,g"
          + "oogle/maps/fleetengine/v1/fleetengine.proto\032\'google/maps/fleetengine/v1/header.p"
          + "roto\032)google/maps/fleetengine/v1/vehicle"
          + "s.proto\032\036google/protobuf/duration.proto\032"
          + " google/protobuf/field_mask.proto\032\037google/protobuf/timestamp.proto\032\036google/protobuf/wrappers.proto\032\030google/type/latlng.proto\032\027google/api/client.proto\"\254\001\n"
          + "\024CreateVehicleRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\023\n"
          + "\006parent\030\003 \001(\tB\003\340A\002\022\027\n\n"
          + "vehicle_id\030\004 \001(\tB\003\340A\002\0222\n"
          + "\007vehicle\030\005"
          + " \001(\0132\034.maps.fleetengine.v1.VehicleB\003\340A\002\"\373\001\n"
          + "\021GetVehicleRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\0228\n"
          + "\004name\030\003 \001(\tB*\340A\002\372A$\n"
          + "\"fleetengine.googleapis.com/Vehicle\022A\n"
          + "\035current_route_segment_version\030\004"
          + " \001(\0132\032.google.protobuf.Timestamp\0225\n"
          + "\021waypoints_version\030\005 \001(\0132\032.google.protobuf.Timestamp\"\307\001\n"
          + "\024UpdateVehicleRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\021\n"
          + "\004name\030\003 \001(\tB\003\340A\002\0222\n"
          + "\007vehicle\030\004 \001(\0132\034.maps.fleetengine.v1.VehicleB\003\340A\002\0224\n"
          + "\013update_mask\030\005"
          + " \001(\0132\032.google.protobuf.FieldMaskB\003\340A\002\"\350\001\n"
          + "\034UpdateVehicleLocationRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\021\n"
          + "\004name\030\003 \001(\tB\003\340A\002\022C\n"
          + "\020current_location\030\004"
          + " \001(\0132$.maps.fleetengine.v1.VehicleLocationB\003\340A\002\0228\n\r"
          + "current_state\030\005"
          + " \001(\0162!.maps.fleetengine.v1.VehicleState:\002\030\001\"\247\001\n"
          + "\036UpdateVehicleAttributesRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\021\n"
          + "\004name\030\003 \001(\tB\003\340A\002\022>\n\n"
          + "attributes\030\004"
          + " \003(\0132%.maps.fleetengine.v1.VehicleAttributeB\003\340A\002\"a\n"
          + "\037UpdateVehicleAttributesResponse\022>\n\n"
          + "attributes\030\001"
          + " \003(\0132%.maps.fleetengine.v1.VehicleAttributeB\003\340A\002\"\201\010\n"
          + "\025SearchVehiclesRequest\0222\n"
          + "\006header\030\001 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\023\n"
          + "\006parent\030\003 \001(\tB\003\340A\002\022@\n"
          + "\014pickup_point\030\004"
          + " \001(\0132%.maps.fleetengine.v1.TerminalLocationB\003\340A\002\022<\n\r"
          + "dropoff_point\030\005 \001(\0132%.maps.fleetengine.v1.TerminalLocation\022!\n"
          + "\024pickup_radius_meters\030\006 \001(\005B\003\340A\002\022\022\n"
          + "\005count\030\007 \001(\005B\003\340A\002\022\035\n"
          + "\020minimum_capacity\030\010 \001(\005B\003\340A\002\0226\n"
          + "\n"
          + "trip_types\030\t \003(\0162\035.maps.fleetengine.v1.TripTypeB\003\340A\002\0224\n"
          + "\021maximum_staleness\030\n"
          + " \001(\0132\031.google.protobuf.Duration\022D\n\r"
          + "vehicle_types\030\016"
          + " \003(\0132(.maps.fleetengine.v1.Vehicle.VehicleTypeB\003\340A\002\022B\n"
          + "\023required_attributes\030\014"
          + " \003(\0132%.maps.fleetengine.v1.VehicleAttribute\022M\n"
          + "\032required_one_of_attributes\030\017"
          + " \003(\0132).maps.fleetengine.v1.VehicleAttributeList\022Q\n"
          + "\036required_one_of_attribute_sets\030\024"
          + " \003(\0132).maps.fleetengine.v1.VehicleAttributeList\022S\n"
          + "\010order_by\030\r"
          + " \001(\0162<.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrderB\003\340A\002\022\034\n"
          + "\024include_back_to_back\030\022 \001(\010\022\017\n"
          + "\007trip_id\030\023 \001(\t\"\252\001\n"
          + "\021VehicleMatchOrder\022\037\n"
          + "\033UNKNOWN_VEHICLE_MATCH_ORDER\020\000\022\024\n"
          + "\020PICKUP_POINT_ETA\020\001\022\031\n"
          + "\025PICKUP_POINT_DISTANCE\020\002\022\025\n"
          + "\021DROPOFF_POINT_ETA\020\003\022\"\n"
          + "\036PICKUP_POINT_STRAIGHT_DISTANCE\020\004\022\010\n"
          + "\004COST\020\005\"L\n"
          + "\026SearchVehiclesResponse\0222\n"
          + "\007matches\030\001 \003(\0132!.maps.fleetengine.v1.VehicleMatch\"\267\004\n"
          + "\023ListVehiclesRequest\0222\n"
          + "\006header\030\014 \001(\0132\".maps.fleetengine.v1.RequestHeader\022\023\n"
          + "\006parent\030\001 \001(\tB\003\340A\002\022\021\n"
          + "\tpage_size\030\003 \001(\005\022\022\n\n"
          + "page_token\030\004 \001(\t\0225\n"
          + "\020minimum_capacity\030\006 \001(\0132\033.google.protobuf.Int32Value\0221\n\n"
          + "trip_types\030\007 \003(\0162\035.maps.fleetengine.v1.TripType\0224\n"
          + "\021maximum_staleness\030\010 \001(\0132\031.google.protobuf.Duration\022W\n"
          + "\027vehicle_type_categories\030\t"
          + " \003(\01621.maps.fleetengine.v1.Vehicle.VehicleType.CategoryB\003\340A\002\022\033\n"
          + "\023required_attributes\030\n"
          + " \003(\t\022\"\n"
          + "\032required_one_of_attributes\030\r"
          + " \003(\t\022&\n"
          + "\036required_one_of_attribute_sets\030\017 \003(\t\0228\n\r"
          + "vehicle_state\030\013 \001(\0162!.maps.fleetengine.v1.VehicleState\022\024\n"
          + "\014on_trip_only\030\016 \001(\010\"x\n"
          + "\024ListVehiclesResponse\022.\n"
          + "\010vehicles\030\001 \003(\0132\034.maps.fleetengine.v1.Vehicle\022\027\n"
          + "\017next_page_token\030\002 \001(\t\022\027\n\n"
          + "total_size\030\003 \001(\003B\003\340A\002\"Y\n"
          + "\010Waypoint\022$\n"
          + "\007lat_lng\030\001 \001(\0132\023.google.type.LatLng\022\'\n"
          + "\003eta\030\002 \001(\0132\032.google.protobuf.Timestamp\"\376\006\n"
          + "\014VehicleMatch\0222\n"
          + "\007vehicle\030\001 \001(\0132\034.maps.fleetengine.v1.VehicleB\003\340A\002\0226\n"
          + "\022vehicle_pickup_eta\030\002 \001(\0132\032.google.protobuf.Timestamp\022C\n"
          + "\036vehicle_pickup_distance_meters\030\003"
          + " \001(\0132\033.google.protobuf.Int32Value\022V\n"
          + ",vehicle_pickup_straight_line_distance_meters\030\013"
          + " \001(\0132\033.google.protobuf.Int32ValueB\003\340A\002\0227\n"
          + "\023vehicle_dropoff_eta\030\004 \001(\0132\032.google.protobuf.Timestamp\022N\n"
          + ")vehicle_pickup_to_dropoff_distance_meters\030\005"
          + " \001(\0132\033.google.protobuf.Int32Value\0225\n"
          + "\ttrip_type\030\006 \001(\0162\035.maps.fleetengine.v1.TripTypeB\003\340A\002\022>\n"
          + "\027vehicle_trips_waypoints\030\007"
          + " \003(\0132\035.maps.fleetengine.v1.Waypoint\022N\n"
          + "\022vehicle_match_type\030\010"
          + " \001(\01622.maps.fleetengine.v1.VehicleMatch.VehicleMatchType\022Z\n"
          + "\024requested_ordered_by\030\t"
          + " \001(\0162<.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrder\022P\n\n"
          + "ordered_by\030\n"
          + " \001(\0162<.maps.fleetengine.v1.SearchVehiclesRequest.VehicleMatchOrder\"g\n"
          + "\020VehicleMatchType\022\013\n"
          + "\007UNKNOWN\020\000\022\r"
          + "\n"
          + "\tEXCLUSIVE\020\001\022\020\n"
          + "\014BACK_TO_BACK\020\002\022\013\n"
          + "\007CARPOOL\020\003\022\030\n"
          + "\024CARPOOL_BACK_TO_BACK\020\004\"Q\n"
          + "\024VehicleAttributeList\0229\n\n"
          + "attributes\030\001 \003(\0132%.maps.fleetengine.v1.VehicleAttribute2\244\n\n"
          + "\016VehicleService\022\214\001\n\r"
          + "CreateVehicle\022).maps.fleetengine.v1.CreateVehicleRequest\032\034.maps.fleetengine.v1.Vehicle\"2\202\323\344\223\002,\"!/v1/{parent=providers/*}/vehicles:\007vehicle\022}\n\n"
          + "GetVehicle\022&.maps.fleetengine.v1.GetVehicleRequest\032\034.maps.fleetengine.v1.Vehicle\")\202\323\344\223\002#\022!/v1/{name=providers/*/vehicles/*}\022\214\001\n\r"
          + "UpdateVehicle\022).maps.fleetengine.v1.UpdateVehicleRequest\032\034.maps.fleetengine.v1.Vehicle\"2\202\323\344\223\002,\032!/v1/{name=providers/*/vehicles/*}:\007vehicle\022\260\001\n"
          + "\025UpdateVehicleLocation\0221.maps.fleetengine.v1.UpdateVehicleLocationRequest\032$.maps.fleetengine.v1.VehicleLocation\">\210\002\001\202\323\344\223\0025\0320/v1/{name=providers/*/vehicles/*}:updateLocation:\001*\022\303\001\n"
          + "\027UpdateVehicleAttributes\0223.maps.fleetengine.v1.UpdateVehicleAttributesRequest\0324.maps.fleetengine.v1.UpdateVehicleAttributesResponse\"=\202\323\344\223\0027\"2/v1/{name=providers/*/vehicles/*}:updateAttributes:\001*\022\216\001\n"
          + "\014ListVehicles\022(.maps.fleetengine.v1.ListVehiclesRequest\032).maps.fleetengine.v1.ListVehiclesResponse\")\202\323\344\223\002#\022!/v1/{parent=providers/*}/vehicles\022\236\001\n"
          + "\016SearchVehicles\022*.maps.fleetengine.v1.SearchVehiclesRequest\032+.maps.fleetengine.v1.SearchVehiclesResponse\"3\202\323\344\223\002-\"(/v1/{parent=providers/*}/vehicles:search:\001*\022\252\001\n"
          + "\024SearchFuzzedVehicles\022*.maps.fleetengine.v1.SearchVehiclesRequest\032+.maps.fleetengine.v1.SearchVehiclesResponse\"9\202\323\344\223\0023\"./v1/{parent=providers/*}/vehicles:searchFuzzed:\001*\032\035\312A\032fleetengine.googleapis.comBw\n"
          + "\032google.maps.fleetengine.v1B\n"
          + "VehicleApiP\001ZEgoogle.golang.org/genproto/googleapis/maps"
          + "/fleetengine/v1;fleetengine\242\002\003CFEb\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.AnnotationsProto.getDescriptor(),
              com.google.api.FieldBehaviorProto.getDescriptor(),
              com.google.api.ResourceProto.getDescriptor(),
              google.maps.fleetengine.v1.FleetEngine.getDescriptor(),
              google.maps.fleetengine.v1.Headers.getDescriptor(),
              google.maps.fleetengine.v1.Vehicles.getDescriptor(),
              com.google.protobuf.DurationProto.getDescriptor(),
              com.google.protobuf.FieldMaskProto.getDescriptor(),
              com.google.protobuf.TimestampProto.getDescriptor(),
              com.google.protobuf.WrappersProto.getDescriptor(),
              com.google.type.LatLngProto.getDescriptor(),
              com.google.api.ClientProto.getDescriptor(),
            });
    internal_static_maps_fleetengine_v1_CreateVehicleRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_maps_fleetengine_v1_CreateVehicleRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_CreateVehicleRequest_descriptor,
            new java.lang.String[] {
              "Header", "Parent", "VehicleId", "Vehicle",
            });
    internal_static_maps_fleetengine_v1_GetVehicleRequest_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_maps_fleetengine_v1_GetVehicleRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_GetVehicleRequest_descriptor,
            new java.lang.String[] {
              "Header", "Name", "CurrentRouteSegmentVersion", "WaypointsVersion",
            });
    internal_static_maps_fleetengine_v1_UpdateVehicleRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_maps_fleetengine_v1_UpdateVehicleRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_UpdateVehicleRequest_descriptor,
            new java.lang.String[] {
              "Header", "Name", "Vehicle", "UpdateMask",
            });
    internal_static_maps_fleetengine_v1_UpdateVehicleLocationRequest_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_maps_fleetengine_v1_UpdateVehicleLocationRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_UpdateVehicleLocationRequest_descriptor,
            new java.lang.String[] {
              "Header", "Name", "CurrentLocation", "CurrentState",
            });
    internal_static_maps_fleetengine_v1_UpdateVehicleAttributesRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_maps_fleetengine_v1_UpdateVehicleAttributesRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_UpdateVehicleAttributesRequest_descriptor,
            new java.lang.String[] {
              "Header", "Name", "Attributes",
            });
    internal_static_maps_fleetengine_v1_UpdateVehicleAttributesResponse_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_maps_fleetengine_v1_UpdateVehicleAttributesResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_UpdateVehicleAttributesResponse_descriptor,
            new java.lang.String[] {
              "Attributes",
            });
    internal_static_maps_fleetengine_v1_SearchVehiclesRequest_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_maps_fleetengine_v1_SearchVehiclesRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_SearchVehiclesRequest_descriptor,
            new java.lang.String[] {
              "Header",
              "Parent",
              "PickupPoint",
              "DropoffPoint",
              "PickupRadiusMeters",
              "Count",
              "MinimumCapacity",
              "TripTypes",
              "MaximumStaleness",
              "VehicleTypes",
              "RequiredAttributes",
              "RequiredOneOfAttributes",
              "RequiredOneOfAttributeSets",
              "OrderBy",
              "IncludeBackToBack",
              "TripId",
            });
    internal_static_maps_fleetengine_v1_SearchVehiclesResponse_descriptor =
        getDescriptor().getMessageTypes().get(7);
    internal_static_maps_fleetengine_v1_SearchVehiclesResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_SearchVehiclesResponse_descriptor,
            new java.lang.String[] {
              "Matches",
            });
    internal_static_maps_fleetengine_v1_ListVehiclesRequest_descriptor =
        getDescriptor().getMessageTypes().get(8);
    internal_static_maps_fleetengine_v1_ListVehiclesRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_ListVehiclesRequest_descriptor,
            new java.lang.String[] {
              "Header",
              "Parent",
              "PageSize",
              "PageToken",
              "MinimumCapacity",
              "TripTypes",
              "MaximumStaleness",
              "VehicleTypeCategories",
              "RequiredAttributes",
              "RequiredOneOfAttributes",
              "RequiredOneOfAttributeSets",
              "VehicleState",
              "OnTripOnly",
            });
    internal_static_maps_fleetengine_v1_ListVehiclesResponse_descriptor =
        getDescriptor().getMessageTypes().get(9);
    internal_static_maps_fleetengine_v1_ListVehiclesResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_ListVehiclesResponse_descriptor,
            new java.lang.String[] {
              "Vehicles", "NextPageToken", "TotalSize",
            });
    internal_static_maps_fleetengine_v1_Waypoint_descriptor =
        getDescriptor().getMessageTypes().get(10);
    internal_static_maps_fleetengine_v1_Waypoint_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_Waypoint_descriptor,
            new java.lang.String[] {
              "LatLng", "Eta",
            });
    internal_static_maps_fleetengine_v1_VehicleMatch_descriptor =
        getDescriptor().getMessageTypes().get(11);
    internal_static_maps_fleetengine_v1_VehicleMatch_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_VehicleMatch_descriptor,
            new java.lang.String[] {
              "Vehicle",
              "VehiclePickupEta",
              "VehiclePickupDistanceMeters",
              "VehiclePickupStraightLineDistanceMeters",
              "VehicleDropoffEta",
              "VehiclePickupToDropoffDistanceMeters",
              "TripType",
              "VehicleTripsWaypoints",
              "VehicleMatchType",
              "RequestedOrderedBy",
              "OrderedBy",
            });
    internal_static_maps_fleetengine_v1_VehicleAttributeList_descriptor =
        getDescriptor().getMessageTypes().get(12);
    internal_static_maps_fleetengine_v1_VehicleAttributeList_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_maps_fleetengine_v1_VehicleAttributeList_descriptor,
            new java.lang.String[] {
              "Attributes",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.ClientProto.defaultHost);
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.AnnotationsProto.http);
    registry.add(com.google.api.ResourceProto.resourceReference);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.api.ResourceProto.getDescriptor();
    google.maps.fleetengine.v1.FleetEngine.getDescriptor();
    google.maps.fleetengine.v1.Headers.getDescriptor();
    google.maps.fleetengine.v1.Vehicles.getDescriptor();
    com.google.protobuf.DurationProto.getDescriptor();
    com.google.protobuf.FieldMaskProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
    com.google.protobuf.WrappersProto.getDescriptor();
    com.google.type.LatLngProto.getDescriptor();
    com.google.api.ClientProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}