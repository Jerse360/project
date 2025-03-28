package ClienteInterfaz;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase que gestiona la conexión a la base de datos MySQL.
 */
public class Conexion {

    /**
     * Establece y retorna una conexión a la base de datos MySQL.
     *
     * @return Un objeto Connection si la conexión es exitosa, o null si ocurre un error.
     */
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
