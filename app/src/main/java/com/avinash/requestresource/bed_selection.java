package com.avinash.requestresource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


public class bed_selection extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private TextView currentAddTv;
    private Location currentLocation;
    private LocationCallback locationCallback;
    public ProgressBar spinner;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bed_selection);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        currentAddTv = findViewById(R.id.selectBedText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                String county = hereLocation(location.getLatitude(), location.getLongitude());
                currentAddTv.setText(("Reserved your bed at a nearby hospital in " + county));
                SharedPreferences pref = getApplicationContext().getSharedPreferences("8ResQ",0);
                SharedPreferences.Editor editor = pref.edit();
                String reqCounty = new StringTokenizer(county).nextToken().toLowerCase();
                editor.putString("location",reqCounty);
                editor.apply();
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
                client.setReadTimeout(30, TimeUnit.SECONDS);
                String url= "https://8resqservices.azurewebsites.net/hospital/getHospitals";
                String postBody="{" + "\"county\": " + "\"" + pref.getString("location","fulton")  + "\"}";
                RequestBody body = RequestBody.create(JSON, postBody);
                Request request = new Request.Builder().url(url).post(body).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Intent QuestionActivity = new Intent(getApplicationContext(), bed_selection.class);
                        startActivity(QuestionActivity);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            ArrayList<Requests> requests = new ArrayList<Requests>();
                            JSONArray json = new JSONArray(response.body().string());
                            RadioGroup rg = (RadioGroup) findViewById(R.id.beds);
                            rg.setOrientation(LinearLayout.VERTICAL);
                            for(int index= 0; index < json.length(); index++){
                                JSONObject jsonobj = json.getJSONObject(index);
                                RadioButton rdbtn = new RadioButton(getApplicationContext());
                                rdbtn.setId(View.generateViewId());
                                rdbtn.setText((String) jsonobj.getString("hospitalName") + " Hospital " + "Cost: " + (String) jsonobj.getString("cost"));
                                rdbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                rg.addView(rdbtn);
                            }
                            spinner.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(bed_selection.this, "Not Found!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String county = hereLocation(location.getLatitude(), location.getLongitude());
                        currentAddTv.setText(county);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(bed_selection.this, "Not Found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission not Granted!", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }


    private String hereLocation(double lat, double lng) {
        String countyName = "";
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getSubAdminArea() != null && adr.getSubAdminArea().length() > 0) {
                        countyName = adr.getSubAdminArea();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            countyName = "fulton";
        }
        return countyName;
    }




}