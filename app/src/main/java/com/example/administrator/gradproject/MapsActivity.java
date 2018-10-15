package com.example.administrator.gradproject;
//package okhttp3.guide;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;

    //Widgets
    private EditText mSearchText;
    private ImageView mGps;
    private Button btnSendHttpRequest;
    private EditText etJsonResponse;

    public double current_latitude;
    public double current_longitude;
    public double search_latitude;
    public double search_longitude;
    public List<Address> current_address = new ArrayList<>();
    public List<Address> search_address = new ArrayList<>();

    private OkHttpClient okHttpClient;
    private Request request;
    public String url = "https://www.instagram.com/explore/tags/vancouver/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        //Widgets
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

    //send request http
        btnSendHttpRequest = (Button)  findViewById(R.id.btnSendRequest);
        //etJsonResponse= (EditText) findViewById(R.id.etJson);
        btnSendHttpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okHttpClient = new OkHttpClient();
                request = new Request.Builder().url(url).build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("hohoho", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            Log.i("ohhhh", response.body().string());
                        }
                    }
                });
            }
        });


        LocationListener location_listner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // to do only for the first time
                if(current_latitude == 0 && current_longitude == 0 ) {
                    current_latitude = location.getLatitude();
                    current_longitude = location.getLongitude();
                    LatLng latLng = new LatLng(current_latitude, current_longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        current_address = geocoder.getFromLocation(current_latitude, current_longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(current_address.size() != 0){
                        camera_city(current_latitude, current_longitude);
                        //mark_city(current_address);
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("current location"));
                        nearby_city(10, current_address);
                    }
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10000, location_listner);
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10000, location_listner);
        }
        //Toast.makeText(getApplicationContext(), "lat " + current_latitude +", long " + current_longitude, Toast.LENGTH_LONG).show();

        //current_latitude = (double) new Location().getLatitude();
        //current_longitude= (double) new Location().getLongitude();
        /*LatLng latLng = new LatLng(current_latitude, current_longitude);
        List<Address> address = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            address = geocoder.getFromLocation(current_latitude, current_longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "lat " + current_latitude +", long " + current_longitude, Toast.LENGTH_LONG).show();
        mMap.addMarker(new MarkerOptions().position(latLng).title(address.get(0).getLocality()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.5f));*/

        //Toast.makeText(getApplicationContext(), "lat " + current_latitude +", long " + current_longitude, Toast.LENGTH_LONG).show();
        //LatLng latLng = new LatLng(current_latitude, current_longitude);
        //mMap.addMarker(new MarkerOptions().position(latLng).title("Here"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.5f));

        //init();
}


    private void nearby_city(int kilo, List<Address> address ){
        double radius = ((double) kilo / 100);
        double latitude = address.get(0).getLatitude();
        double longitude = address.get(0).getLongitude();
        double find_latitude,find_longitude;
        List<Address> relocate_address = new ArrayList<>();
        List<Address> cityname_result = new ArrayList<>();
        List<Address> nearby_city_list = new ArrayList<>();
        //nearby_city_list.add(address.get(0));

        for(find_latitude = latitude -  radius; find_latitude <= latitude + radius; find_latitude+=0.1 ){
            if(find_latitude < - 90 || find_latitude > 90) continue;
            for( find_longitude = longitude - radius; find_longitude <= longitude + radius; find_longitude+=0.1){
                if(find_longitude < - 180 ) {
                    cityname_result = search_by_LatLng(find_latitude, find_longitude + 180);
                    if(cityname_result.size() == 0) continue;
                    else if(nearby_city_list.contains(cityname_result.get(0))) continue;
                    else {
                        nearby_city_list.add(cityname_result.get(0));
                        relocate_address = search_by_name(cityname_result.get(0).getLocality() + cityname_result.get(0).getAdminArea() + cityname_result.get(0).getCountryName());
                        if(relocate_address.size() == 0) continue;
                        mark_city(relocate_address);
                    }
                }
                else if(find_longitude > 180){
                    cityname_result = search_by_LatLng(find_latitude, find_longitude - 180);
                    if(cityname_result.size() == 0) continue;
                    else if(nearby_city_list.contains(cityname_result.get(0))) continue;
                    else {
                        nearby_city_list.add(cityname_result.get(0));
                        relocate_address = search_by_name(cityname_result.get(0).getLocality() + cityname_result.get(0).getAdminArea() + cityname_result.get(0).getCountryName());
                        if(relocate_address.size() == 0) continue;
                        mark_city(relocate_address);
                    }

                }
                else {
                    cityname_result = search_by_LatLng(find_latitude, find_longitude);
                    if(cityname_result.size() == 0) continue;
                    else if(nearby_city_list.contains(cityname_result.get(0))) continue;
                    else{
                        nearby_city_list.add(cityname_result.get(0));
                        relocate_address = search_by_name(cityname_result.get(0).getLocality() + cityname_result.get(0).getAdminArea() + cityname_result.get(0).getCountryName());
                        if(relocate_address.size() == 0) continue;
                        mark_city(relocate_address);
                    }
                }
            }
        }
    }

    // make marker and move camera
    private void mark_city(List<Address> address){
        double latitude = address.get(0).getLatitude();
        double longitude = address.get(0).getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        String mark_title = address.get(0).getLocality();
        if(address.get(0).getLocality() == null) mark_title = address.get(0).getAdminArea();
        //Toast.makeText(getApplicationContext(), mark_title+ "," + address.get(0).getCountryName(), Toast.LENGTH_LONG).show();
        mMap.addMarker(new MarkerOptions().position(latLng).title(mark_title));

    }
    private void camera_city(double latitude, double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.5f));
    }

    // get location from latitude,longitutde
    private List<Address> search_by_LatLng(double latitude, double longitude){
        LatLng latLng = new LatLng(latitude, longitude);
        List <Address> address = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            address = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    // get location name from text
    private List<Address> search_by_name(String searchString){
        List<Address> address = new ArrayList<>();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
           address = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    //get text from input and call geoLocate method
    private void search(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER){
                    String searchString = mSearchText.getText().toString();
                    search_address = search_by_name(searchString);
                    if(search_address.size() == 0) Toast.makeText(getApplicationContext(), "Invalid city name", Toast.LENGTH_LONG).show();
                    else{
                        search_latitude = search_address.get(0).getLatitude();
                        search_longitude = search_address.get(0).getLongitude();
                        //mark_city(search_address);
                        camera_city(search_latitude, search_longitude);
                        nearby_city(10, search_address);
                    }
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera_city(current_latitude, current_longitude);
            }
        });
    }

    //Query
//    private  void query_work() {
//        AQuery aq = new AQuery(getApplicationContext());
//        aq.ajax("https://www.instagram.com/explore/tags/bangkok/", String.class, new AjaxCallback<String>() {
//            @Override
////            public void callback(String url, String response, AjaxStatus status){
////                    List<Cookie> cookies = status.getCookies();
////                    for(Cookie cookie: cookies){
////                            Log.d(cookie.getName(), cookie.getValue());
////                    }
////            }
//            public void callback(String url, JsonObject object, AjaxStatus status) {
//
//                object.get("userName");
//            }
//        });
//        Toast.makeText(getApplicationContext(), "query", Toast.LENGTH_LONG).show();
//    }

//    public class GetJason{
//        private OkHttpClient client;
//        private static GetJason instance = new GetJason();
//        public static GetJsaon getInstance(){
//            return instance;
//        }
//        private GetJason(){this.clinet = new OkHttpClient();}
//
//        public void requestWebserver(String parameter, Callback callback){
//            RequestBody body = new FormBody.Builder()
//                    .build();
//            Request request = new Request.Builder()
//                    .url("http://www.naver.com")
//                    .post(body)
//                    .build();
//            client.newCall(request).enqueue(callback);
//        }
//    }
//    public class GetExample {
//        OkHttpClient client = new OkHttpClient();
//
//        String run(String url) throws IOException {
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                return response.body().string();
//            }
//        }
//
//        public static void main(String[] args) throws IOException {
//            GetExample example = new GetExample();
//            String response = example.run("https://raw.github.com/square/okhttp/master/README.md");
//            System.out.println(response);
//        }
//    }

//    private void gethtmlstring(){
//        HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
//        HttpGet httpget = new HttpGet("http://www.naver.com"); // Set the action you want to do
//        HttpResponse response = httpclient.execute(httpget); // Executeit
//        HttpEntity entity = response.getEntity();
//        InputStream is = entity.getContent(); // Create an InputStream with the response
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        while ((line = reader.readLine()) != null) // Read line by line
//            sb.append(line + "\n");
//
//        String resString = sb.toString(); // Result is here
//
//        is.close(); // Close the stream
//    }
//private class ConnectServer{
//    //Client 생성
//    OkHttpClient client = new OkHttpClient();
//
//    public void requestGet(String url, String searchKey){
//
//        //URL에 포함할 Query문 작성 Name&Value
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);
//        String requestUrl = urlBuilder.build().toString();
//
//        //Query문이 들어간 URL을 토대로 Request 생성
//        Request request = new Request.Builder().url(requestUrl).build();
//
//        //만들어진 Request를 서버로 요청할 Client 생성
//        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("error", "Connect Server Error is " + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("response", "Response Body is " + response.body().string());
//                Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}
//    private void okhttp3() {
//        OkHttpClient client = new OkHttpClient();
//
//        // OkHttp3를 활용하여 구현함
//        public void httpRun(String url) throws IOException {
//            Request request = new Request.Builder().url(url).build();
//
//            try (Response response = client.newCall(request).execute()) {
//                response_string = response.body().string();
//                Log.d("response", "Response Body is " + response_string);
//                return response.body().string();
//            }
//        }
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        search();
        //ConnectServer connectServer = new ConnectServer();
        //connectServer.requestGet("https://whereisusb.tistory.com/55", "searchKey");

    }
}