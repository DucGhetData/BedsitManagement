package com.example.rentinghousemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.HoaDon;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HoaDonAdapter extends RecyclerView.Adapter<HoaDonAdapter.HoaDonViewHolder> {
    private List<HoaDon> invoices;
    private OnInvoiceClickListener onInvoiceClickListener;

    public HoaDonAdapter(List<HoaDon> invoices, OnInvoiceClickListener onInvoiceClickListener) {
        this.invoices = invoices;
        this.onInvoiceClickListener = onInvoiceClickListener;
    }

    // Interface để xử lý sự kiện click
    public interface OnInvoiceClickListener {
        void onInvoiceClick(HoaDon invoice);
    }

    @NonNull
    @Override
    public HoaDonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new HoaDonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoaDonViewHolder holder, int position) {
        HoaDon invoice = invoices.get(position);

        holder.tvInvoiceId.setText(invoice.getInvoiceId());
        holder.tvMonthYear.setText("Ngày tạo:" + formatDate(invoice.getMonthYear()));
        holder.tvRoomId.setText("Phòng: " + invoice.getRoomId());
        holder.tvTenantName.setText("Người thuê: " + invoice.getTenantName());
        holder.tvRoomPrice.setText(formatMoney(invoice.getRoomPrice()));
        holder.tvTotalAmount.setText(formatMoney(invoice.getTotalAmount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInvoiceClickListener.onInvoiceClick(invoice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class HoaDonViewHolder extends RecyclerView.ViewHolder{
        TextView tvInvoiceId;
        TextView tvMonthYear;
        TextView tvRoomId;
        TextView tvTenantName;
        TextView tvRoomPrice;
        TextView tvTotalAmount;
        ImageView iv_detail;

        public HoaDonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tv_invoice_id);
            tvMonthYear = itemView.findViewById(R.id.tv_month_year);
            tvRoomId = itemView.findViewById(R.id.tv_room_id);
            tvTenantName = itemView.findViewById(R.id.tv_tenant_name);
            tvRoomPrice = itemView.findViewById(R.id.tv_room_price);
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
