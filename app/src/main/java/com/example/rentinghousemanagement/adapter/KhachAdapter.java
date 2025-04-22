package com.example.rentinghousemanagement.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.Khach;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KhachAdapter extends RecyclerView.Adapter<KhachAdapter.KhachViewHolder> {
    private Context context;
    private List<Khach> khachList;
    private List<Khach> filteredList;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public KhachAdapter(Context context, List<Khach> khachList) {
        this.context = context;
        this.khachList = new ArrayList<>(khachList);
        this.filteredList = new ArrayList<>(khachList);
    }

    @NonNull
    @Override
    public KhachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khach,parent,false);
        return new KhachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhachViewHolder holder, int position) {
        Khach khach = khachList.get(position);
        holder.tvTenKhach.setText((position + 1) + ". Khách Thuê: " + khach.getTenantName());
        holder.tvSDT.setText("SĐT: " + khach.getPhoneNumber());
        holder.tvEndDate.setText("Ngày hết hạn: " + khach.getEndDate());
        holder.tvPhong.setText("Phòng Trọ: " + khach.getRoomId());

        holder.ivEdit.setOnClickListener(v->{
            showEditDialog(v.getContext(), khach);
        });

    }
    @Override
    public int getItemCount() {
        return khachList.size();
    }

    // Hàm lọc danh sách theo tên khách hàng
    public void filterList(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(khachList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Khach khach : khachList) {
                if (khach.getTenantName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(khach);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class KhachViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenKhach, tvSDT, tvPhong, tvEndDate;
        ImageView ivEdit;

        public KhachViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKhach = itemView.findViewById(R.id.tvTenKhach);
            tvSDT = itemView.findViewById(R.id.tvSDT);
            tvPhong = itemView.findViewById(R.id.tvPhong);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }

    private void showEditDialog(Context context, Khach khach){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_khach, null);
        builder.setView(view);

        // Tạo dialog
        AlertDialog dialog = builder.create();

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etIdNumber = view.findViewById(R.id.etIdNumber);
        EditText etBirthDate = view.findViewById(R.id.etBirthDate);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        setupDatePicker(etBirthDate);

        // Đổ dữ liệu cũ
        etName.setText(khach.getTenantName());
        etPhone.setText(khach.getPhoneNumber());
        etIdNumber.setText(khach.getIdNumber());
        etBirthDate.setText(khach.getBirthDate());

        btnUpdate.setOnClickListener(v->{
            // Lấy dữ liệu mới
            String newName = etName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newId = etIdNumber.getText().toString().trim();
            String newBirth = etBirthDate.getText().toString().trim();

           executor.execute(()->{

               DatabaseHelper.updateTenantInfo(khach.getTenantId(),newName,newPhone,newId,newBirth);

               // Cập nhật lại trong danh sách
               khach.setTenantName(newName);
               khach.setPhoneNumber(newPhone);
               khach.setIdNumber(newId);
               khach.setBirthDate(newBirth);

               // Refresh adapter
               ((Activity) context).runOnUiThread(() -> {
                   notifyDataSetChanged();
                   dialog.dismiss();
               });
           });
        });
        dialog.show();
    }

    // Setup Bộ chọn ngày
    private void setupDatePicker(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL); // Không cho bàn phím hiện
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    v.getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // selectedMonth + 1 vì tháng bắt đầu từ 0
                        String selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editText.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

}
