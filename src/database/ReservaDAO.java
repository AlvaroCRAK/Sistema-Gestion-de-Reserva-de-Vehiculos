package database;
import java.sql.*;
import java.util.ArrayList;
import model.*;
import java.sql.*;
import java.time.LocalDate;

public class ReservaDAO {

    static private ArrayList<Trabajadores> trabajadores = TrabajadoresDAO.getAllTrabajadores();
    static private ArrayList<Vehiculo> vehiculos = VehiculosDAO.getAllVehiculos();

    public static ArrayList<Reserva> getAllReservas() {
        ArrayList<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            
            while (rs.next()) {
                String id = rs.getString("id");
                String trabajadorId = rs.getString("trabajador_id");
                String vehiculoId = rs.getString("vehiculo_id");
                Date fechaInicio = rs.getDate("fechaInicio");
                Date fechaFin = rs.getDate("fechaFin");
                int diasReservados = rs.getInt("diasReservados");
                boolean cancelado = rs.getBoolean("cancelado");
                double montoActual = rs.getDouble("montoActual");
                double montoTotal = rs.getDouble("montoTotal");
                
                Trabajadores trabajador = null;
                for ( int i = 0; i < trabajadores.size(); i++ ) 
                  if ( trabajadores.get(i).getDni().equals(trabajadorId) ) {
                    trabajador = trabajadores.get(i);
                    break;
                  }
                Vehiculo vehiculo = null;
                for ( int i = 0; i < vehiculos.size(); i++ ) 
                  if ( vehiculos.get(i).getMatricula().equals(vehiculoId) ) {
                    vehiculo = vehiculos.get(i);
                    break;
                  }
            
                Reserva reserva = new Reserva(diasReservados, trabajador, vehiculo);
                reserva.setMontoActual(montoActual);
                reserva.setCancelado(cancelado);
                reserva.setId(id);
                
                reservas.add(reserva);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    public static void addReserva ( Reserva reserva ) {
        String sql = "INSERT INTO reservas (id, trabajador_id, vehiculo_id, fechaInicio, fechaFin, diasReservados, cancelado, montoActual, montoTotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Conversión de LocalDate a java.sql.Date
            LocalDate fechaInicioLocalDate = reserva.getFechaInicio(); 
            java.sql.Date fechaInicio = java.sql.Date.valueOf(fechaInicioLocalDate);
            
            LocalDate fechaFinLocalDate = reserva.getFechaFin(); 
            java.sql.Date fechaFin = java.sql.Date.valueOf(fechaFinLocalDate);
            

            
            stmt.setString(1, reserva.getId());
            stmt.setString(2, reserva.getTrabajadorId());
            stmt.setString(3, reserva.getVehiculoId());
            stmt.setDate(4, fechaInicio );
            stmt.setDate(5, fechaFin );
            stmt.setInt(6, reserva.getDiasReservados() );
            stmt.setBoolean(7, reserva.getCancelado());
            stmt.setDouble(8, reserva.getMontoActual());
            stmt.setDouble(9, reserva.getMontoTotal());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateCancelado(String idReserva, boolean cancelado) {
        String sql = "UPDATE reservas SET cancelado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los parámetros en la consulta SQL
            stmt.setBoolean(1, cancelado);
            stmt.setString(2, idReserva);

            // Ejecutar la actualización
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMontoActual(String idReserva, double pago) {
      String sql = "UPDATE reservas SET montoActual = ? WHERE id = ?";
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setDouble(1, pago);
        stmt.setString(2, idReserva);
        
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
}



