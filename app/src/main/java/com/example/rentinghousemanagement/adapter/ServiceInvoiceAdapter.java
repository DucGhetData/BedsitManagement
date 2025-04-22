package com.example.rentinghousemanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceInvoiceAdapter extends RecyclerView.Adapter<ServiceInvoiceAdapter.ServiceViewHolder> {
    private List<Service> services;

    public ServiceInvoiceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvServicePrice.setText(numberFormat.format(service.getPrice()) + " VNĐ/đơn vị");
        holder.tvServiceNumber.setText("x " + service.getQuantity());
        holder.tvServiceTotal.setText(numberFormat.format(service.getTotal_price()) + " VNĐ");

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder{
        TextView tvServiceName;
        TextView tvServicePrice;
        TextView tvServiceNumber;
        TextView tvServiceTotal;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
            tvServiceNumber = itemView.findViewById(R.id.tv_service_number);
            tvServiceTotal = itemView.findViewById(R.id.tv_service_total);

        }
    }
}
