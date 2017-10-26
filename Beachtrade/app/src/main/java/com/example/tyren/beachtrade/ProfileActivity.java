package com.example.tyren.beachtrade;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;

public class ProfileActivity extends AppCompatActivity {

    EditText username;
    EditText firstname;
    EditText lastname;
    EditText emailAddress;
    EditText phoneNumber;
    ProfileMapperClass retrievedProfile;

    Button saveButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = (EditText) findViewById(R.id.username);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        emailAddress = (EditText) findViewById(R.id.emailAddress);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        saveButton = (Button) findViewById(R.id.bSave);

        new getDetails().execute();



    }

    private class getDetails extends AsyncTask<Void, Integer, Integer> {
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
                retrievedProfile = dynamoDBMapper.load(ProfileMapperClass.class, "idman");
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            username.setText(retrievedProfile.getUserName());
            emailAddress.setText(retrievedProfile.getEmailAddress());
            if(integer ==1)
            {
                Toast.makeText(ProfileActivity.this, "good to go", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("Bad stuff...","");
            }
        }


    }
}
