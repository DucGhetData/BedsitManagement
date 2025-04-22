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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{
    private List<Service> serviceList;
    private Context context;
    private List<Service> selectedService = new ArrayList<>();

    public ServiceAdapter(List<Service> serviceList, Context context) {
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

        // Đảm bảo CheckBox luôn hiển thị ngay từ đầu
        holder.checkBoxService.setChecked(false);
        holder.edtQuantity.setVisibility(View.GONE);
        holder.edtQuantity.setText(""); // Xóa dữ liệu cũ nếu có

        // Xử lý sự kiện khi người dùng chọn/bỏ chọn CheckBox
        holder.checkBoxService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            service.setSelected(isChecked);
            if (isChecked) {
                holder.edtQuantity.setVisibility(View.VISIBLE);
            } else {
                holder.edtQuantity.setVisibility(View.GONE);
                holder.edtQuantity.setText("");
            }
        });

        // Xử lý khi nhập số lượng dịch vụ
        holder.edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    try {
                        int quatity = Integer.parseInt(s.toString());
                        service.setQuantity(quatity);
                    }catch (NumberFormatException e){
                        service.setQuantity(0);
                    }
                }else {
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

    public List<Service> getServiceList() {
        List<Service> selectedList = new ArrayList<>();
        for(Service service: serviceList){
            if(service.isSelected()){
                selectedList.add(service);
            }
        }
        return selectedList;
    }


    public static class ServiceViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBoxService;
        EditText edtQuantity;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.checkBoxService = itemView.findViewById(R.id.checkbox_service);
            this.edtQuantity = itemView.findViewById(R.id.edt_quantity);
        }
    }

}
