package com.example.rentinghousemanagement.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.activity.AddContract;
import com.example.rentinghousemanagement.activity.AddInvoice;
import com.example.rentinghousemanagement.activity.Compensation;
import com.example.rentinghousemanagement.model.Phong;
import com.example.rentinghousemanagement.model.Service;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhongAdapter extends RecyclerView.Adapter<PhongAdapter.PhongViewHolder>{
    private List<Phong> phongList;
    private Context context;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<Intent> contractLauncher;

    public PhongAdapter(List<Phong> phongList, Context context, ActivityResultLauncher<Intent> contractLauncher) {
        this.phongList = phongList;
        this.context = context;
        this.contractLauncher = contractLauncher;
    }

    @NonNull
    @Override
    public PhongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phong,parent,false);
        return new PhongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhongViewHolder holder, int position) {
        Phong phong = phongList.get(position);
        String soPhong = "Phòng: "+phong.getRoomId();
        holder.txtRoomNumber.setText(soPhong);

        // Kiểm tra trạng thái phòng
        if(phong.getStatus() == 0){
            holder.layoutContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.blue));
        }else {
            holder.layoutContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
        }

        // Bắt sự kiện click vào icon edit
        holder.imgEdit.setOnClickListener(view -> showBottomDialog(phong.getRoomId()));
    }
    @Override
    public int getItemCount() {
        return phongList != null ? phongList.size() : 0;
    }


    public static class PhongViewHolder extends RecyclerView.ViewHolder{
        TextView txtRoomNumber;
        RelativeLayout layoutContainer;
        ImageView imgEdit;

        public PhongViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.txt_room_number);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            imgEdit = itemView.findViewById(R.id.img_edit);
        }
    }

    private void showBottomDialog(int roomId){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_room,null);
        bottomSheetDialog.setContentView(view);

        // Gán roomId
        TextView tvRoomTitle = view.findViewById(R.id.tvRoomTitle);
        tvRoomTitle.setText("Phòng "+roomId);

        // Them hop dong
        LinearLayout btnAddContract = view.findViewById(R.id.btnAddContract);
        btnAddContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddContract.class);
                intent.putExtra("room_id",roomId);
                contractLauncher.launch(intent);
            }
        });

        // Them hoa don
        LinearLayout btnAddInvoice = view.findViewById(R.id.btnAddInvoice);
        btnAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddInvoice.class);
                intent.putExtra("room_id",roomId);
                contractLauncher.launch(intent);
            }
        });

        // Them den bu CSVC
        LinearLayout btnAddCompensation = view.findViewById(R.id.btnCompensation);
        btnAddCompensation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Compensation.class);
                intent.putExtra("room_id",roomId);
                contractLauncher.launch(intent);
            }
        });

        // Mở Edit Phòng
        LinearLayout btnEditRoom = view.findViewById(R.id.btnEditRoom);
        btnEditRoom.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showEditRoomDialog(roomId);
        });

        bottomSheetDialog.show();
    }

    private void showEditRoomDialog(int roomId) {
        BottomSheetDialog editRoomDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_room, null);
        editRoomDialog.setContentView(view);

        // Ánh xạ các editText
        TextView tvRoomTitle = view.findViewById(R.id.tv_Room_Title);
        EditText edtSquare = view.findViewById(R.id.edt_square);
        EditText edtPrice = view.findViewById(R.id.edt_price);
        EditText edtCapacity = view.findViewById(R.id.edt_capacity);

        // Ánh xạ RecyclerView để chọn dịch vụ
        RecyclerView recyclerView = view.findViewById(R.id.rcv_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        final ServiceEditAdapter[] adapter = new ServiceEditAdapter[1];

        // Ánh xạ button save de luu dich vu
        Button btn_save = view.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String squareStr = edtSquare.getText().toString().trim();
                String priceStr = edtPrice.getText().toString().trim();
                String capacityStr = edtCapacity.getText().toString().trim();

                if (squareStr.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double square = Double.parseDouble(squareStr);
                double price = Double.parseDouble(priceStr);
                int capacity = Integer.parseInt(capacityStr);

                List<Service> selectedServices = adapter[0].getSelectedService();
                executorService.execute(()->{
                    boolean isSuccess = DatabaseHelper.updateRoomAndService(roomId, userId, square, price, capacity, selectedServices);
                    ((Activity) context).runOnUiThread(() -> {
                        if (isSuccess) {
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            editRoomDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });

                });
            }
        });


        // Gán tiêu đề và các thông tin Room
        tvRoomTitle.setText("Sửa thông tin phòng " + roomId);

        // Lấy dữ liệu gán vào các ô tương ứng

        executorService.execute(() -> {
            Phong room = DatabaseHelper.getRoomById(roomId);
            List<Service> allServices = DatabaseHelper.getServicesByUserId(userId);
            List<Service> roomServices = DatabaseHelper.getServiceByRoomId(room.getRoomId());

            ((Activity)context).runOnUiThread(()->{
                if(room !=null){
                    edtSquare.setText(String.valueOf(room.getSquare()));
                    edtPrice.setText(String.valueOf(room.getPrice()));
                    edtCapacity.setText(String.valueOf(room.getCapacity()));
                }
                // Đánh dấu dịch vụ nào đã được chọn trước đó
                for (Service service : allServices) {
                    Log.d("SERVICE_CHECK", service.getServiceName() + " - Selected: " + service.isSelected() + " - Quantity: " + service.getQuantity());
                    for (Service roomService : roomServices) {
                        Log.d("SERVICE_CHECK", roomService.getServiceName() + " - Selected: " + roomService.isSelected() + " - Quantity: " + roomService.getQuantity());
                        if (service.getServiceId() == roomService.getServiceId()) {
                            service.setSelected(true);
                            service.setQuantity(roomService.getQuantity());
                            break;
                        }
                    }
                }

                // Gán Adapter vào RecyclerView
                adapter[0] = new ServiceEditAdapter(allServices, context);
                recyclerView.setAdapter(adapter[0]);
            });
        });
        editRoomDialog.show();
    }
}
