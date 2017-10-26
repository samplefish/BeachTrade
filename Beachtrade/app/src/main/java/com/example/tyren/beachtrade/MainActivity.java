package com.example.tyren.beachtrade;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {

    Button sellButton;
    TextView emailView, userIDView;
    ProfileMapperClass retrievedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailView = (TextView) findViewById(R.id.emailView);
        userIDView = (TextView) findViewById(R.id.userIDView);
        sellButton = (Button) findViewById(R.id.sellButton);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SubmitItemActivity.class);
                startActivity(intent);
            }
        });

        new getDetails().execute();







    }

    public void onBackPressed() {
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
            userIDView.setText(retrievedProfile.getUserID());
            emailView.setText(retrievedProfile.getEmailAddress());
            if(integer ==1)
            {
                Toast.makeText(MainActivity.this, "good to go", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("Bad stuff...","");
            }
        }


    }



}
