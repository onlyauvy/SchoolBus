package oss.bus.school.schoolbus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final EditText u_name=(EditText)findViewById(R.id.u_name);
        final EditText u_pass=(EditText)findViewById(R.id.u_pass);
        Button log_button=(Button)findViewById(R.id.log_button);

        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store.user_name = u_name.getText()+"";
                Store.user_pass = u_pass.getText()+"";

                /*
                Intent intent=new Intent(getApplicationContext(), Bus.class);
                startActivity(intent);
                */
                Login_auth client = new Login_auth();
                client.execute(Store.login_link +"email="+Store.user_name+"&pass="+Store.user_pass);
            }
        });
    }


    public class Login_auth extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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
                    jsonObject=new JSONObject(response.getString(0));
                    Store.login_response = jsonObject.getString("Response");
                    Store.schoolName = jsonObject.getString("SchoolId");
                    Store.busNumber = jsonObject.getString("BusIds").split(";;");

                    //Store.busNumber=jsonObject.getJSONArray("");
                }else{
                    Log.v("CatalogClient", "Response code:" + responseCode);
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
            if(Store.login_response.compareTo("You are successfully logged in to your Account.")==0){
                Intent intent=new Intent(getApplicationContext(), Bus.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Please Enter correct User name and Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
