package com.example.rentinghousemanagement.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.adapter.FacilitiesAdapter;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Compensation extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tv_room_info, tv_total_amount;
    private EditText et_date;
    private Button saveButton;
    private FacilitiesAdapter adapter;
    private List<Service> serviceList = new ArrayList<>();
    private int roomId;
    private double total;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final android.os.Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compensation);

        recyclerView = findViewById(R.id.rv_facilities);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        tv_room_info = findViewById(R.id.tv_room_info);
        et_date = findViewById(R.id.et_date);
        saveButton = findViewById(R.id.btn_save);

        roomId = getIntent().getIntExtra("room_id",-1);

        loadCompentionInfo();
        loadFacilities(roomId);

        saveButton.setOnClickListener(v->{
            String create_date = convertToSqlDateFormat(et_date.getText().toString());
            saveCompensation(roomId,create_date,serviceList);
            finish();
        });
    }
    private void loadCompentionInfo(){
        tv_room_info.setText("Phòng số: "+ roomId);
        // Lấy ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        et_date.setText(currentDate);
        setupDatePicker(et_date);
    }

    private void loadFacilities(int roomId){
        executorService.execute(()->{
            serviceList = DatabaseHelper.getServiceByRoomId(roomId);
            adapter = new FacilitiesAdapter(serviceList,this::updateTotalAmount);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            updateTotalAmount();
        });
    }
    private void updateTotalAmount() {
        total = 0;
        for(Service service : serviceList){
            total += service.getQuantity() * service.getPrice();
        }
        tv_total_amount.setText(String.format(Locale.getDefault(),"%,.0f VNĐ",total));
    }

    public static String convertToSqlDateFormat(String date_ddMMyyyy) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date date = inputFormat.parse(date_ddMMyyyy);
            return sqlFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    private void saveCompensation(int roomId, String create_date, List<Service> services){
        executorService.execute(()->{
            Connection connection = null;
            PreparedStatement stmtComp = null;
            PreparedStatement stmtDetail = null;
            ResultSet generatedKeys = null;

            try {
                connection = DatabaseHelper.connect();
                connection.setAutoCommit(false);
                // XÓA các dịch vụ có số lượng bằng 0 để không lưu dữ liệu thừa
                services.removeIf(service -> service.getQuantity() == 0);

                if(services.isEmpty()){
                    mainHandler.post(() ->
                            Toast.makeText(this, "Không có dịch vụ nào được chọn để lưu.", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                double totalAmount = 0;
                for (Service s : services) {
                    totalAmount += s.getQuantity() * s.getPrice();
                }

                // Lưu Compensation
                String insertcomp = "INSERT INTO Compensation(room_id, create_date, total_amount) VALUES (?, ?, ?)";
                stmtComp = connection.prepareStatement(insertcomp, Statement.RETURN_GENERATED_KEYS);
                stmtComp.setInt(1,roomId);
                stmtComp.setString(2,create_date);
                stmtComp.setDouble(3,totalAmount);
                stmtComp.executeUpdate();

                // Lấy compensation_id vừa tạo
                generatedKeys = stmtComp.getGeneratedKeys();
                int compensationId = -1;
                if (generatedKeys.next()) {
                    compensationId = generatedKeys.getInt(1);
                }

                String inserDetail = "INSERT INTO DetailCompensation(compensation_id, service_id, number, total_price) " +
                        "VALUES (?, ?, ?, ?)";

                stmtDetail = connection.prepareStatement(inserDetail);

                for(Service service : services ){
                    stmtDetail.setInt(1,compensationId);
                    stmtDetail.setInt(2,service.getServiceId());
                    stmtDetail.setInt(3,service.getQuantity());
                    stmtDetail.setDouble(4,service.getQuantity()*service.getPrice());
                    stmtDetail.addBatch();
                }

                stmtDetail.executeBatch();
                connection.commit();

                runOnUiThread(()->{
                    Toast.makeText(this, "Lưu biên bản thành công",Toast.LENGTH_SHORT).show();
                });

            }catch (SQLException e){
                e.printStackTrace();
                // Nếu có lỗi thì rollback
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }finally {
                // Đóng các tài nguyên
                try {
                    if (generatedKeys != null) generatedKeys.close();
                    if (stmtDetail != null) stmtDetail.close();
                    if (stmtComp != null) stmtComp.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}