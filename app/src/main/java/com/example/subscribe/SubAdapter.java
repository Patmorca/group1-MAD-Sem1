package com.example.subscribe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.Subholder> {
    Context context;
    ArrayList<Subscription> subscriptions;

    public SubAdapter(Context context, ArrayList<Subscription> subscriptions) {
        this.context = context;
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    public SubAdapter.Subholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mainviewlist,parent ,false);
        return new Subholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubAdapter.Subholder holder, int position) {

        Subscription subs = subscriptions.get(position);
        Log.d("SubAdapter", "Subscription at position " + position + ": " + subs.getSubName());
        holder.subName.setText(subs.getSubName());
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    public  static  class Subholder extends RecyclerView.ViewHolder{
        public TextView subName;
        public Subholder(@NonNull View itemView) {
            super(itemView);
            subName = (TextView) itemView.findViewById(R.id.MVL_text);
        }
    }
}
