package com.example.bikefinderapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChaisNoAdapter extends RecyclerView.Adapter<ChaisNoAdapter.ViewHolder> implements Filterable {

        private List<Bike> bikeListDataSet;
        private List<Bike> bikeListFiltered;
        private  Context mContext;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                textView = (TextView) view.findViewById(R.id.textView);
            }

            public TextView getTextView() {
                return textView;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public ChaisNoAdapter(List<Bike> dataSet, Context context) {
            bikeListDataSet = dataSet;
            mContext=context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.text_row_item, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.getTextView().setText(bikeListFiltered.get(position).getChaiseNo());
            viewHolder.getTextView().setOnClickListener(view -> {
                Intent mapsIntent= new Intent(mContext, MapsActivity.class);
                String chas= ((TextView)view.findViewById(R.id.textView)).getText().toString().trim();
                if (chas.equalsIgnoreCase("")){
                    Toast.makeText(mContext, "No record found !! ", Toast.LENGTH_SHORT).show();

                }else{
                    Bike bike=bikeListFiltered.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("UniqueKey", bike);
                    mapsIntent.putExtras(bundle);
                    mContext.startActivity(mapsIntent);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return bikeListFiltered.size();
        }




    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bikeListFiltered = bikeListDataSet;
                } else {
                    List<Bike> filteredList = new ArrayList<>();
                    for (Bike bike : bikeListDataSet) {
                        if (bike.getChaiseNo().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(bike);
                        }
                    }
                    bikeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bikeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bikeListFiltered = (ArrayList<Bike>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    }