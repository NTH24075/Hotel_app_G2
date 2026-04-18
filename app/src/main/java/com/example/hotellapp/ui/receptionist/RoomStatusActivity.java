package com.example.hotellapp.ui.receptionist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.RoomStatusAdapter;
import com.example.hotellapp.dao.RoomDAO;
import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.models.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomStatusActivity extends AppCompatActivity {

    private TextView txtAvailableCount;
    private TextView txtOccupiedCount;
    private TextView txtCleaningCount;
    private TextView txtMaintenanceCount;

    private Spinner spRoomStatus;
    private Button btnFilterRoom;
    private Button btnRefreshRoom;

    private GridView gvRoomStatus;

    private RoomDAO roomDAO;
    private List<Room> roomList;
    private RoomStatusAdapter adapter;
    private Button btnBackRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_status);

        mapping();
        initData();
        addEvents();
    }

    private void mapping() {
        txtAvailableCount = findViewById(R.id.txtAvailableCount);
        txtOccupiedCount = findViewById(R.id.txtOccupiedCount);
        txtCleaningCount = findViewById(R.id.txtCleaningCount);
        txtMaintenanceCount = findViewById(R.id.txtMaintenanceCount);

        spRoomStatus = findViewById(R.id.spRoomStatus);
        btnFilterRoom = findViewById(R.id.btnFilterRoom);
        btnRefreshRoom = findViewById(R.id.btnRefreshRoom);

        gvRoomStatus = findViewById(R.id.gvRoomStatus);
        btnBackRoom = findViewById(R.id.btnBackRoom);
    }

    private void initData() {
        roomDAO = new RoomDAO(this);

        loadSpinner();
        loadStatistics();
        loadAllRooms();
    }

    private void loadSpinner() {
        ArrayList<String> statusList = new ArrayList<>();

        statusList.add("Tất cả");
        statusList.add(DatabaseContract.RoomsTable.STATUS_AVAILABLE);
        statusList.add(DatabaseContract.RoomsTable.STATUS_OCCUPIED);
        statusList.add(DatabaseContract.RoomsTable.STATUS_CLEANING);
        statusList.add(DatabaseContract.RoomsTable.STATUS_MAINTENANCE);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusList
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spRoomStatus.setAdapter(spinnerAdapter);
    }

    private void loadStatistics() {
        txtAvailableCount.setText(String.valueOf(
                roomDAO.countRoomsByStatus(
                        DatabaseContract.RoomsTable.STATUS_AVAILABLE)));

        txtOccupiedCount.setText(String.valueOf(
                roomDAO.countRoomsByStatus(
                        DatabaseContract.RoomsTable.STATUS_OCCUPIED)));

        txtCleaningCount.setText(String.valueOf(
                roomDAO.countRoomsByStatus(
                        DatabaseContract.RoomsTable.STATUS_CLEANING)));

        txtMaintenanceCount.setText(String.valueOf(
                roomDAO.countRoomsByStatus(
                        DatabaseContract.RoomsTable.STATUS_MAINTENANCE)));
    }

    private void loadAllRooms() {
        roomList = roomDAO.getAllRooms();

        adapter = new RoomStatusAdapter(
                RoomStatusActivity.this,
                (ArrayList<Room>) roomList
        );

        gvRoomStatus.setAdapter(adapter);
    }

    private void addEvents() {

        btnFilterRoom.setOnClickListener(v -> {

            String selectedStatus =
                    spRoomStatus.getSelectedItem().toString();

            if (selectedStatus.equals("Tất cả")) {
                loadAllRooms();
            } else {
                roomList = roomDAO.getRoomsByStatus(selectedStatus);

                adapter = new RoomStatusAdapter(
                        RoomStatusActivity.this,
                        (ArrayList<Room>) roomList
                );

                gvRoomStatus.setAdapter(adapter);
            }

            Toast.makeText(
                    this,
                    "Đã lọc trạng thái phòng",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnRefreshRoom.setOnClickListener(v -> {
            spRoomStatus.setSelection(0);
            loadStatistics();
            loadAllRooms();

            Toast.makeText(
                    this,
                    "Đã làm mới danh sách phòng",
                    Toast.LENGTH_SHORT
            ).show();
        });

        gvRoomStatus.setOnItemClickListener((parent, view, position, id) -> {

            Room room = roomList.get(position);

            showRoomDialog(room);
        });
        btnBackRoom.setOnClickListener(v -> {
            finish();
        });
    }

    private void showRoomDialog(Room room) {

        String info =
                "Số phòng: " + room.getRoomNumber() +
                        "\nLoại phòng: " + room.getTypeName() +
                        "\nTầng: " + room.getFloorNumber() +
                        "\nTrạng thái: " + room.getRoomStatus() +
                        "\nGiường: " + room.getBedType() +
                        "\nSức chứa: " + room.getCapacity() +
                        "\nDiện tích: " + room.getSizeSqm() + " m²";

        new AlertDialog.Builder(this)
                .setTitle("Thông tin phòng")
                .setMessage(info)
                .setPositiveButton("Đóng", null)
                .show();
    }
}