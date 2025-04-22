package com.example.rentinghousemanagement.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private EditText etUserName, etEmail, etPhone, etPassword;
    private Button btnUpdate;
    private int userId;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Ánh xạ các nút
        etUserName = view.findViewById(R.id.etUserName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        loadUserInfo();

        btnUpdate.setOnClickListener(v-> {
            updateUserInfo();
        });

        return view;
    }

    private void loadUserInfo() {
        executor.execute(() -> {
            User user = DatabaseHelper.getUserById(userId);
            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    etUserName.setText(user.getUserName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhoneNumber());
                    etPassword.setText(user.getPassword());
                }
            });
        });
    }

    private void updateUserInfo() {
        String name = etUserName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean result = DatabaseHelper.updateUser(userId, name, phone, pass);
            requireActivity().runOnUiThread(() -> {
                if (result) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}