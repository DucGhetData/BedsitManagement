package com.example.rentinghousemanagement.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.adapter.KhachAdapter;
import com.example.rentinghousemanagement.model.Khach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KhachFragment extends Fragment {
    private RecyclerView recyclerView;
    private KhachAdapter khachAdapter;
    private List<Khach> khachList = new ArrayList<>();
    private int userid;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentSearchText = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KhachFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static KhachFragment newInstance(String param1, String param2) {
        KhachFragment fragment = new KhachFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Nhập tên khách hàng...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentSearchText = s; // lưu lại query
                if (khachAdapter != null) {
                    khachAdapter.filterList(s);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khach, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewKhach);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userid = sharedPreferences.getInt("user_id", -1);

        loadKhach();

        return view;
    }

    private void loadKhach(){
        executor.execute(()->{
            List<Khach> list = DatabaseHelper.getAllKhach(userid);
            requireActivity().runOnUiThread(()->{
                khachAdapter = new KhachAdapter(getContext(),list);
                recyclerView.setAdapter(khachAdapter);

                // Nếu người dùng đã gõ tìm kiếm trước đó, thì lọc lại
                if (!currentSearchText.isEmpty()) {
                    khachAdapter.filterList(currentSearchText);
                }
            });
        });
    }
}