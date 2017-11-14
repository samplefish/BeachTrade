package com.example.tyren.beachtrade;

/**
 * Created by tyren on 10/25/2017.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.first_layout,container,false);
List<String> list = new ArrayList<>();
        list.add("ONE");
        list.add("TWO");
        list.add("THREE");
        list.add("FOUR");
        list.add("FIVE");

        RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerViewAdapter(list));

        return view;
    }

private class RecyclerViewHolder extends RecyclerView.ViewHolder{
    private CardView mCardView;
    private TextView mTextView;
    public RecyclerViewHolder(View itemView){
        super(itemView);
    }

public RecyclerViewHolder(LayoutInflater inflater,ViewGroup container){
    super(inflater.inflate(R.layout.card_view,container,false));

mCardView= (CardView) itemView.findViewById(R.id.card_container);
mTextView= (TextView) itemView.findViewById(R.id.text_holder);
}
}

private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{
private List<String> mList;

    public RecyclerViewAdapter(List<String> list){
    this.mList=list;
}
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       LayoutInflater inflater= LayoutInflater.from(getActivity());
        return new RecyclerViewHolder(inflater,parent);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
holder.mTextView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}







}
