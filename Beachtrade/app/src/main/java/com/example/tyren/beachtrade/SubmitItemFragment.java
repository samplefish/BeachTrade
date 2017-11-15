package com.example.tyren.beachtrade;


import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3Client;
import com.facebook.AccessToken;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubmitItemFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 1;

    AccessToken accessToken;
    String userID;

    EditText itemName;
    Button submit;
    String mName;

    Double price;
    String description;
    String pictureLink;
    String itemID;
    String itemType;

    Button saveButton;
    View v;

    List<ItemsMapperClass> result;
    Integer resultSize;
    ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_item, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accessToken = AccessToken.getCurrentAccessToken();
        userID = accessToken.getUserId();
        v = getView();

        itemName = (EditText) v.findViewById(R.id.etItemName);
        submit = (Button) v.findViewById(R.id.bSubmit);

        price = 0.99;
        description = "lettuce";
        pictureLink = "chicken.jpg";
        itemType = "ice cream";



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = itemName.getText().toString();
                new SubmitItemFragment.checkItems().execute();

            }
        });


    }

    private class checkItems extends AsyncTask<Void, Integer, Integer> {
        protected void onPreExecute(){
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Working...");
            progress.show();
        }
        protected Integer doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity().getApplicationContext(),
                    "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(userID));
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("userID = :val1").withExpressionAttributeValues(eav);
            result = mapper.scan(ItemsMapperClass.class, scanExpression);

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                if(result != null){
                    resultSize = result.size();
                    new SubmitItemFragment.updateTable().execute();
                }
            }
        }
    }

    private class updateTable extends AsyncTask<Void, Integer, Integer>{
        @Override
        protected Integer doInBackground(Void... params){

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity().getApplicationContext(),
                    "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(userID));
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("userID = :val1").withExpressionAttributeValues(eav);
            result = mapper.scan(ItemsMapperClass.class, scanExpression);

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            String userID = accessToken.getUserId();

            ItemsMapperClass itemMapper = new ItemsMapperClass();
            itemMapper.setItemName(mName);
            itemMapper.setPrice(price);
            itemMapper.setDescription(description);
            itemMapper.setItemID(userID+""+resultSize);
            itemMapper.setPictureLink(pictureLink);
            itemMapper.setItemType(itemType);


            itemMapper.setUserID(userID);

            if(credentialsProvider != null && itemMapper != null){

                DynamoDBMapper dynamoDBMapper = managerClass.intiDynamoClient(credentialsProvider);
                dynamoDBMapper.save(itemMapper);
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(integer ==1)
            {
                Toast.makeText(getActivity(), "Your item has been added.", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new FirstFragment()).commit();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

        }


    }
}
