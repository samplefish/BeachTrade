package com.example.tyren.beachtrade;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;

public class SubmitItemActivity extends AppCompatActivity {

    EditText itemName;
    Button submit;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_item);
        itemName = (EditText) findViewById(R.id.etItemName);
        submit = (Button) findViewById(R.id.bSubmit);





        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = itemName.getText().toString();
                new updateTable().execute();

            }
        });


    }

    private class updateTable extends AsyncTask<Void, Integer, Integer>{
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

            ItemsMapperClass itemMapper = new ItemsMapperClass();
            itemMapper.setItemName(mName);
            itemMapper.setUserID(userID);

            if(credentialsProvider != null && itemMapper != null){
                DynamoDBMapper dynamoDBMapper = ManagerClass.intiDynamoClient(credentialsProvider);
                dynamoDBMapper.save(itemMapper);
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(integer ==1)
            {
                Toast.makeText(SubmitItemActivity.this, "good to go", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
