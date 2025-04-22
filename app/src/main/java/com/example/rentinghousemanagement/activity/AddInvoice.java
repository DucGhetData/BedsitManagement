package com.example.rentinghousemanagement.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class AddInvoice extends AppCompatActivity {
    private TextView tvRoomNumber, tvTongTien ;
    private EditText etMaHoaDon, etMaKhach, etNgayTao, etNuoc, etDien, etTienNha, etChiPhiKhac;
    private Button btnTinhTien, btnThem, btnCancel;
    private int roomId, tenantId;
    private String invoiceId;
    private  SharedPreferences sharedPreferences;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler();
    private double tienDien,tienNuoc, tiennha,tong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xa cac thanh phan giao dien
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvTongTien = findViewById(R.id.tvTongTien);
        etMaHoaDon = findViewById(R.id.etMaHoaDon);
        etMaKhach = findViewById(R.id.etMaKhach);
        etNgayTao = findViewById(R.id.etNgayTao);
        etDien = findViewById(R.id.etSoDien);
        etNuoc = findViewById(R.id.etSoNuoc);
        etChiPhiKhac = findViewById(R.id.etChiPhiKhac);
        etTienNha = findViewById(R.id.etTienNha);
        btnTinhTien = findViewById(R.id.btnTinhTien);
        btnThem = findViewById(R.id.btnThem);

        // Lay room_id, userId, tenantId
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("room_id")){
            roomId = intent.getIntExtra("room_id",-1);
            tvRoomNumber.setText("Phòng số:"+roomId);
        }
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",-1);

        // Gan ma khach va ma hoa don
        loadTenantAndInvoiceCode(roomId,user_id);
        //Khoiwri tao bo chon ngay
        setupDatePicker(etNgayTao);

        // Tính tiền
        btnTinhTien.setOnClickListener(v->{
            executorService.execute(()->{
                double giaDien = DatabaseHelper.getServicePrice("Điện",user_id);
                double giaNuoc = DatabaseHelper.getServicePrice("Nước",user_id);

                int soDien = Integer.parseInt(etDien.getText().toString().trim());
                int soNuoc = Integer.parseInt(etNuoc.getText().toString().trim());
                double chiPhiKhac = 0;
                if (!etChiPhiKhac.getText().toString().trim().isEmpty()) {
                    chiPhiKhac = Double.parseDouble(etChiPhiKhac.getText().toString().trim());
                }

                tienDien = soDien * giaDien;
                tienNuoc = soNuoc * giaNuoc;
                tong = tienDien + tienNuoc + tiennha + chiPhiKhac;

                double finalTong = tong;
                handler.post(()->{
                    tvTongTien.setText("Tổng hóa đơn: " + finalTong + " VNĐ");
                });
            });
        });

        btnThem.setOnClickListener(v -> {
            executorService.execute(()->{
                //Lấy dữ liệu từ giao diện
                String date = etNgayTao.getText().toString().trim();
                int soDien = Integer.parseInt(etDien.getText().toString().trim());
                int soNuoc = Integer.parseInt(etDien.getText().toString().trim());
                SaveInvoice(invoiceId,user_id,roomId,tenantId,date,soDien,soNuoc,tienDien,tienNuoc,tong);
                finish();
            });
        });

    }
    // ham lay ma khach vaf hoa don
    private void loadTenantAndInvoiceCode(int roomId, int userId){
        executorService.execute(()->{
            tenantId = DatabaseHelper.getTenantIdByRoom(userId,roomId);
            invoiceId = DatabaseHelper.generateNewInvoiceCode(userId);
            tiennha = DatabaseHelper.getRoomPriceById(userId,roomId);

            this.runOnUiThread(()->{
                etMaKhach.setText(String.valueOf(tenantId));
                etMaHoaDon.setText(invoiceId);
                etTienNha.setText(String.valueOf(tiennha));
            });
        });
    }

    // Ham setupDatePicker
    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Áp dụng style đã tạo trong styles.xml
            //ContextThemeWrapper themedContext = new ContextThemeWrapper(this, R.style.BlueDatePickerDialog);

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

    // Hàm luu hóa ơn
    private void SaveInvoice(String invoiceId, int user_id, int roomId, int tenantId,
                             String date, int soDien, int soNuoc, double tienDien, double tienNuoc, double tongTien ){

        executorService.execute(()->{
            try {
                boolean inserted = DatabaseHelper.insertInvoice(invoiceId,user_id,roomId,tenantId,date,tongTien);

                if(inserted){
                    int idDien = DatabaseHelper.getServiceIdByName("Điện",user_id);
                    int idNuoc = DatabaseHelper.getServiceIdByName("Nước",user_id);

                    boolean detailDien = DatabaseHelper.insertDetailInvoice(invoiceId, idDien,soDien,tienDien);
                    boolean detailNuoc = DatabaseHelper.insertDetailInvoice(invoiceId,idNuoc,soNuoc,tienNuoc);

                    handler.post(()->{
                        if(detailDien && detailNuoc){
                            Toast.makeText(this, "Lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "Lỗi khi lưu chi tiết hóa đơn!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    handler.post(() -> Toast.makeText(this, "Không thể lưu hóa đơn!", Toast.LENGTH_SHORT).show());
                }
            }catch (Exception e){
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Lỗi xử lý!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}