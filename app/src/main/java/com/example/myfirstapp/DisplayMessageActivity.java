package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DisplayMessageActivity extends AppCompatActivity {
    final String API_KEY = "AIzaSyDhXPfpXFPsQANFW4G1bwIni7zqZ8KEHPg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        StringBuilder request = parseStringForUrl(message);

        // Set the text view as the activity layout
        //textView.setText(request);
        //setContentView(textView);


        setContentView(R.layout.activity_display_message);
        //setTextView("Hello World");

        getRequest(request);

    }

    private void setTextView (String T) {
        TextView textView = (TextView) findViewById(R.id.mapText);
        textView.setText(T);
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
                    JSONObject result = array.getJSONObject(0);
                    String location = result.getJSONObject("geometry").get("location").toString();
                    setTextView(location);

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
