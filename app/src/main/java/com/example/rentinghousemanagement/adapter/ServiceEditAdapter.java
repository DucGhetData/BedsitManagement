package com.example.rentinghousemanagement.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceEditAdapter extends RecyclerView.Adapter<ServiceEditAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private Context context;

    public ServiceEditAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service,parent,false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.checkBoxService.setText(service.getServiceName());
        holder.checkBoxService.setChecked(service.isSelected());
        if (service.isSelected()) {
            holder.edtQuantity.setVisibility(View.VISIBLE);
            holder.edtQuantity.setText(String.valueOf(service.getQuantity()));
        } else {
            holder.edtQuantity.setVisibility(View.GONE);
            holder.edtQuantity.setText("");
        }

        holder.checkBoxService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            service.setSelected(isChecked);
            if (isChecked) {
                holder.edtQuantity.setVisibility(View.VISIBLE);
            } else {
                holder.edtQuantity.setVisibility(View.GONE);
                holder.edtQuantity.setText("");
                service.setQuantity(0);
            }
        });

        holder.edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        double raw = Double.parseDouble(s.toString());
                        int quantity = (int) raw;
                        service.setQuantity(quantity);
                    } catch (NumberFormatException e) {
                        service.setQuantity(0);
                    }
                } else {
                    service.setQuantity(0);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public List<Service> getSelectedService(){
        List<Service> selected = new ArrayList<>();
        for(Service s: serviceList){
            if(s.isSelected()){
                selected.add(s);
            }
        }
        return selected;
    }


    public static class ServiceViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBoxService;
        EditText edtQuantity;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxService = itemView.findViewById(R.id.checkbox_service);
            edtQuantity = itemView.findViewById(R.id.edt_quantity);
        }
    }
}
