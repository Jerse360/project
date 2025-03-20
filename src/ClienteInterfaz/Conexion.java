package ClienteInterfaz;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion
{
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/farmacia", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
