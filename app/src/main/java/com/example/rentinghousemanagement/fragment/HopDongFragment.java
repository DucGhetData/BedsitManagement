package com.example.rentinghousemanagement.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.activity.ContractDetail;
import com.example.rentinghousemanagement.adapter.HopDongAdapter;
import com.example.rentinghousemanagement.model.ChiTietHopDong;
import com.example.rentinghousemanagement.model.HopDong;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HopDongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HopDongFragment extends Fragment {
    private RecyclerView recyclerViewHopDong;
    private SwipeRefreshLayout swipeRefreshHopDong;
    private TextView tvEmptyHopDong;
    private ProgressBar progressBarHopDong;

    private DatabaseHelper dbHelper;
    private HopDongAdapter adapter;
    private int userId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public HopDongFragment() {
        // Required empty public constructor
    }
    public static HopDongFragment newInstance(String param1, String param2) {
        HopDongFragment fragment = new HopDongFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getUserIdFromPreferences();
    }

    private int getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_hopdong, container, false);
        // Khởi tạo các view
        recyclerViewHopDong = view.findViewById(R.id.recyclerViewHopDong);
        swipeRefreshHopDong = view.findViewById(R.id.swipeRefreshHopDong);
        tvEmptyHopDong = view.findViewById(R.id.tvEmptyHopDong);
        progressBarHopDong = view.findViewById(R.id.progressBarHopDong);

        // Thiết lập RecyclerView
        recyclerViewHopDong.setLayoutManager(new LinearLayoutManager(getContext()));

        // Thiết lập SwipeRefreshLayout
        swipeRefreshHopDong.setOnRefreshListener(this::loadContracts);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tải dữ liệu hợp đồng khi fragment được tạo
        loadContracts();
    }

    private void loadContracts() {
        if (adapter == null) {
            progressBarHopDong.setVisibility(View.VISIBLE);
        }
        DatabaseHelper.getContractsList(userId, new DatabaseHelper.ContractsCallback() {
            @Override
            public void onContractsLoaded(List<HopDong> contracts) {
                progressBarHopDong.setVisibility(View.GONE);

                // Kiểm tra xem có dữ liệu hay không
                if (contracts == null || contracts.isEmpty()){
                    // Không có dữ liệu, hiển thị thông báo trống
                    recyclerViewHopDong.setVisibility(View.GONE);
                    tvEmptyHopDong.setVisibility(View.VISIBLE);
                }else {
                    // Có dữ liệu, hiển thị RecyclerView
                    recyclerViewHopDong.setVisibility(View.VISIBLE);
                    tvEmptyHopDong.setVisibility(View.GONE);

                    // Cập nhật hoặc tạo mới adapter
                    if (adapter != null) {
                        adapter.updateData(contracts);
                    } else {
                        adapter = new HopDongAdapter(
                                getContext(),
                                contracts,
                                contract -> handleRenewContract(contract),
                                contract -> handleTerminateContract(contract),
                                contract -> handleDetailContract(contract)
                        );
                        recyclerViewHopDong.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onError(String errorMessage) {
                // Ẩn progress bar
                progressBarHopDong.setVisibility(View.GONE);

                // Tắt refresh indicator
                if (swipeRefreshHopDong.isRefreshing()) {
                    swipeRefreshHopDong.setRefreshing(false);
                }

                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleDetailContract(HopDong contract) {
        Intent intent = new Intent(getActivity(), ContractDetail.class);
        intent.putExtra("contractId",contract.getId());
        startActivity(intent);
    }

    private void handleRenewContract(HopDong contract) {
        // mở dialog gia hạn hợp đồng
        Toast.makeText(getContext(), "Gia hạn hợp đồng: " + contract.getId(), Toast.LENGTH_SHORT).show();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_renew_contract, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        Button btnSave = view.findViewById(R.id.btnConfirmRenew);
        EditText etNewEndDate = view.findViewById(R.id.etNewEndDate);
        setupDatePicker(etNewEndDate);

        //
        etNewEndDate.setText(contract.getEndDate());

        btnSave.setOnClickListener(v->{
            String newDate = etNewEndDate.getText().toString().trim();

            executorService.execute(()->{
                DatabaseHelper.updateContractEndDate(contract.getId(),userId,newDate);

                requireActivity().runOnUiThread(()->{
                    Toast.makeText(getContext(), "Gia hạn thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            });
        });
        dialog.show();
        loadContracts();
        // Tại đây bạn có thể mở một dialog để nhập thông tin gia hạn
        // Sau khi gia hạn xong, gọi lại loadContracts() để cập nhật danh sách
    }

    private void handleTerminateContract(HopDong contract) {
        // Hiển thị dialog xác nhận chấm dứt hợp đồng
        Toast.makeText(getContext(), "Chấm dứt hợp đồng: " + contract.getId() + " so tien den bu " + contract.getDepositAmount(), Toast.LENGTH_SHORT).show();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cancelation, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        RadioGroup partyRadioGroup = view.findViewById(R.id.party_radio_group);
        EditText edtCompensation = view.findViewById(R.id.compensation_amount);
        Button btnCancel = view.findViewById(R.id.cancel_button);
        Button btnSubmit = view.findViewById(R.id.submit_button);

        dialog.show();

        // Thiết lập giá trị đền bù nếu kết thúc trước hạn
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date today = new Date();
            Date endDate = sdf.parse(contract.getEndDate());
            long diff = endDate.getTime() - today.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diff);

            if (daysLeft < 30) {
                edtCompensation.setText(String.valueOf(contract.getDepositAmount()));
            } else {
                edtCompensation.setText("0");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnCancel.setOnClickListener(v-> dialog.dismiss());

        btnSubmit.setOnClickListener(v->{
            if (!validateInput(partyRadioGroup, edtCompensation)) return;

            int selectedId = partyRadioGroup.getCheckedRadioButtonId();
            boolean isTenant = selectedId == R.id.tenant_radio;
            double compensation = Double.parseDouble(edtCompensation.getText().toString().trim());
            String createDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            executorService.execute(()->{
                DatabaseHelper.insertCancelation(contract.getId(),createDate,isTenant,compensation);
                DatabaseHelper.updateContractStatusToTerminated(contract.getId());
            });
            Toast.makeText(requireContext(), "Hợp đồng đã được chấm dứt", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        loadContracts();
    }

    private boolean validateInput(RadioGroup radioGroup, EditText edtCompensation) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        String compenText = edtCompensation.getText().toString().trim();

        if (selectedId == -1) {
            Toast.makeText(requireContext(), "Vui lòng chọn bên chấm dứt hợp đồng", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (compenText.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tiền đền bù", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double value = Double.parseDouble(compenText);
            if (value < 0) {
                Toast.makeText(requireContext(), "Tiền đền bù không được âm", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Tiền đền bù không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

}