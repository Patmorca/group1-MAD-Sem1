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
    private final MainListRVInterface RVI;
    Context context;
    ArrayList<Subscription> subscriptions;

    public SubAdapter(Context context, ArrayList<Subscription> subscriptions, MainListRVInterface RVI) {
        this.context = context;
        this.subscriptions = subscriptions;
        this.RVI = RVI;
    }

    @NonNull
    @Override
    public SubAdapter.Subholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mainviewlist,parent ,false);
        return new Subholder(v,RVI);
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
        public Subholder(@NonNull View itemView, MainListRVInterface RVI) {
            super(itemView);
            subName = (TextView) itemView.findViewById(R.id.MVL_text);

            //CLICK LOGIC FOR VIEW SUB ACTIVITY
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(RVI != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            RVI.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
