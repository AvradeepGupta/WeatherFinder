package com.example.avrad.weatherfinder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView result;
    String mycity;

    //https://api.openweathermap.org/data/2.5/weather?q=London&APPID=f9138e3b25d8ef994347fd58ccf1eab2
    //APIKEY = f9138e3b25d8ef994347fd58ccf1eab2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        city = (EditText)findViewById(R.id.city);
        result = (TextView)findViewById(R.id.weather);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading Weather", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                FindWeather(view);
            }
        });
    }

    //This method will help to find the weather by executing the link

    public void FindWeather(View v)
    {
        mycity = city.getText().toString();
        try
        {
            ExecuteTask tasky = new ExecuteTask();
            tasky.execute("https://api.openweathermap.org/data/2.5/weather?q="+mycity+"&APPID=f9138e3b25d8ef994347fd58ccf1eab2");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public class ExecuteTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(is);

                int data = reader.read();

                while(data != -1){

                    char current = (char)data;
                    result += current;

                    data = reader.read();
                }

                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            try{
                String message = "";
                JSONObject jsonObject = new JSONObject(s);

                String infoWeatherToday = jsonObject.getString("weather");
                JSONArray array = new JSONArray(infoWeatherToday);
                for(int i=0 ; i<array.length() ; i++)
                {
                    JSONObject jsonSecondary = array.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonSecondary.getString("main");
                    description = jsonSecondary.getString("description");

                    if(main != "" && description != "")
                    {
                        message += main + " : " + description + "\n";
                    }
                }
                String infoWeatherToday2 = jsonObject.getString("main");


                int i = infoWeatherToday2.indexOf(':');
                int j = infoWeatherToday2.indexOf(',');

                String temp = infoWeatherToday2.substring(i+1,j);
                float f = Float.parseFloat(temp);
                f-=273.15;
                infoWeatherToday2 = infoWeatherToday2.substring(j+1);

                i = infoWeatherToday2.indexOf(':');
                j = infoWeatherToday2.indexOf(',');
                String pressure = infoWeatherToday2.substring(i+1,j);
                infoWeatherToday2 = infoWeatherToday2.substring(j+1);

                i = infoWeatherToday2.indexOf(':');
                j = infoWeatherToday2.indexOf(',');
                String humid = infoWeatherToday2.substring(i+1,j);

                message += "Temperature : " + Math.round(f) + "\nPressure : "+ pressure+"\nHumidity : "+humid;

                if(message != "")
                {
                    result.setText(message);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Check Again",Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
