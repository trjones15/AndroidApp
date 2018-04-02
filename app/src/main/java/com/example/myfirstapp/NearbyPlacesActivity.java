package com.example.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyPlacesActivity extends AppCompatActivity {

    private final String API_KEY = "AIzaSyD65aKcH2xtNRnxMmwDy4knEu1_GHsaJTk";
    String lat, lng;
    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        Bundle info = getIntent().getExtras();
        lat = info.getString("lat").toString();
        lng = info.getString("lng").toString();

        names = new ArrayList<String>();
    }

    private String buildUrl() {
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurant&location=" +
                lat + "," + lng + "&radius=10000&key=" + API_KEY;
    }

    private void getRequest(StringBuilder url) {
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

                                names.add(result.getString("name"));
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
}
