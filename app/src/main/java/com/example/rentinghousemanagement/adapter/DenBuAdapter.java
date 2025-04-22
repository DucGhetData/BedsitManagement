package com.example.rentinghousemanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.DenBu;


import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DenBuAdapter extends RecyclerView.Adapter<DenBuAdapter.DenBuViewHolder> {
    private List<DenBu> compensations;
    private OnCompensationClickListener onCompensationClickListener;

    public DenBuAdapter(List<DenBu> compensations, OnCompensationClickListener onCompensationClickListener) {
        this.compensations = compensations;
        this.onCompensationClickListener = onCompensationClickListener;
    }

    @NonNull
    @Override
    public DenBuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compensation, parent, false);
        return new DenBuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DenBuViewHolder holder, int position) {
        DenBu denBu = compensations.get(position);
        holder.tvCompenId.setText(String.valueOf(denBu.getCompensatioId()));
        holder.tvMonthYear.setText("Ngày tạo:" + formatDate(denBu.getCreateDate()));
        holder.tvRoomId.setText("Phòng: " + denBu.getRoomId());
        holder.tvTenantName.setText("Người thuê: " + denBu.getTenantName());
        holder.tvTotalAmount.setText(formatMoney(denBu.getTotal_amount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCompensationClickListener.onCompensationClick(denBu);
            }
        });
    }

    @Override
    public int getItemCount() {
        return compensations.size();
    }

    public interface OnCompensationClickListener{
        void onCompensationClick(DenBu compensation);
    }

    public class DenBuViewHolder extends RecyclerView.ViewHolder{
        TextView tvCompenId;
        TextView tvMonthYear;
        TextView tvRoomId;
        TextView tvTenantName;
        TextView tvTotalAmount;
        ImageView iv_detail;

        public DenBuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompenId = itemView.findViewById(R.id.tv_compensation_id);
            tvMonthYear = itemView.findViewById(R.id.tv_month_year);
            tvRoomId = itemView.findViewById(R.id.tv_room_id);
            tvTenantName = itemView.findViewById(R.id.tv_tenant_name);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            iv_detail = itemView.findViewById(R.id.iv_arrow);

        }
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
