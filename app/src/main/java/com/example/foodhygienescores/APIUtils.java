package com.example.foodhygienescores;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class APIUtils {
    // Base URL for API.
    private static final String API_URL = "https://api.ratings.food.gov.uk/Establishments?";
    // Headers for API Version
    public static final String API_VERSION = "2";
    // Query parameters for the search string.
    private static final String QUERY_PARAM_ADDRESS = "address";
    private static final String QUERY_PARAM_DISTANCE = "maxDistanceLimit";
    // Parameter that limits search results based on address.
    private static final String MAX_DISTANCE_MILES = "0.5";

    static ArrayList<APIResultsModel> getFoodHygieneData(String queryString) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        ArrayList<APIResultsModel> resultsList = new ArrayList<>();

        try {

            Uri builtURI = Uri.parse(API_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM_ADDRESS, queryString)
                    .appendQueryParameter(QUERY_PARAM_DISTANCE, MAX_DISTANCE_MILES)
                    .build();
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("x-api-version", API_VERSION);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject resultObject = new JSONObject(line);
                JSONArray jsonArray = resultObject.getJSONArray("establishments");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    // Skip empty objects if present
                    if (jsonObject != null) {
                        APIResultsModel resultsModel = new APIResultsModel();
                        resultsModel.setFHRSID(jsonObject.getString("FHRSID"));
                        resultsModel.setBusinessName(jsonObject.getString("BusinessName"));
                        resultsModel.setAddressLine1(jsonObject.getString("AddressLine1"));
                        resultsModel.setAddressLine2(jsonObject.getString("AddressLine2"));
                        resultsModel.setAddressLine3(jsonObject.getString("AddressLine3"));
                        resultsModel.setAddressLine4(jsonObject.getString("AddressLine4"));
                        resultsModel.setPostCode(jsonObject.getString("PostCode"));
                        resultsModel.setRatingValue(jsonObject.getString("RatingValue"));
                        resultsModel.setAuthorityName(jsonObject.getString("LocalAuthorityName"));
                        resultsModel.setAuthorityWebsite(jsonObject.getString("LocalAuthorityWebSite"));
                        resultsModel.setAuthorityEmail(jsonObject.getString("LocalAuthorityEmailAddress"));
                        resultsModel.setScoreHygiene(jsonObject.getJSONObject("scores").optInt("Hygiene", -1));
                        resultsModel.setScoreStructural(jsonObject.getJSONObject("scores").optInt("Structural", -1));
                        resultsModel.setScoreConInMan(jsonObject.getJSONObject("scores").optInt("ConfidenceInManagement", -1));
                        resultsModel.setLongitude(jsonObject.getJSONObject("geocode").getString("longitude"));
                        resultsModel.setLatitude(jsonObject.getJSONObject("geocode").getString("latitude"));
                        resultsModel.setDistance(jsonObject.optDouble("Distance", Double.NaN));
                        resultsList.add(resultsModel);
                    }
                }
            }

        } catch (IOException | JSONException e) {

            e.printStackTrace();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultsList;

    }
}
