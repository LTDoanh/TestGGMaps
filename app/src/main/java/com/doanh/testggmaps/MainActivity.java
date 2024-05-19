package com.doanh.testggmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.PolyUtil;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12;
    private static final LatLng[] FIXED_LOCATIONS = {
            new LatLng(21.0376713, 105.7816301),
            new LatLng(10.771065, 106.692282),
    };

    private Polyline currentPolyline;

    private SupportMapFragment mapFragment;
    private ListView cinemaListView;
    private List<Cinema> cinemaList;
    private CinemaAdapter adapter;
    private TextView showAllCinemaBtn;
    private TextView cinemaName;
    private TextView cinemaDistance;

    public static LatLng currentLatLng;

//    private GetCinemaController getCinemaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        cinemaListView = findViewById(R.id.cinemaListView);
        showAllCinemaBtn = findViewById(R.id.showAllCinemaBtn);
        cinemaName = findViewById(R.id.cinemaName1);
        cinemaDistance = findViewById(R.id.distance1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Zoom level 15

                            initializeCinemaList();
                            updateCinemaDistances();

                            for (Cinema cinema : cinemaList) {
                                LatLng latLng = new LatLng(cinema.getLatitude(), cinema.getLongitude());
                                this.googleMap.addMarker(new MarkerOptions().position(latLng).title(cinema.getName()));
                            }

                            this.googleMap.setOnMarkerClickListener(marker -> {
                                LatLng destinationLatLng = marker.getPosition();
                                String distance = String.format("%.2fKm", calculateDistance(destinationLatLng));
                                marker.setSnippet(distance);
                                marker.showInfoWindow();
                                showRoute(currentLatLng, destinationLatLng);
                                cinemaName.setText(marker.getTitle());
                                cinemaDistance.setText(distance);
                                return false;
                            });
                        } else {
                            Log.e("Location is null", "Location is null");
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showRoute(LatLng currentLatLng, LatLng destinationLatLng) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                currentLatLng.latitude + "," + currentLatLng.longitude +
                "&destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude +
                "&key=" + getString(R.string.google_maps_key);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject polyline = route.getJSONObject("overview_polyline");
                            String polylinePoints = polyline.getString("points");

                            List<LatLng> decodedPolyline = PolyUtil.decode(polylinePoints);

                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(decodedPolyline)
                                    .color(Color.BLUE)
                                    .width(5);
                            currentPolyline = googleMap.addPolyline(polylineOptions);
                        } else {
                            Log.e("No routes found", "No routes found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                , error -> {
            Log.e("Error fetching route", "Error fetching route");
        });
        queue.add(jsonObjectRequest);
    }

    public static float calculateDistance(LatLng cinemaLatLng) {
        float[] results = new float[1];
        if (currentLatLng == null) {
            Log.e("e", "e");
        } else {
            Log.e("a", "a");
        }
        android.location.Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude, cinemaLatLng.latitude, cinemaLatLng.longitude, results);
        float distanceInMeters = results[0];
        float distanceInKm = distanceInMeters / 1000;
        return distanceInKm;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(googleMap);
            } else {
                Log.e("Location permission denied", "Location permission denied");
            }
        }
    }

    private void initializeCinemaList() {
        cinemaList = new ArrayList<>();
        adapter = new CinemaAdapter(this, cinemaList);
        cinemaListView.setAdapter(adapter);

        Cinema cinema = new Cinema("CGV Indochina Plaza Hà Nội", 21.036045350169463, 105.78227570049765,
                "Indochina Plaza HaNoi, 241 Xuân Thủy, Dịch Vọng Hậu, Cầu Giấy, Hà Nội, Việt Nam",
                "CGV");
        cinemaList.add(cinema);
        cinema = new Cinema("BHD Star Discovery Cầu Giấy", 21.035272446512277, 105.7947252658769,
                "Trung Tâm Thương mại Discovery, 302 Đ. Cầu Giấy, Dịch Vọng, Cầu Giấy, Hà Nội, Việt Nam",
                "BHD");
        cinemaList.add(cinema);
        cinema = new Cinema("Trung tâm Chiếu phim Quốc gia", 21.01684967388941, 105.81561734530099,
                "87 P. Láng Hạ, Chợ Dừa, Ba Đình, Hà Nội 10000, Việt Nam",
                "NCC");
        cinemaList.add(cinema);
    }

    private void updateCinemaDistances() {
        for (Cinema cinema : cinemaList) {
            // Update distance for each cinema
            cinema.setDistance(calculateDistance(new LatLng(cinema.getLatitude(), cinema.getLongitude())));
        }
        adapter.notifyDataSetChanged();
    }
}