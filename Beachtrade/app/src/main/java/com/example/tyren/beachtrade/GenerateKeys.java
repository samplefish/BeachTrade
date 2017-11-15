package com.example.tyren.beachtrade;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by William on 11/9/2017.
 */

public class GenerateKeys {
    Context context;

    ArrayList<HashMap<String,Object>> keysHolder = new ArrayList<>();
    List<S3ObjectSummary> s3ObjectSummaries;

    GenerateKeys(Context context){
        this.context = context;
    }

    private class downloadKeys extends AsyncTask<Void, Void, Void>{
        Dialog dialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Loading", "downloading keys");
        }
        @Override
        protected Void doInBackground(Void... params){
            ManagerClass managerClass = new ManagerClass();
            managerClass.getCredentials(context);
            AmazonS3Client s3Client = managerClass.initS3Client(context);
            s3ObjectSummaries = s3Client.listObjects(Utils.myBucket).getObjectSummaries();
            for(S3ObjectSummary summary:s3ObjectSummaries){
                HashMap<String,Object> maps = new HashMap<>();
                maps.put("key", summary.getKey());

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }
}
