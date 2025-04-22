package com.example.rentinghousemanagement.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.adapter.ServiceInvoiceAdapter;
import com.example.rentinghousemanagement.model.HoaDon;
import com.example.rentinghousemanagement.model.HopDong;
import com.example.rentinghousemanagement.model.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailInvoice extends AppCompatActivity {
    private TextView tvInvoiceId;
    private TextView tvMonthYear;
    private TextView tvRoomId;
    private TextView tvTenantId;
    private TextView tvTenantName;
    private TextView tvPhone;
    private TextView tvRoomPrice;
    private TextView tvTotalAmount;
    private RecyclerView recyclerViewServices;
    private Button btnClose;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<Service> services = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_invoice);

        // Lấy dữ liệu từ Intent
        String invoiceId = getIntent().getStringExtra("INVOICE_ID") != null ?
                getIntent().getStringExtra("INVOICE_ID") : "";
        HoaDon hoaDon = (HoaDon) getIntent().getSerializableExtra("invoice");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);

        // Khởi tạo các view
        initViews();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadInvoiceDetails(invoiceId, hoaDon, userId);

    }

    private void loadInvoiceDetails(String invoiceId, HoaDon hoaDon, int userId) {
        tvInvoiceId.setText(hoaDon.getInvoiceId());
        tvMonthYear.setText(formatDate(hoaDon.getMonthYear()));
        tvRoomId.setText(String.valueOf(hoaDon.getRoomId()));
        tvTenantId.setText(String.valueOf(hoaDon.getTenantId()));
        tvTenantName.setText(hoaDon.getTenantName());
        tvPhone.setText(hoaDon.getPhoneNumber());
        tvRoomPrice.setText(formatMoney(hoaDon.getRoomPrice()));
        tvTotalAmount.setText(formatMoney(hoaDon.getTotalAmount()));

        executorService.execute(()->{
            services = DatabaseHelper.getServiceByInvoice(invoiceId,userId);
            ServiceInvoiceAdapter serviceInvoiceAdapter = new ServiceInvoiceAdapter(services);
            recyclerViewServices.setAdapter(serviceInvoiceAdapter);
        });
    }

    private void initViews() {
        tvInvoiceId = findViewById(R.id.tv_invoice_id);
        tvMonthYear = findViewById(R.id.tv_month_year);
        tvRoomId = findViewById(R.id.tv_room_id);
        tvTenantId = findViewById(R.id.tv_tenant_id);
        tvTenantName = findViewById(R.id.tv_tenant_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvRoomPrice = findViewById(R.id.tv_room_price);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        recyclerViewServices = findViewById(R.id.rv_services);
        btnClose = findViewById(R.id.btn_close);

        // Thiết lập RecyclerView
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
    }


    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VNĐ";
    }
}