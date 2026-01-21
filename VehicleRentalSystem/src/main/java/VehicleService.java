import java.sql.Connection;
import java.sql.PreparedStatement;

public class VehicleService {

    public static void addVehicle(String name, String type, double price) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO vehicles (vehicle_name, vehicle_type, price_per_day, available) VALUES (?, ?, ?, true)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, type);
            ps.setDouble(3, price);

            ps.executeUpdate();
            System.out.println("🚗 Vehicle added successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAvailableVehicles() {
    }
}

