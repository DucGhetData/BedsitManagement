package com.example.rentinghousemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.model.HopDong;
import com.example.rentinghousemanagement.model.TinhTrangHopDong;

import java.util.List;

public class HopDongAdapter extends RecyclerView.Adapter<HopDongAdapter.ContractViewHolder> {
    private Context context;
    private List<HopDong> contracts;
    private OnContractActionListener renewListener;
    private OnContractActionListener terminateListener;
    private OnContractActionListener detailListener;

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hopdong, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        HopDong contract = contracts.get(position);
        holder.bind(contract);

    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }

    public void updateData(List<HopDong> newContracts) {
        this.contracts = newContracts;
        notifyDataSetChanged();
    }

    public interface OnContractActionListener {
        void onAction(HopDong contract);
    }

    public HopDongAdapter(Context context, List<HopDong> contracts,
                          OnContractActionListener renewListener,
                          OnContractActionListener terminateListener, OnContractActionListener detailListener) {
        this.context = context;
        this.contracts = contracts;
        this.renewListener = renewListener;
        this.terminateListener = terminateListener;
        this.detailListener = detailListener;
    }

    class ContractViewHolder extends RecyclerView.ViewHolder{
        private TextView tvContractId;
        private TextView tvCustomerName;
        private TextView tvDateRange;
        private TextView tvStatus;
        private ImageView ivOptions;

        public ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContractId = itemView.findViewById(R.id.tv_contract_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivOptions = itemView.findViewById(R.id.iv_options);
        }

        public void bind(final HopDong contract){
            // Hiển thị mã hợp đồng
            tvContractId.setText(String.valueOf(contract.getId()));
            // Hiển thị tên khách hàng
            tvCustomerName.setText(contract.getCustomerName());
            // Hiển thị thời hạn hợp đồng
            tvDateRange.setText(contract.getStartDate() + " - " + contract.getEndDate());

            // Hiển thị trạng thái hợp đồng và thiết lập màu nền tương ứng
            switch (contract.getStatus()) {
                case ACTIVE:
                    tvStatus.setText("Còn hiệu lực");
                    tvStatus.setBackgroundResource(R.drawable.status_bg);
                    // Không cần thay đổi màu nền vì mặc định đã là màu xanh lá
                    break;
                case EXPIRED:
                    tvStatus.setText("Hết hạn");
                    tvStatus.setBackgroundResource(R.drawable.status_bg);
                    tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.expired_status));
                    break;
                case PENDING:
                    tvStatus.setText("Sắp hết hạn");
                    tvStatus.setBackgroundResource(R.drawable.status_bg);
                    tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.pending_status));
                    break;
            }
            // Xử lý sự kiện click vào nút tùy chọn
            ivOptions.setOnClickListener(v -> showOptionsMenu(v, contract));
        }

        private void showOptionsMenu(View view, HopDong contract){
            PopupMenu popupMenu = new PopupMenu(context,view);
            popupMenu.inflate(R.menu.contract_options_menu);

            // Tùy chỉnh menu dựa trên trạng thái hợp đồng
            if (contract.getStatus() == TinhTrangHopDong.EXPIRED) {
                // Nếu hợp đồng đã hết hạn, không cho phép chấm dứt
                popupMenu.getMenu().findItem(R.id.action_terminate).setVisible(false);
            } else {
                // Nếu hợp đồng đang hiệu lực hoặc sắp hết hạn
                popupMenu.getMenu().findItem(R.id.action_terminate).setVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item->{
                int id = item.getItemId();
                if (id == R.id.action_renew) {
                    if (renewListener != null) {
                        renewListener.onAction(contract);
                    }
                    return true;
                } else if (id == R.id.action_terminate) {
                    if (terminateListener != null) {
                        terminateListener.onAction(contract);
                    }
                    return true;
                } else if (id == R.id.action_details) {
                    // Xử lý xem chi tiết hợp đồng (có thể thêm listener mới nếu cần)
                    detailListener.onAction(contract);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        }
    }
}
