package database;
import java.sql.*;
import java.util.ArrayList;
import model.Vehiculo;
import model.Auto;
import model.Camion;
import model.Motocicleta;

public class VehiculosDAO {
  public static ArrayList<Vehiculo> getAllVehiculos() {
    ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    String sql = "SELECT * FROM vehiculos";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
         
        while ( rs.next() ) {
          String matricula = rs.getString("matricula");
          String marca = rs.getString("marca");
          String modelo = rs.getString("modelo");
          double precio_por_dia = rs.getDouble("precio_por_dia");
          boolean disponible = rs.getBoolean("disponible");
          String tipo = rs.getString("tipo_vehiculo");
          int numAsientos = rs.getInt("numAsientos");
          int capMaletero = rs.getInt("capMaletero");
          double capCarga = rs.getDouble("capCarga");
          boolean dobCabina = rs.getBoolean("dobCabina");
          int cilindraje = rs.getInt("cilindraje");

          Vehiculo vehiculo;

          if ( tipo.equals("Auto") )
            vehiculo = new Auto (matricula, marca, modelo, precio_por_dia, disponible, numAsientos, capMaletero );
          else if ( tipo.equals("Camion") )
            vehiculo = new Camion (matricula,marca,modelo,precio_por_dia,disponible,capCarga,dobCabina);
          else 
            vehiculo = new Motocicleta(matricula,marca,modelo,precio_por_dia,disponible,cilindraje);
          vehiculo.setDisponible(disponible);

          vehiculos.add(vehiculo);
        }
    }catch ( SQLException e ) {
      e.printStackTrace();
    }
    return vehiculos;
  }
  public static void addVehiculo(Vehiculo vehiculo) {
      String sql = "INSERT INTO vehiculos (matricula, marca, modelo, precio_por_dia, disponible, tipo_vehiculo, numAsientos, capMaletero, capCarga, dobCabina, cilindraje) " +
                  "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

      try (Connection conn = DatabaseConnection.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {

          // Para el caso de Auto
          if (vehiculo instanceof Auto) {
              Auto auto = (Auto) vehiculo;
              stmt.setString(1, auto.getMatricula());
              stmt.setString(2, auto.getMarca());
              stmt.setString(3, auto.getModelo());
              stmt.setDouble(4, auto.getPrecioPorDia());
              stmt.setBoolean(5, auto.getDisponible());
              stmt.setString(6, "Auto");
              stmt.setInt(7, auto.getNumeroAsientos());
              stmt.setInt(8, auto.getCapacidadMaletero());
              stmt.setNull(9, Types.INTEGER); // Si no aplica, dejamos NULL
              stmt.setNull(10, Types.BOOLEAN); // Lo mismo para capCarga y dobCabina
              stmt.setNull(11, Types.INTEGER); // Si no aplica, dejamos NULL
          }
          // Para el caso de Camion
          else if (vehiculo instanceof Camion) {
              Camion camion = (Camion) vehiculo;
              stmt.setString(1, camion.getMatricula());
              stmt.setString(2, camion.getMarca());
              stmt.setString(3, camion.getModelo());
              stmt.setDouble(4, camion.getPrecioPorDia());
              stmt.setBoolean(5, camion.getDisponible());
              stmt.setString(6, "Camion");
              stmt.setNull(7, Types.INTEGER); // Si no aplica, dejamos NULL
              stmt.setNull(8, Types.INTEGER); // Si no aplica, dejamos NULL
              stmt.setDouble(9, camion.getCapacidadCarga());
              stmt.setBoolean(10, camion.isDobleCabina());
              stmt.setNull(11, Types.INTEGER); // Si no aplica, dejamos NULL
          }
          // Para el caso de Motocicleta
          else {
              Motocicleta motocicleta = (Motocicleta) vehiculo;
              stmt.setString(1, motocicleta.getMatricula());
              stmt.setString(2, motocicleta.getMarca());
              stmt.setString(3, motocicleta.getModelo());
              stmt.setDouble(4, motocicleta.getPrecioPorDia());
              stmt.setBoolean(5, motocicleta.getDisponible());
              stmt.setString(6, "Motocicleta");
              stmt.setNull(7, Types.INTEGER); // Si no aplica, dejamos NULL
              stmt.setNull(8, Types.INTEGER); // Si no aplica, dejamos NULL
              stmt.setNull(9, Types.DOUBLE); // Si no aplica, dejamos NULL
              stmt.setNull(10, Types.BOOLEAN); // Lo mismo para dobCabina
              stmt.setInt(11, motocicleta.getCilindraje());
          }

          stmt.executeUpdate();
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }
public static void updateDisponibilidad(String matricula, boolean disponible) {
    String sql = "UPDATE vehiculos SET disponible = ? WHERE matricula = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setBoolean(1, disponible);
        stmt.setString(2, matricula);

        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
