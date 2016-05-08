package oss.bus.school.schoolbus;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BusMapsActivity extends FragmentActivity {

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ProgressDialog pDialog;
    ArrayList<String> listLat = new ArrayList<String>();
    ArrayList<String> listlon = new ArrayList<String>();
    ArrayList<String> listDate = new ArrayList<String>();
    String TAG = "mamun";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        MapViewData mapClient = new MapViewData();
        mapClient.execute(Store.map_view_link+"vid="+Store.mapBus+"&dt="+Store.mapDate);
    }

    public class MapViewData extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
       /*     pDialog = new ProgressDialog(getApplicationContext());
            pDialog.setMessage("Loading...");
            pDialog.show();*/
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            JSONArray response = new JSONArray();
            JSONObject jsonObject=new JSONObject();

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpStatus.SC_OK){
                    String responseString = readStream(urlConnection.getInputStream());
                    response = new JSONArray(responseString);

                    for (int i=0;i<response.length();i++){
                        jsonObject=new JSONObject(response.getString(i));
                        listLat.add(jsonObject.getString("Latitude"));
                        listlon.add(jsonObject.getString("Longitude"));
                        listDate.add(jsonObject.getString("PositionTimeString"));
                    }

                    for (int i=0;i<response.length();i++){
                        Log.e("mamun", listLat.get(i) + "-" + listlon.get(i) + "-" + listDate.get(i));
                    }
                    //jsonObject=new JSONObject(response.getString(0));
                    //Store.login_response=jsonObject.getString("Response");

                }else{
                    Log.v("CatalogClient", "Response code:"+ responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }

            return response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }


        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        /*    if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
*/
            PolylineOptions pLine=new PolylineOptions();
            pLine.width(3);
            pLine.color(Color.BLUE);
            for (int i=0;i<listLat.size();i++){
                Log.e("mamun",listLat.get(i)+"-"+listlon.get(i)+"-"+listDate.get(i));
                pLine.add(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i))));
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i)))).title(listDate.get(i)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i))), 15));
            }

            /*
            for(int i=0; i<10; i++){
                pLine.add(new LatLng(i,i));
                map.addMarker(new MarkerOptions().position(new LatLng(i, i)).title("Day Time"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(i, i), 15));
            }*/
            mMap.addPolyline(pLine);

        }
    }
}
