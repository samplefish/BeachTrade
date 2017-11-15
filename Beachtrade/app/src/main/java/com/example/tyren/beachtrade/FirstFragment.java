package com.example.tyren.beachtrade;

/**
 * Created by tyren on 10/25/2017.
 */

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment {

    Button newPostButton;
    Dialog progress;
    ScanResult result;
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_layout, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //
        // recyclerView.setAdapter(new RecyclerViewAdapter(list));
        new checkItems().execute();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        newPostButton = (Button) v.findViewById(R.id.postFeedButton);

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new SubmitItemFragment()).commit();
            }
        });
    }


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextView;
        private TextView mNameView;

        private TextView mPriceView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view, container, false));

            mCardView = (CardView) itemView.findViewById(R.id.card_container);
            mTextView = (TextView) itemView.findViewById(R.id.text_holder);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mPriceView = (TextView) itemView.findViewById(R.id.price);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private ScanResult result;

        public RecyclerViewAdapter(ScanResult result) {
            this.result = result;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {

            holder.mTextView.setText(result.getItems().get(position).get("description").toString().replace("{S:","").replace(",}",""));
            holder.mNameView.setText(result.getItems().get(position).get("itemName").toString().replace("{S:","").replace(",}",""));
            holder.mPriceView.setText("$"+result.getItems().get(position).get("price").toString().replace("{N:","").replace(",}",""));


        }

        @Override
        public int getItemCount() {
            return result.getCount();
        }
    }

    public class checkItems extends AsyncTask<Void, Integer, Integer> {
        protected void onPreExecute(){
            /*progress = new ProgressDialog(getActivity());
            progress.setMessage("Working...");
            progress.show();*/
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


            ScanRequest scanRequest = new ScanRequest().withTableName("userItems");

            result = ddbClient.scan(scanRequest);

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            recyclerView.setAdapter(new RecyclerViewAdapter(result));
        }
    }


}
