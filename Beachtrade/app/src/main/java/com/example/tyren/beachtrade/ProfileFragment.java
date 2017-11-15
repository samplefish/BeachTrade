package com.example.tyren.beachtrade;


import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.facebook.AccessToken;

import java.io.File;
import java.io.FileOutputStream;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView profileImage;
    File downloadedImage;

    EditText username;
    EditText firstname;
    EditText lastname;
    EditText emailAddress;
    EditText phoneNumber;
    ProfileMapperClass retrievedProfile;

    String tUserName;
    String tFirstName ;
    String tLastName;
    String tEmailAddress ;
    String tPhoneNumber;

    AccessToken accessToken;
    String userID;

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
        accessToken = AccessToken.getCurrentAccessToken();
        userID = accessToken.getUserId();
        downloadedImage = null;
        View v = getView();

        profileImage = (ImageView) v.findViewById(R.id.imageToUpload);

        username = (EditText) v.findViewById(R.id.username);
        username.setEnabled(false);

        firstname = (EditText) v.findViewById(R.id.firstname);
        lastname = (EditText) v.findViewById(R.id.lastname);
        emailAddress = (EditText) v.findViewById(R.id.emailAddress);
        phoneNumber = (EditText) v.findViewById(R.id.phoneNumber);

        saveButton = (Button) v.findViewById(R.id.bSave);

        new ProfileFragment.getDetails().execute();

        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tFirstName = firstname.getText().toString();
                tLastName = lastname.getText().toString();
                tEmailAddress = emailAddress.getText().toString();
                tPhoneNumber = phoneNumber.getText().toString();
                tUserName = username.getText().toString();
                new ProfileFragment.updateDetails().execute();
                //new ProfileFragment.downloadImage().execute();
            }
        });
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


            ProfileMapperClass profileMapper = new ProfileMapperClass();


            if(credentialsProvider != null && profileMapper != null){
                DynamoDBMapper dynamoDBMapper = ManagerClass.intiDynamoClient(credentialsProvider);
                retrievedProfile = dynamoDBMapper.load(ProfileMapperClass.class, userID);
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(retrievedProfile != null){
                username.setText(retrievedProfile.getUserName());
                emailAddress.setText(retrievedProfile.getEmailAddress());
                firstname.setText(retrievedProfile.getFirstName());
                lastname.setText(retrievedProfile.getLastName());
                phoneNumber.setText(retrievedProfile.getPhoneNumber());
            }
            if(emailAddress.getText().toString() == "")
            {
                emailAddress.setHint("first name not set");
            }

            if(integer ==1)
            {
                //Toast.makeText(NavigationBarActivity.class, "good to go", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("Bad stuff...","");
            }
        }


    }
    private class updateDetails extends AsyncTask<Void, Integer, Integer>{
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
            profileMapper.setUserName(tUserName);
            profileMapper.setUserID(userID);
            profileMapper.setEmailAddress(tEmailAddress);
            profileMapper.setFirstName(tFirstName);
            profileMapper.setLastName(tLastName);
            profileMapper.setPhoneNumber(tPhoneNumber);


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
                Toast.makeText(getActivity(), "Your profile details have been updated!", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private class downloadImage extends AsyncTask<Void, Void, Void>{
        Dialog dialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();


        }
        @Override
        protected Void doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            managerClass.getCredentials(getActivity());
            AmazonS3Client s3Client = managerClass.initS3Client(getActivity());
            TransferUtility transferUtility = managerClass.checkTransferUtility(s3Client, getActivity().getApplicationContext());
            downloadedImage = new File(Environment.getExternalStorageDirectory().toString() + "/64897429_p0.png");
            TransferObserver observer = transferUtility.download("beachtrade","64897429_p0.png",downloadedImage);
            return null;
        }

        protected void onPostExecute(){
            Bitmap bitmap = BitmapFactory.decodeFile(downloadedImage.getPath());
            profileImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE &&  resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
        }
    }
}
