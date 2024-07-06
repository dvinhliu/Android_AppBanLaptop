package com.example.project_app_laptop.Admin.View;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_app_laptop.Admin.Controller.DBHelper;
import com.example.project_app_laptop.Admin.Model.Order;
import com.example.project_app_laptop.Admin.Model.ProductOrder;
import com.example.project_app_laptop.R;

import java.io.Serializable;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderInformation extends AppCompatActivity {
    TextView txtID, txtUID, txtDate, txtAddress, txtPhone, txtPayment, txtTotal, txtStatus;
    Button btnXacNhan1, btnPO;
    Order od;
    ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        addControls();
        addEvents();
    }

    public void addControls() {
        Intent it = getIntent();
        txtID = findViewById(R.id.txtID);
        txtUID = findViewById(R.id.txtUserID);
        txtDate = findViewById(R.id.txtDate);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtPayment = findViewById(R.id.txtPayment);
        txtTotal = findViewById(R.id.txtTotal);
        txtStatus = findViewById(R.id.txtStatus);
        btnXacNhan1 = findViewById(R.id.btnXacNhan1);
        btnPO = findViewById(R.id.btnPO);
        btnBack = findViewById(R.id.btnBack);
        od = (Order) it.getSerializableExtra("Order");

        if (od != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            try {
                String[] date = od.getNgayMua().split(" ");
                Date dateOfOrder = sdf.parse(date[0] + " " + date[1]);
                Date dateOfNow = sdf.parse(currentDateandTime);

                long millisecondsBetween = Math.abs(dateOfNow.getTime() - dateOfOrder.getTime());
                if ("Chờ xác nhận".equals(od.getTrangThai().trim()) && millisecondsBetween >= 2 * 60 * 1000) {
                    btnXacNhan1.setVisibility(View.VISIBLE);
                } else {
                    btnXacNhan1.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            txtID.setText(String.valueOf(od.getMaHoaDon()));
            txtUID.setText(String.format("Tên khách hàng: %s", od.getTenKhachHang()));
            txtDate.setText(String.format("Ngày mua: %s", od.getNgayMua()));
            txtAddress.setText(String.format("Địa chỉ: %s", od.getDiaChi()));
            txtPhone.setText(String.format("SĐT: %s", od.getSDT()));
            txtPayment.setText(String.format("Hình thức thanh toán: %s", od.getHinhThucThanhToan()));
            DecimalFormat decimalFormat = new DecimalFormat("#,###" + "₫");
            String tong = decimalFormat.format(od.getTongTien());
            txtTotal.setText("Tổng tiền: " + tong);
            txtStatus.setText(String.format("Trạng thái: %s", od.getTrangThai()));
        }
    }

    public void addEvents() {
        btnXacNhan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper db = new DBHelper(getApplicationContext());
                db.comfirmStatus(Integer.parseInt(txtID.getText().toString()));
                btnXacNhan1.setVisibility(View.GONE);
            }
        });
        btnPO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), ViewProductsOrder.class);
                if (od != null)
                    it.putExtra("Order", (Serializable) od);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}