package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;
    private static final String URL = "jdbc:mysql://localhost:3306/rentar_vehiculos";
    private static final String USERNAME = "root";  // tu usuario de MySQL
    private static final String PASSWORD = "zublime";  // tu contraseña de MySQL

    // Constructor privado para evitar instanciación
    private DatabaseConnection() {}

    // Método para obtener la instancia de la conexión (Singleton)
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                // Cargar el conector JDBC de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se encontró el driver de MySQL", e);
            }
        }
        return conn;
    }

    // Método para cerrar la conexión (opcional, por si deseas cerrarla explícitamente)
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

