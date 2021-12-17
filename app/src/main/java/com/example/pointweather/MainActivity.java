package com.example.pointweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText textCity;
    private Button buttonSearch;
    private TextView result;
    private TextView nameCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        result = findViewById(R.id.result);
        nameCity = findViewById(R.id.textCity);


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textCity.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.toast, Toast.LENGTH_LONG).show();
                } else {
                    String city = textCity.getText().toString();
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=8ad47d6c666666337b0534d23f4d4dcc&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result.setText("Ожидайте..");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String url = "https://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=8ad47d6c666666337b0534d23f4d4dcc&units=metric&lang=ru";
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result1) {
            super.onPostExecute(result1);
            String description = "";

            try {
                JSONObject json = new JSONObject(result1);
                JSONArray jsonArray = json.getJSONArray("weather");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject temp = jsonArray.getJSONObject(i);

                    description = temp.getString("description");
                }

                nameCity.setText("" + json.getString("name"));
                result.setText("Температура: " + json.getJSONObject("main").getDouble("temp") + "\n" + "\n" +
                        "Ощущается как: " + json.getJSONObject("main").getDouble("feels_like") + "\n" + "\n" +
                        "Скорость ветра: " + json.getJSONObject("wind").getDouble("speed") + "\n" + "\n" +
                        "Облачность: " + json.getJSONObject("clouds").getDouble("all") + "\n" + "\n" +
                        "Влажность: " + json.getJSONObject("main").getDouble("humidity") + "\n" + "\n" +
                        description + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}