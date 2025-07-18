package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout idHome;
    private TextView idCityName,idTemp,idCondition;
    private RecyclerView idWeatherCard;
    private TextInputEditText idEditCity;
    private ImageView idSearch,idConditionIm,idBack;
    private ArrayList<WeatherModel>weatherModelArray;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String cityname;
    private RelativeLayout developerInfoLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        idHome=findViewById(R.id.idHome);
        idCityName=findViewById(R.id.idCityName);
        idTemp=findViewById(R.id.idTemp);
        idCondition=findViewById(R.id.idCondition);
        idWeatherCard=findViewById(R.id.idWeatherCard);
        idEditCity=findViewById(R.id.idEditCity);
        idSearch=findViewById(R.id.idSearch);
        idConditionIm=findViewById(R.id.idConditionIm);
        idBack = findViewById(R.id.idBack);
        weatherModelArray=new ArrayList<>();
        weatherAdapter=new WeatherAdapter(this,weatherModelArray);
        idWeatherCard.setAdapter(weatherAdapter);
        idWeatherCard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_CODE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                cityname = getcityname(location.getLongitude(), location.getLatitude());
                getweatherinfo(cityname);
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        }


        idSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city=idEditCity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();
                }else{
                    idCityName.setText(city);
                    getweatherinfo(city);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        cityname = getcityname(location.getLongitude(), location.getLatitude());
                        getweatherinfo(cityname);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private String getcityname(double longitude, double latitude) {
        String cityname = "Unknown";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityname = addresses.get(0).getLocality();
                if (cityname == null) {
                    cityname = addresses.get(0).getAdminArea();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityname;
    }
    private void getweatherinfo(String cityName) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        ((View) progressBar).setVisibility(View.VISIBLE);
        idHome.setVisibility(View.GONE);
        String url = "https://api.weatherapi.com/v1/forecast.json?key=ec19b9cc1fb740f3a35212822253005&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        idCityName.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        idHome.setVisibility(View.VISIBLE);
                        weatherModelArray.clear();

                        try {
                            String temperature = response.getJSONObject("current").getString("temp_c");
                            idTemp.setText(temperature + "°C");

                            int isDay = response.getJSONObject("current").getInt("is_day");
                            String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                            Picasso.get().load("https:" + conditionIcon).into(idConditionIm);
                            idCondition.setText(condition);

                            if (isDay == 1) {
                                // Morning background
                                Picasso.get().load("https://images.unsplash.com/photo-1469474968028-56623f02e42e").into(idBack);
                            } else {
                                // Night background
                                Picasso.get().load("https://images.unsplash.com/photo-1534796636912-3b95b3ab5986").into(idBack);
                            }

                            JSONObject forecastObj = response.getJSONObject("forecast");
                            JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray = forecastO.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String temper = hourObj.getString("temp_c");
                                String img = hourObj.getJSONObject("condition").getString("icon");
                                String wind = hourObj.getString("wind_kph");
                                weatherModelArray.add(new WeatherModel(time, temper, img, wind));
                            }

                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
