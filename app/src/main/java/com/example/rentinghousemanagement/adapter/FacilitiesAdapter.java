package com.example.rentinghousemanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.Service;

import java.util.List;
import java.util.Locale;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder> {
    private List<Service> servicesList;
    private Runnable onQuantityChanged;

    public FacilitiesAdapter(List<Service> servicesList, Runnable onQuantityChanged) {
        this.servicesList = servicesList;
        this.onQuantityChanged = onQuantityChanged;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Service service = servicesList.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }


    public class FacilityViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView;
        private TextView priceTextView;
        private Button decreaseButton;
        private TextView quantityTextView;
        private Button increaseButton;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_name);
            priceTextView = itemView.findViewById(R.id.facility_price);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            quantityTextView = itemView.findViewById(R.id.quantity_text);
            increaseButton = itemView.findViewById(R.id.increase_button);
        }

        public void bind(Service service){
            nameTextView.setText(service.getServiceName());
            priceTextView.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", service.getPrice()));
            quantityTextView.setText(String.valueOf(service.getQuantity()));

            decreaseButton.setOnClickListener(v->{
                int quantity = service.getQuantity();
                if (quantity > 0) {
                    service.setQuantity(quantity - 1);
                    quantityTextView.setText(String.valueOf(service.getQuantity()));
                    onQuantityChanged.run();
                }
            });

            increaseButton.setOnClickListener(v->{
                int quantity = service.getQuantity();
                service.setQuantity(quantity + 1);
                quantityTextView.setText(String.valueOf(service.getQuantity()));
                onQuantityChanged.run();
            });
        }
    }

}
