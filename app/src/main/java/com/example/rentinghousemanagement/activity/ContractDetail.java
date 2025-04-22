package com.example.rentinghousemanagement.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.ChiTietHopDong;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContractDetail extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int userId;
    private TextView tv_contract_id, tv_contract_status, tv_room_id, tv_room_price, tv_room_square
                    ,tv_tenant_name, tv_tenant_id_number, tv_start_date, tv_end_date, tv_deposit_amount
                    ,tv_cancelation_date, tv_cancelation_party, tv_compen_amount;
    private CardView card_cancelation;
    private Button btn_terminate_contract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contract_detail);

        // Lấy contractId từ Intent
        int contractId = getIntent().getIntExtra("contractId",0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", 0);

        initView(contractId,userId);

    }

    private void initView(int contractId, int userId ){
        // Ánh xạ các view
        tv_contract_id = findViewById(R.id.tv_contract_id);
        tv_contract_status = findViewById(R.id.tv_contract_status);
        tv_room_id = findViewById(R.id.tv_room_id);
        tv_room_price = findViewById(R.id.tv_room_price);
        tv_room_square = findViewById(R.id.tv_room_square);
        tv_tenant_name = findViewById(R.id.tv_tenant_name);
        tv_tenant_id_number = findViewById(R.id.tv_tenant_id_number);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        tv_deposit_amount = findViewById(R.id.tv_deposit_amount);
        card_cancelation = findViewById(R.id.card_cancelation);
        tv_cancelation_date = findViewById(R.id.tv_cancelation_date);
        tv_cancelation_party = findViewById(R.id.tv_cancelation_party);
        tv_compen_amount = findViewById(R.id.tv_compen_amount);
        btn_terminate_contract = findViewById(R.id.btn_terminate_contract);

        // Goi truy van
        executor.execute(()->{
            ChiTietHopDong details = DatabaseHelper.getContractDetails(contractId, userId);

            if(details!=null){
                // Gán dữ liệu hợp đồng
                tv_contract_id.setText(String.valueOf(details.getContractId()));
                tv_tenant_name.setText(details.getTenantName());
                tv_tenant_id_number.setText(details.getIdNumber());
                tv_room_id.setText(String.valueOf(details.getRoomId()));
                tv_room_price.setText(formatMoney(details.getPrice()));
                tv_room_square.setText(formatArea(details.getSquare()));
                tv_start_date.setText(formatDate(details.getStartDate()));
                tv_end_date.setText(formatDate(details.getEndDate()));
                tv_deposit_amount.setText(formatMoney(details.getDepositAmount()));
                tv_contract_status.setText(details.getStatus() == 1 ? "Đang hiệu lực" : "Đã hủy");

                if (details.getStatus()==1){
                    card_cancelation.setVisibility(View.GONE);
                }
                else {
                    card_cancelation.setVisibility(View.VISIBLE);
                    // Gán thông tin hủy nếu có
                    if (details.getCancelDate() != null) {
                        tv_cancelation_date.setText(formatDate(details.getCancelDate()));
                    }
                    tv_cancelation_party.setText(details.getCancelParty() == 1 ? "Chủ nhà" : "Bên thuê");
                    tv_compen_amount.setText(formatMoney(details.getCompenAmount()));
                }
            }
        });

        btn_terminate_contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    private String formatArea(double square) {
        return square + " m²";
    }

}