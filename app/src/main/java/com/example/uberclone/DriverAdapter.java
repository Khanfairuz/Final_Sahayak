package com.example.uberclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    private List<Driver> driverList;
    private OnDriverSelectedListener listener;

    public DriverAdapter(List<Driver> driverList, OnDriverSelectedListener listener) {
        this.driverList = driverList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list_item, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driverList.get(position);
        holder.bind(driver, listener);
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public interface OnDriverSelectedListener {
        void onDriverSelected(Driver driver);
    }

    static class DriverViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private RatingBar ratingBar;
        private Button chooseDriverButton;

        DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.driver_name);
            ratingBar = itemView.findViewById(R.id.driver_rating);
            chooseDriverButton = itemView.findViewById(R.id.choose_driver_button);
        }

        void bind(Driver driver, OnDriverSelectedListener listener) {
            nameTextView.setText(driver.getName());
            ratingBar.setRating(driver.getRating());
            chooseDriverButton.setOnClickListener(v -> listener.onDriverSelected(driver));
        }
    }
}
