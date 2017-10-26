package com.example.tyren.beachtrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        cbManager = CallbackManager.Factory.create();
        login = (LoginButton) findViewById(R.id.login_button);
        testText = (TextView) findViewById(R.id.textView);
        if( accessToken != null){
            startActivity(new Intent(LoginActivity.this, NavigationBarActivity.class));
        }


        login.registerCallback(cbManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                facebookID = loginResult.getAccessToken().getUserId();
                accessToken = loginResult.getAccessToken();
                testText.setText(facebookID);
                new FacebookCognitoSync().execute(accessToken.getToken());//Cognito integration that works as an async task in the background

                startActivity(new Intent(LoginActivity.this, NavigationBarActivity.class));

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




}
