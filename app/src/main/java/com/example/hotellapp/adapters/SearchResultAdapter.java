package com.example.hotellapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.models.Room;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {
    public interface OnSearchResultClickListener {
        void onRoomClick(Room room);
    }

    private final LayoutInflater inflater;
    private final List<Room> rooms;
    private final NumberFormat currencyFormatter;
    private final OnSearchResultClickListener onSearchResultClickListener;

    public SearchResultAdapter(Context context, List<Room> rooms, OnSearchResultClickListener onSearchResultClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.rooms = rooms;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.onSearchResultClickListener = onSearchResultClickListener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_result_card, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Room room = rooms.get(position);
        String bedType = room.getBedType() == null || room.getBedType().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.room_bed_type_unknown)
                : room.getBedType();

        holder.roomName.setText("Phòng " + room.getRoomNumber() + " - " + room.getTypeName());
        
        holder.roomFeatures.setText(holder.itemView.getContext().getString(
                R.string.room_default_features,
                formatSize(room.getSizeSqm()),
                bedType,
                room.getCapacity()
        ));
        
        // Hiển thị trạng thái phòng thay vì số lượng tổng
        String statusText = "Available".equalsIgnoreCase(room.getRoomStatus()) ? "Còn trống" : "Đã đặt";
        holder.roomAvailability.setText(statusText);
        
        holder.roomPrice.setText(currencyFormatter.format(room.getPricePerNight()));
        holder.itemView.setOnClickListener(v -> onSearchResultClickListener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private String formatSize(double sizeSqm) {
        if (sizeSqm == Math.rint(sizeSqm)) {
            return String.valueOf((int) sizeSqm);
        }
        return String.format(Locale.getDefault(), "%.1f", sizeSqm);
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomName;
        private final TextView roomFeatures;
        private final TextView roomAvailability;
        private final TextView roomPrice;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_result_room_name);
            roomFeatures = itemView.findViewById(R.id.tv_result_room_features);
            roomAvailability = itemView.findViewById(R.id.tv_result_room_availability);
            roomPrice = itemView.findViewById(R.id.tv_result_room_price);
        }
    }
}
