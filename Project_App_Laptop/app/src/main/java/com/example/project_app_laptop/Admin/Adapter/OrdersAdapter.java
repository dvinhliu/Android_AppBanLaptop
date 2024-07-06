package com.example.project_app_laptop.Admin.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.project_app_laptop.Admin.Controller.DBHelper;
import com.example.project_app_laptop.Admin.Model.Order;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import com.example.project_app_laptop.R;
import com.example.project_app_laptop.Admin.View.OrderInformation;

public class OrdersAdapter extends ArrayAdapter<Order> {
    Context context;
    int IDLayout;
    ArrayList<Order> lstOr;
    DBHelper db;

    public OrdersAdapter(@NonNull Context context, int IDLayout, ArrayList<Order> lstOr, DBHelper db) {
        super(context, IDLayout, lstOr);
        this.context = context;
        this.IDLayout = IDLayout;
        this.lstOr = lstOr;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Order order = lstOr.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(IDLayout, null, true);
        }

        TextView txtMaDonHang = convertView.findViewById(R.id.txtMaDonHang);
        txtMaDonHang.setText(String.format("Mã đơn hàng: %s", order.getMaHoaDon()));
        TextView txtNgayMua = convertView.findViewById(R.id.txtNgayMua);
        txtNgayMua.setText(String.format("Ngày mua: %s", order.getNgayMua()));
        TextView txtTrangThai = convertView.findViewById(R.id.txtTrangThai);
        txtTrangThai.setText(String.format("Trạng thái: %s", order.getTrangThai()));
        Button btnXacNhan = convertView.findViewById(R.id.btnXacNhan);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        try {
            String[] date = order.getNgayMua().split(" ");
            Date dateOfOrder = sdf.parse(date[0] + " " + date[1]);
            Date dateOfNow = sdf.parse(currentDateandTime);

            long millisecondsBetween = Math.abs(dateOfNow.getTime() - dateOfOrder.getTime());
            if ("Chờ xác nhận".equals(order.getTrangThai().trim()) && millisecondsBetween >= 2 * 60 * 1000) {
                btnXacNhan.setVisibility(View.VISIBLE);
            } else {
                btnXacNhan.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xác nhận hóa đơn
                db.comfirmStatus(order.getMaHoaDon());
                order.setTrangThai("Đã xác nhận");
                notifyDataSetChanged();
                btnXacNhan.setVisibility(View.GONE);
            }
        });

        Button btnThongTin = convertView.findViewById(R.id.btnThongTin);
        btnThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển tab sang thông tin Order
                Intent it = new Intent(context, OrderInformation.class);
                it.putExtra("Order", (Serializable) order);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });

        return convertView;
    }
}