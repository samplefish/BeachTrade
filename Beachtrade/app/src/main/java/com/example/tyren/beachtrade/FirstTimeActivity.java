package com.example.tyren.beachtrade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.facebook.AccessToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTimeActivity extends AppCompatActivity {

    EditText userName;
    Button submitButton;
    String usernamePattern;
    String tUserName;
    ProfileMapperClass profileToFind;
    List<ProfileMapperClass> result;
    AccessToken accessToken;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        usernamePattern = "^[a-zA-Z0-9._-]{3,}$";

        userName = (EditText) findViewById(R.id.userName);
        userName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

                for(int i = s.length(); i > 0; i--) {

                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });

        submitButton = (Button) findViewById(R.id.submitButton);

        accessToken = AccessToken.getCurrentAccessToken();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().matches(usernamePattern)){
                    tUserName = userName.getText().toString();
                    profileToFind = new ProfileMapperClass();
                    profileToFind.setUserName(tUserName);
                    new checkUsername().execute();

                }
                else{
                    Toast.makeText(FirstTimeActivity.this, "Please enter a valid username.", Toast.LENGTH_SHORT).show();
                    Log.e("Regex broken?","Baaad..");
                }

            }
        });


    }
    private class checkUsername extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(FirstTimeActivity.this);
            progress.setMessage("Working...");
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
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            String userID = accessToken.getUserId();

            /*DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedList<ProfileMapperClass> result = mapper.scan(ProfileMapperClass.class, )*/

            //result = mapper.load(ProfileMapperClass.class, userID);

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(tUserName));
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("userName = :val1").withExpressionAttributeValues(eav);
            result = mapper.scan(ProfileMapperClass.class, scanExpression);


            /*ProfileMapperClass profileMapper = new ProfileMapperClass();
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression<ProfileMapperClass>()
                    .withHashKeyValues(profileToFind)
                    .withConsistentRead(false);*/

            //PaginatedList<ProfileMapperClass> result = mapper.query(ProfileMapperClass.class, queryExpression);

            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            progress.dismiss();
            if(result!=null){
                if(result.size()>0){
                    Toast.makeText(FirstTimeActivity.this, "That username is taken. Try again.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FirstTimeActivity.this, "Creating your new username...", Toast.LENGTH_SHORT).show();
                    new createNewUsername().execute();
                }

                Log.v("exec","return !null.");

            }
            if(result == null){
                Log.v("exec","return null.");
                Toast.makeText(FirstTimeActivity.this, "Creating your new username...", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private class createNewUsername extends AsyncTask<Void, Integer, Integer>{
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

            profileMapper.setUserID(userID);
            profileMapper.setUserName(tUserName);

            if(credentialsProvider != null && profileMapper != null){
                DynamoDBMapper dynamoDBMapper = ManagerClass.intiDynamoClient(credentialsProvider);
                dynamoDBMapper.save(profileMapper);
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(integer ==1)
            {
                Toast.makeText(FirstTimeActivity.this, "Username set! Welcome, "+tUserName, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FirstTimeActivity.this, NavigationBarActivity.class));
            }

        }


    }
}
