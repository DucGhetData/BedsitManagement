package com.example.rentinghousemanagement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class SignInActivity extends AppCompatActivity {
    private EditText edt_email,edt_password;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvRegister = findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        edt_email = findViewById(R.id.edt_email_si);
        edt_password = findViewById(R.id.edt_password_si);
        Button button = findViewById(R.id.btnsignin);

        databaseHelper = new DatabaseHelper();
        sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
        checkLoginStatus();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser(){
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString();

        // Kiểm tra xem người dùng đã nhập dữ liệu hay chưa
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tài khoản trong cơ sở dữ liệu bằng Thread
        new Thread(()->{
            try {
                Connection connection = databaseHelper.connect();
                if(connection != null){
                    String query = "SELECT user_id,password FROM Users WHERE email = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1,email);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if(resultSet.next()){
                        // Lấy mk va id tu DB
                        int userId = resultSet.getInt("user_id");
                        String storedPassword = resultSet.getString("password");

                        if(storedPassword.equals(password)){
                            // Lưu thông tin đăng nhập vào SharedPreferences
                            saveLoginInfo(userId, email);

                            runOnUiThread(()->{
                                Toast.makeText(SignInActivity.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }else {
                            runOnUiThread(()-> Toast.makeText(SignInActivity.this,"Mật khẩu không đúng",Toast.LENGTH_SHORT).show());
                        }
                    }else {
                        runOnUiThread(()-> Toast.makeText(SignInActivity.this,"Tài khoản không tồn tại",Toast.LENGTH_SHORT).show());
                    }

                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                }else {
                    runOnUiThread(() -> Toast.makeText(SignInActivity.this, "Lỗi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show());
                }
            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(()->Toast.makeText(SignInActivity.this,"Lỗi khi đăng nhập!",Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Lưu thông tin đăng nhập vào SharedPreferences
    private void saveLoginInfo(int userId, String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id",userId);
        editor.putString("user_email",email);
        editor.putBoolean("is_logged_in",true);
        editor.apply();
    }

    // Kiểm tra trạng thái đăng nhập
    private void checkLoginStatus(){
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in",false);
        if(isLoggedIn){
            startActivity(new Intent(SignInActivity.this,MainActivity.class));
            finish();
        }
    }
}