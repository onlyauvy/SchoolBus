package oss.bus.school.schoolbus;

//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.ProgressDialog;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by Abdullah on 10/11/2015.
 */
public class BusMap extends Fragment {
    public GoogleMap map;
    ProgressDialog pDialog;
    ArrayList<String> listLat = new ArrayList<String>();
    ArrayList<String> listlon = new ArrayList<String>();
    ArrayList<String> listDate = new ArrayList<String>();
    String TAG = "mamun";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.map,container,false);
        //((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapToday)).getMap();
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        //map = getMapFragment().getMap();
        //map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapViewData mapClient = new MapViewData();
        //Log.e("mamun link",Store.map_view_link+"vid="+Store.mapBus+"&dt="+Store.mapDate);

        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = getMapFragment().getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                mapClient.execute(Store.map_view_link+"vid="+Store.mapBus+"&dt="+Store.mapDate);
            }
        }
        //mapClient.execute(Store.map_view_link+"vid="+Store.mapBus+"&dt="+Store.mapDate);
    }

    private SupportMapFragment getMapFragment() {
        FragmentManager fm = null;

        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "release: " + Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
            fm = getChildFragmentManager();
        }

        return (SupportMapFragment) fm.findFragmentById(R.id.map);
    }

    public class MapViewData extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.show();
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
                        Log.e("mamun",listLat.get(i)+"-"+listlon.get(i)+"-"+listDate.get(i));
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
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            PolylineOptions pLine=new PolylineOptions();
            pLine.width(3);
            pLine.color(Color.BLUE);
            for (int i=0;i<listLat.size();i++){
                Log.e("mamun",listLat.get(i)+"-"+listlon.get(i)+"-"+listDate.get(i));
                pLine.add(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i))));
                map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i)))).title(listDate.get(i)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(listLat.get(i)), Double.parseDouble(listlon.get(i))), 15));
            }

            /*
            for(int i=0; i<10; i++){
                pLine.add(new LatLng(i,i));
                map.addMarker(new MarkerOptions().position(new LatLng(i, i)).title("Day Time"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(i, i), 15));
            }*/
            map.addPolyline(pLine);

        }
    }
}
