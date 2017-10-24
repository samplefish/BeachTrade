package com.example.tyren.beachtrade;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by William on 10/23/2017.
 */

public class FacebookCognitoSync extends AsyncTask<String, Void, String> { //takes fb token and passes it to cognito
    @Override
    protected String doInBackground(String... params)
    {
        String token = params[0];
        Log.d("Background Worker", token);
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923", // Identity pool ID
                Regions.US_EAST_1 // Region
        );


        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com", token);
        credentialsProvider.setLogins(logins);

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_1, // Region
                credentialsProvider);

        credentialsProvider.refresh();
        return "Success";
    }


}