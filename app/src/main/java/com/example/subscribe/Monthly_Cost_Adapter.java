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

    public class Monthly_Cost_Adapter extends RecyclerView.Adapter<Monthly_Cost_Adapter.MonthlyHolder> {
        private final MainListRVInterface RVI;
        Context context;
        ArrayList<Subscription> Subs;



        public Monthly_Cost_Adapter(Context context, ArrayList<Subscription> subs , MainListRVInterface RVI)  {
            this.context = context;
            Subs = subs;
            this.RVI = RVI;
        }

        @NonNull
        @Override
        public MonthlyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.amc_viewlist , parent , false);
            return new MonthlyHolder(v , RVI);
        }

        @Override
        public void onBindViewHolder(@NonNull MonthlyHolder holder, int position) {
            Subscription subs = Subs.get(position);
            Log.d("SubsCostAdapter", "Subscription at position " + position + ": " + subs.getSubName());
            holder.subName.setText(subs.getSubName());
            Log.d("SubsCostAdapter", "Subscription at position " + position + ": " + subs.getCost());
            holder.price.setText(String.valueOf(subs.getCost())); // Chuyển đổi int thành String trước khi gán cho TextView
        }


        @Override
        public int getItemCount() {
            return Subs.size();
        }

        public static class MonthlyHolder extends RecyclerView.ViewHolder {
            public TextView subName , price;
            public MonthlyHolder(@NonNull View itemView , MainListRVInterface RVI) {
                super(itemView);
                subName =(TextView) itemView.findViewById(R.id.AMCVL_name);
                price = (TextView) itemView.findViewById(R.id.AMCVL_Price);


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

