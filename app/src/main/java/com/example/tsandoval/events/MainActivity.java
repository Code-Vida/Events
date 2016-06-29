package com.example.tsandoval.events;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;

public class MainActivity extends FragmentActivity {

    // Google Map
    private GoogleMap googleMap;

    // Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //*** Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ArrayList<HashMap<String, String>> location = null;
        String url = "http://beijoseabracos.com.br/get_all_events.php";
        try {

            JSONArray data = new JSONArray(getHttpGet(url));

            location = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);

                map = new HashMap<String, String>();
                //map.put("LocationID", c.getString("LocationID"));
                map.put("pid", c.getString("pid"));
                map.put("nome", c.getString("nome"));
                map.put("descricao", c.getString("descricao"));
                map.put("local", c.getString("local"));
                map.put("latitude", c.getString("latitude"));
                map.put("longitude", c.getString("longitude"));
                //map.put("LocationName", c.getString("LocationName"));
                location.add(map);

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // *** Display Google Map
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();

        // *** Focus & Zoom
        Latitude = Double.parseDouble(location.get(0).get("latitude").toString());
        Longitude = Double.parseDouble(location.get(0).get("longitude").toString());
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings loc = googleMap.getUiSettings();
        loc.setZoomControlsEnabled(true);
        loc.setMyLocationButtonEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location localizacao = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        if (localizacao != null) {
            LatLng coordinate = new LatLng(localizacao.getLatitude(), localizacao.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(localizacao.getLatitude(), localizacao.getLongitude()))      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
        }
        // *** Marker (Loop)
        for (int i = 0; i < location.size(); i++) {
            Latitude = Double.parseDouble(location.get(i).get("latitude").toString());
            Longitude = Double.parseDouble(location.get(i).get("longitude").toString());
            String name = location.get(i).get("nome").toString();
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(Latitude, Longitude))
                    .title(name);
            googleMap.addMarker(marker);
        }


        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Toast.makeText(getBaseContext(), "Posição: " + latLng, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, novoActivity.class);
                double lat = latLng.latitude;
                double lon = latLng.longitude;

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                startActivity(intent);
                Log.i("Script", "Latitude: " + lat + " Longitude: " + lon);
            }
        });

    }

    public static String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
