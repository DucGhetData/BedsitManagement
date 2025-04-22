package com.example.rentinghousemanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.activity.DetailCompensation;
import com.example.rentinghousemanagement.adapter.DenBuAdapter;
import com.example.rentinghousemanagement.model.DenBu;
import com.example.rentinghousemanagement.model.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompensationFragment extends Fragment implements DenBuAdapter.OnCompensationClickListener {
    private RecyclerView recyclerView;
    private DenBuAdapter denBuAdapter;
    private List<DenBu> compensations;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public CompensationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CompensationFragment newInstance(String param1, String param2) {
        CompensationFragment fragment = new CompensationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compensation,container,false);
        recyclerView = view.findViewById(R.id.rv_compensations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadCompensationData();
        return view;
    }

    private void loadCompensationData() {
        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }
        executorService.execute(()->{
            compensations = getCompensationList(userId);
            if(compensations!=null){
                requireActivity().runOnUiThread(()->{
                    denBuAdapter = new DenBuAdapter(compensations,this);
                    recyclerView.setAdapter(denBuAdapter);
                });
            }

        });
    }

    public static List<DenBu> getCompensationList(int userId){
        List<DenBu> compensations = new ArrayList<>();
        Connection conn = DatabaseHelper.connect();
        if(conn != null){
            String sql = "SELECT \n" +
                    "    Compensation.compensation_id,\n" +
                    "    Compensation.room_id,\n" +
                    "    Compensation.create_date,\n" +
                    "    Tenants.tenant_id,\n" +
                    "    Tenants.tenant_name,\n" +
                    "    Tenants.phone_number,\n" +
                    "    Compensation.total_amount\n" +
                    "FROM Compensation INNER JOIN DetailCompensation\n" +
                    "    ON Compensation.compensation_id = DetailCompensation.compensation_id\n" +
                    "INNER JOIN Contracts\n" +
                    "    ON Compensation.room_id = Contracts.contract_id\n" +
                    "INNER JOIN Tenants\n" +
                    "    ON Contracts.tenant_id = Tenants.tenant_id\n" +
                    "WHERE Contracts.user_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()){
                    int compensationId = rs.getInt("compensation_id");
                    String month = rs.getString("create_date");
                    int roomId = rs.getInt("room_id");
                    int tenantId = rs.getInt("tenant_id");
                    String tenant_name = rs.getString("tenant_name");
                    String phone = rs.getString("phone_number");
                    double total = rs.getDouble("total_amount");

                    DenBu denBu = new DenBu(compensationId,roomId,month,tenantId,tenant_name,phone,total);
                    compensations.add(denBu);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try{
                    conn.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return compensations;
    }

    @Override
    public void onCompensationClick(DenBu compensation) {
        Intent intent = new Intent(getContext(), DetailCompensation.class);
        intent.putExtra("Compensation",compensation);
        startActivity(intent);
    }
}