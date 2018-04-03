package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private final String API_KEY = "AIzaSyD65aKcH2xtNRnxMmwDy4knEu1_GHsaJTk";
    private String address;
    private float lng, lat;
    private String placeId;
    private ArrayList<PointOfInterest> nearbyRestaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        address = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);

        setContentView(R.layout.activity_display_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        StringBuilder url = parseStringForUrl(address);
        getRequest(url);
    }

    private void updateMapLocation(float lng, float lat) {
        LatLng coord = new LatLng(lat,lng);
        CameraUpdate newCamera = CameraUpdateFactory.newLatLng(coord);
        CameraUpdate animate = CameraUpdateFactory.zoomTo(15);
        MarkerOptions marker = new MarkerOptions();
        marker.position(coord);
        map.moveCamera(newCamera);
        map.addMarker(marker);
        map.animateCamera(animate);
    }

    private StringBuilder parseStringForUrl(String message) {
        String[] parsedMessage = message.split(" ");
        //this should handle the fencepost error
        StringBuilder request = new StringBuilder();
        //String request = parsedMessage[0];
        if(!parsedMessage[0].equals("")) {
            request.append(parsedMessage[0]);
            for (int i = 1; i < parsedMessage.length; i++) {
                request.append("+");
                request.append(parsedMessage[i]);
            }
        } else {
            request.append("ERROR");
        }

        StringBuilder beginningOfUrl = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");
        beginningOfUrl.append(request);
        beginningOfUrl.append("&key=");
        beginningOfUrl.append(API_KEY);

        return beginningOfUrl;
    }

    private void getRequest(StringBuilder url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url.toString(),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("results");
                            JSONObject results = array.getJSONObject(0);
                            JSONObject geometry = results.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");

                            lat = (float) location.getDouble("lat");
                            lng = (float) location.getDouble("lng");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateMapLocation(lng,lat);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error handling happens here
            }
        }
        );
        queue.add(jsonRequest);
    }

    public void onClick() {
    }

    private void showNearbyRestaurants() {
        getNearbyRestaurantRequest();

        //place Points of interest on the map

    }

    private void getNearbyRestaurantRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, buildUrl(),
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("results");

                            for(int i = 0; i < array.length() && i < 5; i++) {
                                JSONObject result = array.getJSONObject(i);
                                JSONObject geometry = result.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");

                                PointOfInterest p = new PointOfInterest();
                                p.name = result.getString("name");
                                p.lat = (float) location.getDouble("lat");
                                p.lng = (float) location.getDouble("lng");

                                nearbyRestaurants.add(p);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //updateMapLocation(lng,lat);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error handling happens here
            }
        }
        );
        queue.add(jsonRequest);
    }

    private String buildUrl() {
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=" +
                lat + "," + lng + "&radius=10000&key=" + API_KEY;
    }

    //inner class
    private class PointOfInterest {
        public float lat;
        public float lng;
        public String name;
    }
}
