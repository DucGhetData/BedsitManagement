package com.example.rentinghousemanagement.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.adapter.PhongAdapter;
import com.example.rentinghousemanagement.adapter.ServiceAdapter;
import com.example.rentinghousemanagement.model.Phong;
import com.example.rentinghousemanagement.model.Service;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhongFragment extends Fragment {
    private RecyclerView recyclerView;
    private PhongAdapter phongAdapter;
    private List<Phong> phongList;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<Intent> contractLauncher;
    public PhongFragment() {

    }
    public static PhongFragment newInstance() {

        return new PhongFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phong,container,false);
        // Ánh xạ RecycleView
        recyclerView = view.findViewById(R.id.rcv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        // Gọi hàm loadRoomsData
        loadRoomData();

        // Ánh xạ nút +
        View fab = view.findViewById(R.id.addfab);
        // Bắt sự kiện để hiển thị bottom_dialog
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contractLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadRoomData(); // cập nhật lại danh sách
                    }
                }
        );
        // Gọi hàm để setup adapter và truyền launcher này vào
        setupAdapter();
    }


    // Hàm hiện Bottom_Sheet
    private void showBottomDialog(){
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
       View  bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_room,null);
       bottomSheetDialog.setContentView(bottomSheetView);

        // Ánh xạ các EditText để nhập thông tin phòng
        EditText edtSquare = bottomSheetDialog.findViewById(R.id.edt_square);
        EditText edtPrice = bottomSheetDialog.findViewById(R.id.edt_price);
        EditText edtCapacity = bottomSheetDialog.findViewById(R.id.edt_capacity);
        Button btnAdd = bottomSheetDialog.findViewById(R.id.btn_save);

        // Ánh xạ RecyclerView để chọn dịch vụ
       RecyclerView recyclerView = bottomSheetView.findViewById(R.id.rcv_services);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        // Load danh sách dịch vụ từ DatabaseHelper
       executorService.execute(()->{
           List<Service> services = DatabaseHelper.getServicesByUserId(userId);

           requireActivity().runOnUiThread(()->{
               ServiceAdapter adapter = new ServiceAdapter(services,getContext());
               recyclerView.setAdapter(adapter);
           });
       });

        // Xử lý sự kiện khi nhấn "Thêm"
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String squareStr = edtSquare.getText().toString().trim();
                String priceStr = edtPrice.getText().toString().trim();
                String capacityStr = edtCapacity.getText().toString().trim();

                if (squareStr.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                float square = Float.parseFloat(squareStr);
                float price = Float.parseFloat(priceStr);
                int capacity = Integer.parseInt(capacityStr);
                int status = 0;

                executorService.execute(()->{
                    // Thêm phòng vào Rooms trước
                    int roomId = DatabaseHelper.insertRoom(userId, square, price, capacity,status);
                    if(roomId != -1){
                        requireActivity().runOnUiThread(()->Toast.makeText(getContext(),"Thêm phòng thành công!",Toast.LENGTH_SHORT).show());

                        ServiceAdapter adapter = (ServiceAdapter) recyclerView.getAdapter();
                        if(adapter != null){
                            List<Service> selectedServices = adapter.getServiceList();
                            boolean success = DatabaseHelper.insertRoomFacilities(roomId,selectedServices,recyclerView);
                            if(success){
                                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Thêm dịch vụ thành công!", Toast.LENGTH_SHORT).show());
                            }
                        }
                        // Cập nhật danh sách phòng trên giao diện
                        requireActivity().runOnUiThread(PhongFragment.this::loadRoomData);
                    }else {
                        requireActivity().runOnUiThread(()->Toast.makeText(getContext(),"Thêm phòng thất bại!",Toast.LENGTH_SHORT).show());
                    }
                });
                bottomSheetDialog.dismiss(); // Đóng dialog sau khi thêm
            }
        });
       bottomSheetDialog.show();
    }

    // Hàm load danh sách phòng trọ
    private void loadRoomData(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id",-1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(()->{
            List<Phong>  rooms = DatabaseHelper.getAllRooms(userId);
            if(rooms!=null){
                requireActivity().runOnUiThread(()->{
                    phongList = rooms;
                    phongAdapter = new PhongAdapter(phongList,getContext(),contractLauncher);
                    recyclerView.setAdapter(phongAdapter);
                });
            }
        });
    }

    private void setupAdapter() {
        phongAdapter = new PhongAdapter(phongList, getContext(), contractLauncher);
        recyclerView.setAdapter(phongAdapter);
    }

}
