package com.sam_chordas.android.stockhawk.rest;

import android.os.AsyncTask;
import android.util.Log;

import com.sam_chordas.android.stockhawk.AsyncResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class FetchChartData extends AsyncTask<String, Void, ChartParcelable[]> {

    private final String LOG_TAG = FetchChartData.class.getSimpleName();

    public AsyncResponse delegate = null;

    @Override
    protected ChartParcelable[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String symbol = params[0] + "";

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String startDate = format.format(date);

        String endDate = sdf.format(new Date());

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String chartJsonStr = null;

        try {

            String myUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20" +
                    "symbol%20%3D%20%22" + symbol + "%22%20and%20" +
                    "startDate%20%3D%20%22" + startDate + "%22%20and%20" +
                    "endDate%20%3D%20%22" + endDate +
                    "%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&format=json&callback=";


            URL url = new URL(myUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            chartJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        try {
            return getDataFromJson(chartJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    private ChartParcelable[] getDataFromJson(String chartDBStr)
            throws JSONException {

        final String MDB_QUERY = "query";
        final String MDB_QUOTE = "quote";
        final String MDB_CLOSE = "Close";
        final String MDB_RESULTS = "results";
        final String MDB_DATE = "Date";

        JSONObject chartJson = new JSONObject(chartDBStr);
        JSONObject queryJson = chartJson.getJSONObject(MDB_QUERY);
        JSONObject resultsJson = queryJson.getJSONObject(MDB_RESULTS);
        JSONArray arrayJson = resultsJson.getJSONArray(MDB_QUOTE);


        ChartParcelable[] result = new ChartParcelable[arrayJson.length()];
        for (int i = 0; i < arrayJson.length(); i++) {

            String closePrice;
            String date;

            JSONObject dateAndClosePrice = arrayJson.getJSONObject(i);

            closePrice = dateAndClosePrice.getString(MDB_CLOSE);
            Float closePriceFloat = Float.parseFloat(closePrice);

            date = dateAndClosePrice.getString(MDB_DATE);
            date = date.substring(5, date.length());

            result[i] = new ChartParcelable(date, closePriceFloat);

        }
        return result;
    }

    @Override
    protected void onPostExecute(ChartParcelable[] result) {
        if (result != null) {
            delegate.processFinish(result);
        }
    }
}