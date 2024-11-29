package database;
import java.sql.*;
import java.util.ArrayList;
import model.Trabajadores;

public class TrabajadoresDAO {

    public static ArrayList<Trabajadores> getAllTrabajadores() {
        ArrayList<Trabajadores> trabajadores = new ArrayList<>();
        String sql = "SELECT * FROM trabajadores"; // Asegúrate de que este nombre coincida con el de tu tabla en la base de datos
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellidos");
                String email = rs.getString("email");
                String clave = rs.getString("claveAcceso");
                String fechaNacimiento = rs.getString("fechaNacimiento");
                String dni = rs.getString("dni");
                String tipoTrabajador = rs.getString("tipoTrabajador");
                
                Trabajadores trabajador = new Trabajadores(nombre, apellido, email, clave, fechaNacimiento, dni, tipoTrabajador);
                trabajadores.add(trabajador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trabajadores;
    }

    public static void addTrabajador(Trabajadores trabajador) {
        String sql = "INSERT INTO trabajadores (nombre, apellidos, email, claveAcceso, fechaNacimiento, dni, tipoTrabajador) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, trabajador.getNombre());
            stmt.setString(2, trabajador.getApellido());
            stmt.setString(3, trabajador.getEmail());
            stmt.setString(4, trabajador.getClaveAcceso());
            stmt.setString(5, trabajador.getFechaNacimiento());
            stmt.setString(6, trabajador.getDni());
            stmt.setString(7, trabajador.getTipoTrabajador());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

