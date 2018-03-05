package androids.newapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;
    private Marker marker;
    DBHelper DbHelper;
    ArrayList<User> Locations = new ArrayList<User>();
    LocationRequest mLocationRequest;
    EditText editText;
    String Field;
    ImageButton btn;
    int count = 0;
    Button hire;
    String location;
    SessionManager sessionManager;
    String userId;
    Connection connect;
    ArrayList<String> phoneNumbers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Field = IntentData.skillIntent;
        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(getApplicationContext());
        userId = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        editText = (EditText) findViewById(R.id.text);
        btn = (ImageButton) findViewById(R.id.search);
        hire = (Button) findViewById(R.id.hire);
        hire.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = editText.getText().toString();
                if (location == null || location.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please search for Location", Toast.LENGTH_SHORT).show();
                    hire.setEnabled(false);
                } else {
                    if (isNetworkAvailable() == false) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        mBuilder.setTitle("Alert");
                        mBuilder.setMessage("You have to on your Mobile data/wifi");
                        mBuilder.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openwifi();
                            }
                        });
                        mBuilder.setNeutralButton("DENY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = mBuilder.create();
                        alertDialog.show();
                    } else {
                        //Toast.makeText(getApplicationContext(), location, Toast.LENGTH_SHORT).show();
                        onMapSearch(view);
                    }
                }
            }
        });


        hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.isLoggedIn()==false){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    mBuilder.setTitle("Alert");

                    mBuilder.setMessage("To hire please login");
                    mBuilder.setPositiveButton("login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntentData.intentClass = 2;
                            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = mBuilder.create();
                    alertDialog.show();

                }
                else {
                    Intent intent = new Intent(MapsActivity.this, Description.class);
                    IntentData.phoneNos = phoneNumbers;
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this,Home.class);
        startActivity(intent);
    }

    public void openwifi() {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void onMapSearch(View view) {
        if (marker != null) {
            marker.remove();
            mMap.clear();
        }
        editText = (EditText) findViewById(R.id.text);
        String loc = editText.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);
                if (addressList == null || addressList.isEmpty()) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    mBuilder.setTitle("Alert");
                    mBuilder.setMessage("Sorry, Place not found or Enter a valid location");
                    mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = mBuilder.create();
                    alertDialog.show();
                    hire.setEnabled(false);
                } else {
                    for (Address a : addressList) {
                        if (a.hasLatitude() && a.hasLongitude()) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Searched Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            DbHelper = new DBHelper(MapsActivity.this);
                            DbHelper.open();


                            ConnectionHelper conStr = new ConnectionHelper();
                            connect = conStr.connectionclasss();
                            if (connect == null) {
                                Toast.makeText(getApplicationContext(),"Check Your Internet Access!",Toast.LENGTH_SHORT).show();
                            } else {

                                if (sessionManager.isLoggedIn() == false) {
                                    Locations = DbHelper.retrieveLocationsForMap(Field,connect);
                                } else {
                                    Locations = DbHelper.retrieveLocationsForMapLogin(Field, userId,connect);
                                }
                                DbHelper.close();
                                if (Locations.size() == 0 || Locations == null) {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                    mBuilder.setTitle("Alert");
                                    mBuilder.setMessage("No workers found in this place");
                                    mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog alertDialog = mBuilder.create();
                                    alertDialog.show();
                                    hire.setEnabled(false);
                                } else {
                                    count = 0;
                                    hire.setEnabled(true);
                                    for (User s : Locations) {
                                        addToMap(s, address.getLatitude(), address.getLongitude());
                                    }
                                    if (count == 0) {
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                        mBuilder.setTitle("Alert");
                                        mBuilder.setMessage("No workers found in this place");
                                        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog alertDialog = mBuilder.create();
                                        alertDialog.show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "There are " + count + " worker(s) nearby your searched Location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                //Toast.makeText(getApplicationContext(), "Null Pointer", Toast.LENGTH_SHORT).show();
                n.printStackTrace();
            }
        }
    }


    public void addToMap(User location, double latitude1, double longitude1) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder((this));
        String location1 = location.getCity();
        if (location1 != null || !location1.equals("")) {
            try {
                addressList = geocoder.getFromLocationName(location1, 1);
                for (Address a : addressList) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        float results[] = new float[10];
                        Location.distanceBetween(latitude1, longitude1, address.getLatitude(), address.getLongitude(), results);
                        if (results[0] / 1000 <= 50) {
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Near By"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            phoneNumbers.add(location.getPhoneNo());
                            count++;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(true);
            }
            LatLng sydney = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(sydney).title("from here "));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10.0f));

        }


    }
}