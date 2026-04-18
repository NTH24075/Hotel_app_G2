package com.example.hotellapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.models.Room;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    private final LayoutInflater layoutInflater;
    private final List<Room> rooms;
    private final NumberFormat currencyFormatter;
    private final OnRoomClickListener onRoomClickListener;

    public RoomAdapter(Context context, List<Room> rooms, OnRoomClickListener onRoomClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.rooms = rooms;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.onRoomClickListener = onRoomClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        
        // Hiển thị: "Phòng [Số phòng] - [Hạng phòng]"
        holder.roomName.setText("Phòng " + room.getRoomNumber() + " - " + room.getTypeName());
        
        holder.roomFeatures.setText(buildFeatures(holder.itemView.getContext(), room));
        holder.roomPrice.setText(currencyFormatter.format(room.getPricePerNight()));
        holder.roomImage.setContentDescription(room.getTypeName());
        
        // Kiểm tra trạng thái phòng thực tế
        boolean isAvailable = "Available".equalsIgnoreCase(room.getRoomStatus());
        holder.bookButton.setEnabled(isAvailable);
        holder.bookButton.setAlpha(isAvailable ? 1f : 0.6f);
        holder.bookButton.setText(isAvailable ? "Đặt" : "Hết");

        holder.itemView.setOnClickListener(v -> onRoomClickListener.onRoomClick(room));
        holder.bookButton.setOnClickListener(v -> onRoomClickListener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private String buildFeatures(Context context, Room room) {
        // Thêm thông tin Tầng vào mô tả
        String bedType = room.getBedType();
        if (bedType == null || bedType.trim().isEmpty()) {
            bedType = "Giường tiêu chuẩn";
        }
        
        return String.format(Locale.getDefault(), "Tầng %d · %s · %d khách", 
                room.getFloorNumber(), bedType, (int)room.getSizeSqm());
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView roomImage;
        private final TextView roomName;
        private final TextView roomFeatures;
        private final TextView roomPrice;
        private final Button bookButton;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.room_image);
            roomName = itemView.findViewById(R.id.room_name);
            roomFeatures = itemView.findViewById(R.id.room_features);
            roomPrice = itemView.findViewById(R.id.room_price);
            bookButton = itemView.findViewById(R.id.book_room_button);
        }
    }
}
