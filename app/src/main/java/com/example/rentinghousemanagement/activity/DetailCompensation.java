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
import com.example.rentinghousemanagement.model.DenBu;
import com.example.rentinghousemanagement.model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailCompensation extends AppCompatActivity {
    private TextView tv_compensation_id, tv_month_year, tv_room_id, tv_tenant_id, tv_tenant_name,
            tv_phone, tv_total_amount;
    private Button btn_close;
    private RecyclerView recyclerViewServices;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<Service> services = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_compensation);
        DenBu compensation = (DenBu) getIntent().getSerializableExtra("Compensation");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);

        // Khởi tạo các view
        initViews();
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadCompensationDetail(compensation.getCompensatioId(),compensation,userId);

    }

    private void loadCompensationDetail(int compensationId, DenBu denBu, int userId) {
        tv_compensation_id.setText(String.valueOf(compensationId));
        tv_month_year.setText(formatDate(denBu.getCreateDate()));
        tv_room_id.setText(String.valueOf(denBu.getRoomId()));
        tv_tenant_id.setText(String.valueOf(denBu.getTenantId()));
        tv_tenant_name.setText(denBu.getTenantName());
        tv_phone.setText(denBu.getPhone_number());
        tv_total_amount.setText(formatMoney(denBu.getTotal_amount()));

        executorService.execute(()->{
            services = getServiceByCompensation(denBu.getCompensatioId(),userId);
            ServiceInvoiceAdapter serviceInvoiceAdapter = new ServiceInvoiceAdapter(services);
            recyclerViewServices.setAdapter(serviceInvoiceAdapter);
        });
    }


    private void initViews() {
        tv_compensation_id = findViewById(R.id.tv_invoice_id);
        tv_month_year = findViewById(R.id.tv_month_year);
        tv_room_id = findViewById(R.id.tv_room_id);
        tv_tenant_id = findViewById(R.id.tv_tenant_id);
        tv_tenant_name = findViewById(R.id.tv_tenant_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        recyclerViewServices = findViewById(R.id.rv_services);
        btn_close = findViewById(R.id.btn_close);

        // Thiết lập RecyclerView
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Service> getServiceByCompensation(int compensationId, int userId){
        List<Service> services = new ArrayList<>();
        Connection conn = DatabaseHelper.connect();
        if(conn != null){
            String sql = "SELECT\n" +
                    "    DetailCompensation.service_id,\n" +
                    "    Services.service_name,\n" +
                    "    DetailCompensation.number,\n" +
                    "    Services.price,\n" +
                    "    DetailCompensation.total_price\n" +
                    "FROM Compensation INNER JOIN DetailCompensation\n" +
                    "    ON Compensation.compensation_id = DetailCompensation.compensation_id\n" +
                    "INNER JOIN Services\n" +
                    "    ON DetailCompensation.service_id = Services.service_id\n" +
                    "WHERE Compensation.compensation_id = ?";

            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,compensationId);
                ResultSet rs = stmt.executeQuery();

                while(rs.next()){
                    int service_id = rs.getInt("service_id");
                    String service_name = rs.getString("service_name");
                    double price = rs.getDouble("price");
                    int number = rs.getInt("number");
                    double total = rs.getDouble("total_price");

                    Service service = new Service(service_id,userId,service_name,price);
                    service.setQuantity(number);
                    service.setTotal_price(total);
                    services.add(service);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return services;
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