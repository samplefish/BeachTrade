package com.example.tyren.beachtrade;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    EditText username;
    EditText firstname;
    EditText lastname;
    EditText emailAddress;
    EditText phoneNumber;
    ProfileMapperClass retrievedProfile;

    Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        username = (EditText) v.findViewById(R.id.username);
        firstname = (EditText) v.findViewById(R.id.firstname);
        lastname = (EditText) v.findViewById(R.id.lastname);
        emailAddress = (EditText) v.findViewById(R.id.emailAddress);
        phoneNumber = (EditText) v.findViewById(R.id.phoneNumber);

        saveButton = (Button) v.findViewById(R.id.bSave);

        new ProfileFragment.getDetails().execute();

    }

    private class getDetails extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params){

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity().getApplicationContext(),
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
            firstname.setText(retrievedProfile.getFirstName());
            lastname.setText(retrievedProfile.getLastName());
            phoneNumber.setText(retrievedProfile.getPhoneNumber());
            if(integer ==1)
            {
                //Toast.makeText(NavigationBarActivity.class, "good to go", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("Bad stuff...","");
            }
        }


    }


}
