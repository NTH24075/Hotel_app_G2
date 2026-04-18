package com.example.hotellapp.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onToggleStatus(User user);
    }

    private final List<User> userList = new ArrayList<>();
    private final OnUserActionListener listener;

    public UserAdminAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<User> users) {
        userList.clear();
        if (users != null) {
            userList.addAll(users);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(nullToEmpty(user.fullName));
        holder.tvUserEmail.setText("Email: " + nullToEmpty(user.email));
        holder.tvUserPhone.setText("SĐT: " + nullToEmpty(user.phone));
        holder.tvUserRole.setText("RoleId: " + user.roleId);
        holder.tvUserStatus.setText("Trạng thái: " + nullToEmpty(user.status));

        boolean isActive = "Active".equalsIgnoreCase(nullToEmpty(user.status));
        holder.btnToggleStatus.setText(isActive ? "Khóa" : "Mở khóa");

        holder.btnToggleStatus.setOnClickListener(v -> {
            if (listener != null) listener.onToggleStatus(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserPhone, tvUserRole, tvUserStatus;
        Button btnToggleStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}