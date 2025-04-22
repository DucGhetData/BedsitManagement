package com.example.rentinghousemanagement.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignUpActivity extends AppCompatActivity {
    EditText edtemail, edtphone, edtpassword;
    Button btnsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtemail = findViewById(R.id.edt_email);
        edtphone = findViewById(R.id.edt_phone);
        edtpassword = findViewById(R.id.edt_password);
        btnsignup = findViewById(R.id.btnsignup);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount();
            }
        });


        TextView tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
    private void registerAccount(){
        String email = edtemail.getText().toString().trim();
        String phone = edtphone.getText().toString().trim();
        String pass_word = edtpassword.getText().toString().trim();
        String user_name = email;

        if (email.isEmpty() || pass_word.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tạo luồng hoạt động mới
        new Thread(()->{
            Connection conn = DatabaseHelper.connect();
            if(conn != null){
                try{
                    // Kiểm tra tài khoản đã tồn tại chưa
                    String checkQuery = "SELECT COUNT(*) FROM Users WHERE email = ? OR phonenumber = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setString(1, email);
                    checkStmt.setString(2, phone);
                    ResultSet rs = checkStmt.executeQuery();

                    if(rs.next() && rs.getInt(1)>0){
                        runOnUiThread(() -> Toast.makeText(SignUpActivity.this,"Tài khoản đã tồn tại!",Toast.LENGTH_SHORT).show());
                    } else {
                        // Nếu không tồn tại thì tiến hành đăng ký
                        String query = "INSERT INTO Users(user_name, email, phonenumber, password) VALUES (?, ?, ?, ?)";
                        PreparedStatement registerStmt = conn.prepareStatement(query);
                        registerStmt.setString(1, user_name);
                        registerStmt.setString(2, email);
                        registerStmt.setString(3, phone);
                        registerStmt.setString(4, pass_word);
                        int rowsAffected = registerStmt.executeUpdate();
                        if(rowsAffected>0){
                            runOnUiThread(() -> {
                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                // Hiển thị thông báo chuyển màn hình
                                new AlertDialog.Builder(SignUpActivity.this)
                                        .setTitle("Xác nhận")
                                        .setMessage("Chuyển sang màn hình đăng nhập không?")
                                        .setPositiveButton("Ok", (dialog, which) -> {
                                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .setNegativeButton("Tôi sẽ đăng nhập sau", (dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show());
                        }
                    }
                    // Đóng kết nôi
                    conn.close();
                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(()-> Toast.makeText(SignUpActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show());
                }
            }else {
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Không thể kết nối đến cơ sở dữ liệu!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}