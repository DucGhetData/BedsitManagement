package com.example.rentinghousemanagement;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rentinghousemanagement.model.ChiTietHopDong;
import com.example.rentinghousemanagement.model.HoaDon;
import com.example.rentinghousemanagement.model.HopDong;
import com.example.rentinghousemanagement.model.Khach;
import com.example.rentinghousemanagement.model.Phong;
import com.example.rentinghousemanagement.model.Service;
import com.example.rentinghousemanagement.model.TinhTrangHopDong;
import com.example.rentinghousemanagement.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseHelper {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String TAG = "DatabaseHelper";
    private static final String IP = "127.0.0.0"; // Địa chỉ SQL Server
    private static final String PORT = "433";
    private static final String DATABASE = "RentingHouse";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "*****";

    public static Connection connect() {
        Connection connection = null;
        String url = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DATABASE;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.print("Kết nối thành cong !" + DATABASE);
        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Lấy danh sách dịch vụ
    public static List<Service> getServicesByUserId(int userId) {
        List<Service> serviceList = new ArrayList<>();
        Connection connection = DatabaseHelper.connect();
        if (connection != null) {
            String query = "SELECT * FROM Services WHERE user_id = ? AND service_id > 3";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int serviceId = resultSet.getInt("service_id");
                    int user_id = resultSet.getInt("user_id");
                    String serviceName = resultSet.getString("service_name");
                    double price = resultSet.getDouble("price");

                    Service service = new Service(serviceId, user_id, serviceName, price);
                    serviceList.add(service);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return serviceList;
    }

    // Lấy danh sách phòng trọ
    public static List<Phong> getAllRooms(int userId) {
        List<Phong> phongList = new ArrayList<>();
        Connection connection = DatabaseHelper.connect();
        if (connection != null) {
            String query = "SELECT * FROM Rooms WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int roomId = resultSet.getInt("room_id");
                    int user_id = resultSet.getInt("user_id");
                    float square = resultSet.getFloat("square");
                    float price = resultSet.getFloat("price");
                    int capacity = resultSet.getInt("capacity");
                    int status = resultSet.getInt("status");

                    Phong phong = new Phong(roomId, user_id, square, price, capacity, status);
                    phongList.add(phong);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return phongList;
    }

    // Hàm them phòng trợ mơi
    public static int insertRoom(int userId, float square, float price, int capacity, int status) {
        int roomId = -1;
        Connection conn = DatabaseHelper.connect();
        if (conn == null) {
            return roomId;
        }
        String query = "INSERT INTO Rooms(user_id, square, price, capacity, status) " +
                "VALUES(?,?,?,?,?);" +
                "SELECT SCOPE_IDENTITY();";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setFloat(2, square);
            stmt.setFloat(3, price);
            stmt.setInt(4, capacity);
            stmt.setInt(5, status);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                roomId = rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    // Hàm thêm cơ so vat chat phong
    public static boolean insertRoomFacilities(int roomId, List<Service> serviceList, RecyclerView recyclerView) {
        Connection conn = DatabaseHelper.connect();
        if (conn == null) {
            return false; // Nếu kết nối thất bại
        }
        String query = "INSERT INTO RoomFacilities(room_id,service_id,number) " +
                "VALUES(?,?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            boolean hasInserted = false;

            for (int i = 0; i < serviceList.size(); i++) {
                Service service = serviceList.get(i);
                if (service.isSelected()) {
                    View view = recyclerView.getLayoutManager().findViewByPosition(i);
                    if (view != null) {
                        EditText edtQuantity = view.findViewById(R.id.edt_quantity);
                        int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());

                        pstmt.setInt(1, roomId);
                        pstmt.setInt(2, service.getServiceId());
                        pstmt.setInt(3, quantity);
                        pstmt.addBatch();
                        hasInserted = true;
                    }
                }
            }
            if (hasInserted) {
                pstmt.executeBatch();
            }
            pstmt.close();
            conn.close();
            return true;
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Phong getRoomById(int roomId) {
        Phong phong = null;
        Connection connection = DatabaseHelper.connect();

        if (connection != null) {
            String query = "SELECT * FROM Rooms WHERE room_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, roomId);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("room_id");
                    int userId = resultSet.getInt("user_id");
                    float square = resultSet.getFloat("square");
                    float price = resultSet.getFloat("price");
                    int capacity = resultSet.getInt("capacity");
                    int status = resultSet.getInt("status");

                    phong = new Phong(id, userId, square, price, capacity, status);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return phong;
    }

    public static List<Service> getServiceByRoomId(int roomId) {
        List<Service> services = new ArrayList<>();
        String query = "SELECT s.service_id, s.user_id, s.service_name, s.price, rf.number " +
                "FROM Services s " +
                "INNER JOIN RoomFacilities rf ON s.service_id = rf.service_id " +
                "WHERE rf.room_id = ?";

        try (Connection connection = DatabaseHelper.connect()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int serviceId = resultSet.getInt("service_id");
                    int userId = resultSet.getInt("user_id");
                    String serviceName = resultSet.getString("service_name");
                    double price = resultSet.getDouble("price");
                    int quantity = resultSet.getInt("number");

                    Service service = new Service(serviceId, userId, serviceName, price);
                    service.setSelected(true);
                    service.setQuantity(quantity);

                    services.add(service);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Hàm cập nh thông tin phongf và dịch vụ
    public static boolean updateRoomAndService(int roomId, int userId, double square,
                                               double price, int capacity, List<Service> updatedServices) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.connect();
            conn.setAutoCommit(false);

            updateRoomInfo(conn, roomId, userId, square, price, capacity);
            Map<Integer, Integer> existingServices = getExistingRoomServices(conn, roomId);
            processRoomFacilitiesChanges(conn, roomId, updatedServices, existingServices);

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //Hàm cap nhat toong tin phòng
    private static void updateRoomInfo(Connection conn, int roomId, int userId,
                                       double square, double price, int capacity) throws SQLException {
        String sql = "UPDATE Rooms SET square = ?, price = ?, capacity = ? WHERE room_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, square);
            stmt.setDouble(2, price);
            stmt.setInt(3, capacity);
            stmt.setInt(4, roomId);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }

    // Lay danh sách dich vu da co
    private static Map<Integer,Integer> getExistingRoomServices(Connection conn, int roomId) throws SQLException{
        Map<Integer, Integer> services = new HashMap<>();
        String sql = "SELECT service_id, number FROM RoomFacilities WHERE room_id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,roomId);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    services.put(rs.getInt("service_id"), rs.getInt("number"));
                }
            }

        }
        return services;
    }
    // Xu ly them/sua/xoa dich vu
    private static void processRoomFacilitiesChanges(Connection conn, int roomId, List<Service> updatedServices,
                                                     Map<Integer, Integer> existingServices) throws SQLException {
        Set<Integer> selectedIds = new HashSet<>();
        for(Service service : updatedServices){
            int serviceId = service.getServiceId();
            int quantity = service.getQuantity();
            boolean selected = service.isSelected();
            if (selected) {
                selectedIds.add(serviceId);
                if (!existingServices.containsKey(serviceId)) {
                    insertRoomFacility(conn, roomId, serviceId, quantity);
                } else if (existingServices.get(serviceId) != quantity) {
                    updateRoomFacility(conn, roomId, serviceId, quantity);
                }
            }
        }
        for (Integer oldServiceId : existingServices.keySet()) {
            if (!selectedIds.contains(oldServiceId)) {
                deleteRoomFacility(conn, roomId, oldServiceId);
            }
        }
    }
    // Thêm dịch vu cho phong
    private static void insertRoomFacility(Connection conn, int roomId, int serviceId, int quantity) throws SQLException {
        String sql = "INSERT INTO RoomFacilities (room_id, service_id, number) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, serviceId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        }
    }
    // Cap nhat dich vụ phong
    private static void updateRoomFacility(Connection conn, int roomId, int serviceId, int quantity) throws SQLException {
        String sql = "UPDATE RoomFacilities SET number = ? WHERE room_id = ? AND service_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, roomId);
            stmt.setInt(3, serviceId);
            stmt.executeUpdate();
        }
    }
    // Xoa dich vụ phong
    private static void deleteRoomFacility(Connection conn, int roomId, int serviceId) throws SQLException {
        String sql = "DELETE FROM RoomFacilities WHERE room_id = ? AND service_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();
        }
    }

    public static int insertTenant(String name, String idNumber, String phone, String birthDate){
        int tenantId = -1;
        Connection connection = DatabaseHelper.connect();
        if(connection == null) return tenantId;

        String query = "INSERT INTO Tenants(tenant_name, id_number, phone_number, birth_date) " +
                "VALUES (?, ?, ?, ?); SELECT SCOPE_IDENTITY();";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1,name);
            stmt.setString(2,idNumber);
            stmt.setString(3,phone);
            stmt.setString(4,birthDate);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                tenantId = rs.getInt(1);
            }
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tenantId;
    }

    // Them hop dong
    public static int insertContract(int userId, int tenantId, int roomId,
                                       String startDate, String endDate, float depositAmount){
        int contractId = -1;
        Connection conn = DatabaseHelper.connect();

        if(conn == null){
            System.err.print("❌ Không thể kết nối SQL Server.");
            return contractId;
        }
        String query = "INSERT INTO Contracts(user_id, tenant_id, room_id, start_date, end_date, deposit_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1,userId);
            stmt.setInt(2,tenantId);
            stmt.setInt(3,roomId);
            stmt.setString(4,startDate);
            stmt.setString(5,endDate);
            stmt.setFloat(6,depositAmount);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                contractId = rs.getInt(1);
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return contractId;
    }
    // Cap nhat trang thai phong
    public static void updateRoomStatus(int roomId,int user_id, int status) {
        Connection conn = DatabaseHelper.connect();
        if (conn == null) return;

        String query = "UPDATE Rooms SET status = ? WHERE room_id = ? AND user_id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, status);
            stmt.setInt(2, roomId);
            stmt.setInt(3,user_id);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lay ma khach thue
    public static int getTenantIdByRoom(int userId, int roomId) {
        int tenantId = -1;

        String sql = "SELECT Contracts.tenant_id " +
                "FROM Rooms " +
                "INNER JOIN Contracts ON Rooms.room_id = Contracts.room_id " +
                "WHERE Rooms.user_id = ? AND Rooms.room_id = ? " +
                "AND GETDATE() BETWEEN Contracts.start_date AND Contracts.end_date";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tenantId = rs.getInt("tenant_id");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenantId;
    }

    // Lấy giá nhà
    public static double getRoomPriceById(int userId, int roomId){
        double price = -1;
        String sql = "SELECT price FROM Rooms WHERE user_id = ? AND room_id = ?;";

        try(Connection conn = DatabaseHelper.connect()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,userId);
            stmt.setInt(2,roomId);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                price = rs.getDouble("price");
            }
            rs.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return price;
    }

    // Hàm taạo mã hoa đơn moi
    public static String generateNewInvoiceCode(int user_id){
        String newCode = "HD00001"; // mặc định nếu chưa có hóa đơn nào
        String sql = "SELECT TOP 1 invoice_id FROM Invoices " +
                "WHERE user_id = ?" +
                "ORDER BY invoice_id DESC";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,user_id);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String lastId = rs.getString("invoice_id");
                int number = Integer.parseInt(lastId.substring(2));
                number +=1;
                newCode = String.format("HD%05d", number);
            }
            rs.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return newCode;
    }

    public static double getServicePrice(String serviceName, int userId) {
        double price = 0;
        String query = "SELECT price FROM Services WHERE service_name = ? AND user_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, serviceName);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                price = rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }

    // Thêm hóa đơn vào bảng Invoices
    public static boolean insertInvoice(String invoiceId, int userId, int roomId, int tenantId, String date, double totalAmount) {
        String sql = "INSERT INTO Invoices (invoice_id, user_id, room_id, tenant_id, month_year, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, invoiceId);
            stmt.setInt(2, userId);
            stmt.setInt(3, roomId);
            stmt.setInt(4, tenantId);
            stmt.setString(5, date);
            stmt.setDouble(6, totalAmount);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy service_id theo tên dịch vụ
    public static int getServiceIdByName(String name, int userId) {
        String sql = "SELECT service_id FROM Services WHERE service_name = ? AND user_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("service_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Thêm chi tiết hóa đơn vào bảng DetailInvoice
    public static boolean insertDetailInvoice(String invoiceId, int serviceId, float number, double totalPrice) {
        String sql = "INSERT INTO DetailInvoice (invoice_id, service_id, number, total_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, invoiceId);
            stmt.setInt(2, serviceId);
            stmt.setFloat(3, number);
            stmt.setDouble(4, totalPrice);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Khach> getAllKhach(int userId){
        List<Khach> khachList = new ArrayList<>();
        Connection conn = DatabaseHelper.connect();
        if(conn != null){
            String query  = "SELECT t.tenant_id, t.tenant_name, t.phone_number, t.id_number, t.birth_date, c.room_id, c.end_date " +
                    "FROM Tenants t " +
                    "JOIN Contracts c ON t.tenant_id = c.tenant_id " +
                    "WHERE c.user_id = ? " +
                    "ORDER BY CASE WHEN c.end_date >= CAST(GETDATE() AS DATE) THEN 0 ELSE 1 END, c.end_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(query)){
                stmt.setInt(1,userId);
                ResultSet resultSet = stmt.executeQuery();

                while (resultSet.next()){
                    int tenantId = resultSet.getInt("tenant_id");
                    String tenantName = resultSet.getString("tenant_name");
                    String phone = resultSet.getString("phone_number");
                    String id_number = resultSet.getString("id_number");
                    String birth_date = resultSet.getString("birth_date");
                    int roomId = resultSet.getInt("room_id");
                    String end_date = resultSet.getString("end_date");

                    Khach khach = new Khach(tenantId,tenantName,phone,id_number,roomId,birth_date,end_date);
                    khachList.add(khach);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return khachList;
    }

    public static void updateTenantInfo(int tenantId, String name, String phone, String idNumber, String birthDate) {
        Connection conn = DatabaseHelper.connect();
        if (conn != null) {
            String query = "UPDATE Tenants SET tenant_name = ?, phone_number = ?, id_number = ?, birth_date = ? WHERE tenant_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, phone);
                stmt.setString(3, idNumber);
                stmt.setString(4, birthDate);
                stmt.setInt(5, tenantId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void getContractsList(int userId, ContractsCallback callback){
        executorService.execute(()->{
            List<HopDong> contractsList = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                connection = DatabaseHelper.connect();
                String query = "SELECT c.contract_id, t.tenant_name, c.start_date, c.end_date, " +
                        "c.status,c.deposit_amount, t.phone_number " +
                        "FROM Contracts c " +
                        "INNER JOIN Tenants t ON c.tenant_id = t.tenant_id " +
                        "WHERE c.user_id = ? " +
                        "ORDER BY c.end_date DESC";

                statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                resultSet = statement.executeQuery();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                while (resultSet.next()){
                    int contractId = resultSet.getInt("contract_id");
                    String tenantName = resultSet.getString("tenant_name");
                    Date startDate = resultSet.getDate("start_date");
                    Date endDate = resultSet.getDate("end_date");
                    boolean isActive = resultSet.getBoolean("status");
                    double deposit = resultSet.getDouble("deposit_amount");

                    TinhTrangHopDong status;
                    if (!isActive) {
                        status = TinhTrangHopDong.EXPIRED;
                    } else if (new Date().after(endDate)) {
                        status = TinhTrangHopDong.EXPIRED;
                    } else if (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000 > endDate.getTime()) {
                        // Nếu còn 7 ngày để hết hạn
                        status = TinhTrangHopDong.PENDING;
                    } else {
                        status = TinhTrangHopDong.ACTIVE;
                    }

                    // Tạo đối tượng hợp đồng
                    HopDong contract = new HopDong(
                            contractId,
                            tenantName,
                            dateFormat.format(startDate),
                            dateFormat.format(endDate),
                            status,
                            deposit
                    );
                    contractsList.add(contract);
                }
                // Chuyển về main thread để trả về kết quả
                final List<HopDong> finalContractsList = contractsList;
                mainHandler.post(() -> {
                    callback.onContractsLoaded(finalContractsList);
                });
            }catch (SQLException e){
                mainHandler.post(() -> {
                    callback.onError("Lỗi khi lấy dữ liệu: " + e.getMessage());
                });
            } finally {
                try{
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                }catch (SQLException e){
                    Log.e(TAG, "Error closing database resources", e);
                }
            }
        });
    }
    //
    public static void updateContractEndDate(int contractId, int user_id, String newEndDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseHelper.connect();
            String sql = "UPDATE Contracts SET end_date = ? WHERE user_id = ? AND contract_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newEndDate); // yyyy-MM-dd nếu cần format
            stmt.setInt(2, user_id);
            stmt.setInt(3,contractId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           try {
               stmt.close();
               conn.close();
           }catch (SQLException e){
               e.printStackTrace();
           }
        }
    }

    // Cac hàm cham dut hop dong
    public static Future<Double> getDepositAmountByContractIdAsync(int contractId) {
        return executorService.submit(() -> {
            double amount = 0;
            String query = "SELECT deposit_amount FROM Contracts WHERE contract_id = ?";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, contractId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    amount = rs.getDouble("deposit_amount");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return amount;
        });
    }


    public static void insertCancelation(int contractId, String createDate, boolean party, double compenAmount) {
        String sql = "INSERT INTO Cancelation (contract_id, create_date, party, compen_amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.setString(2, createDate);
            stmt.setBoolean(3, party); // true = khách, false = chủ
            stmt.setDouble(4, compenAmount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateContractStatusToTerminated(int contractId) {
        String sql = "UPDATE Contracts SET status = 0 WHERE contract_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface ContractsCallback {
        void onContractsLoaded(List<HopDong> contracts);
        void onError(String errorMessage);
    }

    // Hàm lấy thông tin người dùng
    public static User getUserById(int userId) {
        User user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseHelper.connect();
            String sql = "SELECT * FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("phonenumber"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return user;
    }

    // Hàm cập nhật thông tin
    public static boolean updateUser(int userId, String name, String phone, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseHelper.connect();
            String sql = "UPDATE Users SET user_name = ?, phonenumber = ?, password = ? WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setInt(4, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                stmt.close();
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    // Hàm lấy thông tin hợp đồng
    public static ChiTietHopDong getContractDetails(int contractId, int userId) {
        ChiTietHopDong details = null;

        String sql = "SELECT " +
                "Contracts.contract_id, Tenants.tenant_name, Tenants.id_number, Rooms.room_id, " +
                "Rooms.price, Rooms.square, Contracts.start_date, Contracts.end_date, " +
                "Contracts.deposit_amount, Contracts.status, Cancelation.create_date, " +
                "Cancelation.party, Cancelation.compen_amount " +
                "FROM Contracts " +
                "INNER JOIN Rooms ON Contracts.room_id = Rooms.room_id " +
                "INNER JOIN Tenants ON Contracts.tenant_id = Tenants.tenant_id " +
                "LEFT JOIN Cancelation ON Contracts.contract_id = Cancelation.contract_id " +
                "WHERE Contracts.contract_id = ? AND Contracts.user_id = ?";

        Connection conn = DatabaseHelper.connect();

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                details = new ChiTietHopDong();
                details.setContractId(rs.getInt("contract_id"));
                details.setTenantName(rs.getString("tenant_name"));
                details.setIdNumber(rs.getString("id_number"));
                details.setRoomId(rs.getInt("room_id"));
                details.setPrice(rs.getDouble("price"));
                details.setSquare(rs.getDouble("square"));
                details.setStartDate(rs.getString("start_date"));
                details.setEndDate(rs.getString("end_date"));
                details.setDepositAmount(rs.getDouble("deposit_amount"));
                details.setStatus(rs.getInt("status"));
                details.setCancelDate(rs.getString("create_date")); // có thể null
                details.setCancelParty(rs.getInt("party"));
                details.setCompenAmount(rs.getDouble("compen_amount"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return details;
    }

    // Lay danh sach hoa don
    public static List<HoaDon> getInvoiceList(int userId){
        List<HoaDon> invoices = new ArrayList<>();
        Connection conn = DatabaseHelper.connect();
        if(conn != null){
            String sql = "SELECT Invoices.invoice_id, Invoices.month_year, Invoices.room_id, " +
                    "Invoices.tenant_id, Tenants.tenant_name, Tenants.phone_number, Rooms.price, Invoices.total_amount " +
                    "FROM Invoices INNER JOIN Tenants " +
                    "ON Invoices.tenant_id = Tenants.tenant_id " +
                    "INNER JOIN Rooms " +
                    "ON Invoices.room_id = Rooms.room_id " +
                    "WHERE Invoices.user_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()){
                    String invoiceId = rs.getString("invoice_id");
                    String month = rs.getString("month_year");
                    int roomId = rs.getInt("room_id");
                    int tenantId = rs.getInt("tenant_id");
                    String tenant_name = rs.getString("tenant_name");
                    String phone = rs.getString("phone_number");
                    double price = rs.getDouble("price");
                    double total = rs.getDouble("total_amount");

                    HoaDon invoice = new HoaDon(invoiceId,month,roomId,tenantId,tenant_name,phone,price,total);
                    invoices.add(invoice);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try{
                    conn.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return invoices;
    }

    public static List<Service> getServiceByInvoice(String invoiceId, int userId){
        List<Service> services = new ArrayList<>();
        Connection conn = DatabaseHelper.connect();
        if(conn != null){
            String sql = "SELECT Services.service_id ,Services.service_name, DetailInvoice.number, Services.price, " +
                    "DetailInvoice.total_price " +
                    "FROM DetailInvoice INNER JOIN Services " +
                    "ON DetailInvoice.service_id = Services.service_id " +
                    "WHERE DetailInvoice.invoice_id = ? ";

            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,invoiceId);
                ResultSet rs = stmt.executeQuery();

                while(rs.next()){
                    int service_id = rs.getInt("service_id");
                    String service_name = rs.getString("service_name");
                    double price = rs.getDouble("price");
                    int number = rs.getInt("number");
                    double total = rs.getDouble("total_price");

                    Service service = new Service(service_id,userId,service_name,price);
                    service.setQuantity(number);
                    service.setTotal_price(total);
                    services.add(service);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return services;
    }
}
