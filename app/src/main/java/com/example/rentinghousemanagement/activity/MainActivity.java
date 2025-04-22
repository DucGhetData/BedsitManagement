package com.example.rentinghousemanagement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.rentinghousemanagement.DatabaseHelper;
import com.example.rentinghousemanagement.fragment.CompensationFragment;
import com.example.rentinghousemanagement.fragment.HoaDonFragment;
import com.example.rentinghousemanagement.fragment.HopDongFragment;
import com.example.rentinghousemanagement.fragment.KhachFragment;
import com.example.rentinghousemanagement.fragment.PhongFragment;
import com.example.rentinghousemanagement.fragment.ProfileFragment;
import com.example.rentinghousemanagement.R;
import com.example.rentinghousemanagement.UserProvider;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserProvider {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Ánh xạ DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigation_view);

        // Lấy View Header của Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_name);
        TextView tvEmail = headerView.findViewById(R.id.tv_email);

        //Gọi hàm loadUserInfo
        loadUserInfo(tvName,tvEmail);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo Toggle Button để mở Navigation Drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Lắng nghe sự kiện click trên NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        // Mặc định hiển thị FragmentPhong khi vào app lần đầu
        PhongFragment phongFragment = PhongFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, phongFragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if(id == R.id.nav_phong){
            PhongFragment phongFragment = PhongFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, phongFragment)
                    .commit();
        } else if (id == R.id.nav_khach) {
            KhachFragment khachFragment = KhachFragment.newInstance("", "");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, khachFragment)
                    .commit();
        } else if (id == R.id.nav_hopdong) {
            HopDongFragment hopDongFragment = HopDongFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,hopDongFragment)
                    .commit();
        } else if (id == R.id.nav_hoadon) {
            HoaDonFragment hoaDonFragment = HoaDonFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,hoaDonFragment)
                    .commit();
        } else if (id == R.id.nav_doanhthu) {
            //selectedFragment = new FragmentDoanhThu();
        } else if (id == R.id.nav_myprofile) {
            ProfileFragment profileFragment = ProfileFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,profileFragment)
                    .commit();
        } else if (id == R.id.nav_compensation) {
            CompensationFragment compensationFragment = CompensationFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,compensationFragment)
                    .commit();
        } else if (id == R.id.nav_dangxuat) {
            logoutUser();
        }

        // Load Fragment được chọn vào FrameLayout
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, selectedFragment)
                    .commit();
        }

        // Đóng Navigation Drawer sau khi chọn item
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Hàm logout tài khoản
    private void logoutUser(){
        // Đảm bảo SharedPreferences không bị null
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Xóa toàn bộ dữ liệu trong SharedPreferences
            editor.apply();

            // Chuyển về màn hình đăng nhập
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.e("LogoutError", "SharedPreferences is null");
        }
    }

    // Hàm lấy userName và Email gán vào Nav
    private void loadUserInfo(TextView tvName, TextView tvEmail){
        sharedPreferences =  getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Log.e("UserInfo", "User ID not found in SharedPreferences");
            return;
        }
        new Thread(()->{
            try {
                Connection connection = databaseHelper.connect();
                if(connection != null){
                    String query = "SELECT user_name, email FROM Users WHERE user_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1,userId);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if(resultSet.next()){
                        String userName = resultSet.getString("user_name");
                        String userEmail = resultSet.getString("email");
                        runOnUiThread(()->{
                            tvName.setText(userName);
                            tvEmail.setText(userEmail);
                        });
                    }
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                runOnUiThread(()->
                        Toast.makeText(MainActivity.this, "Lỗi khi tải thông tin người dùng!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    @Override
    public int getUserId() {
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("user_id",-1);
    }
}