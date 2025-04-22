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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.activity.DetailInvoice;
import com.example.rentinghousemanagement.adapter.HoaDonAdapter;
import com.example.rentinghousemanagement.model.HoaDon;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoaDonFragment extends Fragment implements HoaDonAdapter.OnInvoiceClickListener {
    private RecyclerView recyclerView;
    private HoaDonAdapter hoaDonAdapter;
    private List<HoaDon> invoices;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int userId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public HoaDonFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static HoaDonFragment newInstance(String param1, String param2) {
        HoaDonFragment fragment = new HoaDonFragment();
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
       View view = inflater.inflate(R.layout.fragment_hoa_don,container,false);
       recyclerView = view.findViewById(R.id.rv_invoices);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       loadHoaDonData();
       return view;
    }

    private void loadHoaDonData(){
        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }
        executorService.execute(()->{
            invoices = DatabaseHelper.getInvoiceList(userId);
            if(invoices != null){
                requireActivity().runOnUiThread(()->{
                    hoaDonAdapter = new HoaDonAdapter(invoices,this);
                    recyclerView.setAdapter(hoaDonAdapter);
                });
            }
        });

    }

    @Override
    public void onInvoiceClick(HoaDon invoice) {
        // Xử lý sự kiện click vào hóa đơn
        Intent intent = new Intent(getContext(), DetailInvoice.class);
        intent.putExtra("INVOICE_ID", invoice.getInvoiceId());
        intent.putExtra("invoice", invoice);
        startActivity(intent);
    }
}