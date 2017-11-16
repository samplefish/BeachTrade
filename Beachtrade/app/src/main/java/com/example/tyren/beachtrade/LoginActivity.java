package com.example.tyren.beachtrade;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by William on 10/23/2017.
 */

public class LoginActivity extends AppCompatActivity {

    LoginButton login;
    CallbackManager cbManager;
    LoginButton register;
    AccessToken accessToken;
    String facebookID;
    TextView testText;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    ProfileMapperClass retrievedProfile;

    ProgressDialog progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        cbManager = CallbackManager.Factory.create();
        login = (LoginButton) findViewById(R.id.login_button);
        testText = (TextView) findViewById(R.id.textView);
        accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null){
            Log.e("Access token not null!!", "NOT!");
            Toast.makeText(LoginActivity.this, "Logged in from previous session", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, NavigationBarActivity.class));


        }


        login.registerCallback(cbManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                facebookID = loginResult.getAccessToken().getUserId();
                accessToken = loginResult.getAccessToken();
                testText.setText(facebookID);
                new FacebookCognitoSync().execute(accessToken.getToken());//Cognito integration that works as an async task in the background
                new checkUsername().execute();
                //startActivity(new Intent(LoginActivity.this, NavigationBarActivity.class));

            }

            @Override
            public void onCancel() {
            }


            @Override
            public void onError(FacebookException error) {

            }



        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbManager.onActivityResult(requestCode, resultCode, data);

    }

    private class checkUsername extends AsyncTask<Void, Integer, Integer> {


        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Logging in...");
            progress.show();
        }

        @Override
        protected Integer doInBackground(Void... params){

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            String userID = accessToken.getUserId();

            ProfileMapperClass profileMapper = new ProfileMapperClass();


            if(credentialsProvider != null && profileMapper != null){
                DynamoDBMapper dynamoDBMapper = ManagerClass.intiDynamoClient(credentialsProvider);
                retrievedProfile = dynamoDBMapper.load(ProfileMapperClass.class, userID);
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            progress.dismiss();
            if(retrievedProfile !=null){
                if(retrievedProfile.getUserName() == null) {
                    startActivity(new Intent(LoginActivity.this, FirstTimeActivity.class));
                }
                else{
                    startActivity(new Intent(LoginActivity.this, NavigationBarActivity.class));
                    finish();
                }
            }
            if(retrievedProfile == null){
                startActivity(new Intent(LoginActivity.this, FirstTimeActivity.class));
            }
            if(integer ==1)
            {
                Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("Bad stuff...","");
            }

        }


    }




}
