package com.example.rentinghousemanagement.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddContract extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private EditText edtTenantName, edtIdNumber, edtPhone, edtBirthDate;
    private EditText edtStartDate, edtEndDate, edtDeposit, edtRoomId;
    private Button btnSave, btnCancel;
    private SharedPreferences sharedPreferences;
    private int roomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contract);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các thanh phan giao dien
        edtTenantName = findViewById(R.id.edt_name);
        edtIdNumber = findViewById(R.id.edt_id_number);
        edtPhone = findViewById(R.id.edt_phonenumber);
        edtBirthDate = findViewById(R.id.edt_birthdate);
        // Hop dong
        edtRoomId = findViewById(R.id.edt_roomid);
        edtStartDate = findViewById(R.id.edt_startdate);
        edtEndDate = findViewById(R.id.edt_enddate);
        edtDeposit = findViewById(R.id.edt_depositamount);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);


        // Nhận roomId từ Intent
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("room_id")){
            roomId = intent.getIntExtra("room_id",-1);
            edtRoomId.setText("Phòng số:"+roomId);
            edtRoomId.setEnabled(false); // Ko cho sửa
        }
        // Lay usser_id tu ShareReference
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",-1);

        // Gán DatePicker
        setupDatePicker(edtStartDate);
        setupDatePicker(edtEndDate);
        setupDatePicker(edtBirthDate);

        // Luu hop dong
        btnSave.setOnClickListener(v->{
            String name = edtTenantName.getText().toString().trim();
            String idNumber = edtIdNumber.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String birthDate = edtBirthDate.getText().toString().trim();

            String startDate = edtStartDate.getText().toString().trim();
            String endDate = edtEndDate.getText().toString().trim();
            String depositStr = edtDeposit.getText().toString().trim();
            float deposit = Float.parseFloat(depositStr);

            if (name.isEmpty() || idNumber.isEmpty() || phone.isEmpty() || birthDate.isEmpty()
                    || startDate.isEmpty() || endDate.isEmpty() || depositStr.isEmpty()) {
                Toast.makeText(AddContract.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(()->{
                int tenantId = DatabaseHelper.insertTenant(name,idNumber,phone,birthDate);
                if(tenantId == -1){
                    handler.post(()->Toast.makeText(AddContract.this, "❌ Thêm khách thuê thất bại", Toast.LENGTH_SHORT).show());
                    return;
                }
                int contractId = DatabaseHelper.insertContract(user_id,tenantId,roomId,startDate,endDate,deposit);
                handler.post(()->{
                    if(contractId != -1){
                        Toast.makeText(AddContract.this, "✅ Đã lưu hợp đồng thành công!", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(AddContract.this, "❌ Lưu hợp đồng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
                DatabaseHelper.updateRoomStatus(roomId,user_id,1);

            });
        });

        // Quay la trang trước
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                    this,
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