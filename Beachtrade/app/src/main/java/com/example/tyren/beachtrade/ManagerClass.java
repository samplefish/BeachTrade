package com.example.tyren.beachtrade;

import android.content.Context;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by William on 10/24/2017.
 */

public class ManagerClass {
    CognitoCachingCredentialsProvider credentialsProvider = null;
    CognitoSyncManager syncManager=null;
    AmazonS3Client s3Client=null;
    TransferUtility transferUtility = null;

    public static AmazonDynamoDBClient dynamoDBClient=null;
    public static DynamoDBMapper dynamoDBMapper=null;

    public CognitoCachingCredentialsProvider getCredentials(Context context){
        credentialsProvider = new CognitoCachingCredentialsProvider(context,
                "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923",
                Regions.US_EAST_1);
        CognitoSyncManager syncClient = new CognitoSyncManager(
                context,
                Regions.US_EAST_1, // Region
                credentialsProvider);

        syncManager = new CognitoSyncManager(context, Regions.US_EAST_1,credentialsProvider);
        Dataset dataset = syncClient.openOrCreateDataset("Mydataset");
        dataset.put("mykey","myvalue");
        dataset.synchronize(new DefaultSyncCallback());
        return credentialsProvider;

    }

    public static DynamoDBMapper intiDynamoClient(CognitoCachingCredentialsProvider credentialsProvider){
        if(dynamoDBClient==null){
            BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJQB6ET3MLU3QSZGQ", "sSUYLtLr+wnYPjW8ls9ae+2VOWIr743bZwQynf3E");
            dynamoDBClient = new AmazonDynamoDBClient(credentials);
            dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1));
            dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        }
        return dynamoDBMapper;
    }
}
