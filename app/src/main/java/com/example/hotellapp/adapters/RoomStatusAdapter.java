package com.example.hotellapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hotellapp.R;
import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.models.Room;

import java.util.ArrayList;

public class RoomStatusAdapter extends BaseAdapter {

    private final Activity activity;
    private final ArrayList<Room> roomList;
    private final LayoutInflater inflater;

    public RoomStatusAdapter(Activity activity, ArrayList<Room> roomList) {
        this.activity = activity;
        this.roomList = roomList;
        this.inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return roomList.get(position).getRoomId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(
                    R.layout.item_room_status,
                    parent,
                    false
            );
        }

        LinearLayout layoutRoomCard =
                view.findViewById(R.id.layoutRoomCard);

        TextView txtRoomNumber =
                view.findViewById(R.id.txtRoomNumber);

        TextView txtRoomType =
                view.findViewById(R.id.txtRoomType);

        TextView txtRoomStatus =
                view.findViewById(R.id.txtRoomStatus);

        Room room = roomList.get(position);

        txtRoomNumber.setText(room.getRoomNumber());
        txtRoomType.setText(room.getTypeName());
        txtRoomStatus.setText(room.getRoomStatus());

        String status = room.getRoomStatus();

        if (status.equals(DatabaseContract.RoomsTable.STATUS_AVAILABLE)) {

            layoutRoomCard.setBackgroundResource(
                    R.drawable.bg_room_available
            );

            txtRoomStatus.setTextColor(0xFF15803D);

        } else if (status.equals(
                DatabaseContract.RoomsTable.STATUS_OCCUPIED)) {

            layoutRoomCard.setBackgroundResource(
                    R.drawable.bg_room_occupied
            );

            txtRoomStatus.setTextColor(0xFFDC2626);

        } else if (status.equals(
                DatabaseContract.RoomsTable.STATUS_CLEANING)) {

            layoutRoomCard.setBackgroundResource(
                    R.drawable.bg_room_cleaning
            );

            txtRoomStatus.setTextColor(0xFFCA8A04);

        } else {

            layoutRoomCard.setBackgroundResource(
                    R.drawable.bg_room_maintenance
            );

            txtRoomStatus.setTextColor(0xFF475569);
        }

        return view;
    }
}