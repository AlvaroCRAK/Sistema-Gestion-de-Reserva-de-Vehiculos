package controller;

import view.ReservasView;
import view.RegistroReservaView;
import view.ModificarReservas;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;

import view.VehiculosView;
import model.*;

import database.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ReservasController {
  private static ReservasController instance;


  private ReservasView reservasView;
  private boolean temp1 = false;
  private RegistroReservaView registroReservasView;
  private ModificarReservas modificarReservasView;
  private boolean temp2 = false;
  private boolean temp3 = false;
  public ArrayList<Reserva> reservas;
  //private int totalReserva;

  
  public static ReservasController getInstance() {
    if ( instance == null ) 
      instance = new ReservasController ();

    return instance;
  }

  private ReservasController () {
    realizarConexion();
    this.reservasView = new ReservasView();
    this.registroReservasView = new RegistroReservaView();
    this.modificarReservasView = new ModificarReservas();
    this.reservas = ReservaDAO.getAllReservas();
//modificarReservasView = new ModificarReservas();
//modificarReservasView.setVisible(true);
    actualizarTablaReserva();
  }
  public void realizarConexion() {
      try {
          // Obtener la conexión desde la clase DatabaseConnection
          Connection conn = DatabaseConnection.getConnection();
          System.out.println("Conexión exitosa a la base de datos");

          // Aquí puedes hacer lo que necesites con la conexión, como consultas SQL
          // ...

      } catch (SQLException e) {
          e.printStackTrace();
      }
  }


  public void start () {
    instance.initReservasView();
    instance.initRegistroReservaView();
    instance.initModificarReservasView();
    reservasView.setVisible(true);
  }
  
  private void initReservasView () {
    if (temp1) return; // Evita múltiples inicializaciones
    temp1 = true;

    reservasView.getBtnAnhadir().addActionListener ( e -> {
      reservasView.setVisible(false);
      registroReservasView.setVisible(true);
    });

    reservasView.getBtnBuscar().addActionListener ( e -> buscarReserva() );
    reservasView.getComboBox().addActionListener ( e -> filtrarReserva() );

    reservasView.getBtnVehiculos().addActionListener ( e -> {
      reservasView.setVisible(false);
      VehiculoController vehiculoController = VehiculoController.getInstance();
      vehiculoController.start();
    });
    reservasView.getBtnTrabajadores().addActionListener ( e -> {
      reservasView.setVisible(false);
      TrabajadoresController trabajadoresController = TrabajadoresController.getInstance();
      trabajadoresController.start();
    });

    reservasView.getBtnModificar().addActionListener ( e -> {
      reservasView.setVisible(false);
      modificarReservasView.setVisible(true);
      System.out.println("llego");

    });
  }
  private void initModificarReservasView() {
    if ( temp2 ) return;
    temp2 = true;

    modificarReservasView.getBtnModificar().addActionListener(e -> {
        try {
            String id = modificarReservasView.getTextIdReserva().getText();
            double pago = Double.parseDouble(modificarReservasView.getTextPago().getText());

            if (id.isEmpty()) throw new Exception("El ID de la reserva no puede estar vacío");
            if (pago <= 0) throw new Exception("El monto del pago debe ser mayor a 0");


            // Buscar reserva
            Reserva reserva = buscarReserva(id);
            if ( reserva == null ) throw new Exception("Reserva no identificada");

            // Actualizar monto actual y verificar estado
            System.out.println(reserva.getMontoActual()); // 0
            reserva.setMontoActual(reserva.getMontoActual() + pago);
            System.out.println(reserva.getMontoActual()); // 500
            String matricula = reserva.getVehiculoId();

            if (reserva.getMontoActual() >= reserva.getMontoTotal()) {
                reserva.setCancelado(true);
                VehiculosDAO.updateDisponibilidad(matricula, true);
                JOptionPane.showMessageDialog(modificarReservasView,
                    reserva.getMontoActual() > reserva.getMontoTotal()
                        ? "Pago con éxito, devolver vuelto: " + (reserva.getMontoActual() - reserva.getMontoTotal())
                        : "Pago TOTAL con éxito");
                reserva.setMontoActual(reserva.getMontoTotal());
                VehiculosDAO.updateDisponibilidad(reserva.getVehiculoId(), true);
            } else {
                JOptionPane.showMessageDialog(modificarReservasView, "Pago con éxito, aún debe: " +
                    (reserva.getMontoTotal() - reserva.getMontoActual()));
            }

            // Actualizar base de datos
            ReservaDAO.updateMontoActual(reserva.getId(), reserva.getMontoActual());
            ReservaDAO.updateCancelado(reserva.getId(), reserva.getCancelado());

            // Actualizar vista
            reservas.clear();
            reservas = ReservaDAO.getAllReservas();

            actualizarTablaReserva();

            VehiculoController vehiculoController = VehiculoController.getInstance();
            vehiculoController.actualizarTablaVehiculos();

            modificarReservasView.setVisible(false);
            reservasView.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(modificarReservasView, "Error al modificar reserva: " + ex.getMessage());
        }
    });
    
    modificarReservasView.getBtnCancelar().addActionListener( e -> {
      modificarReservasView.setVisible(false);
      reservasView.setVisible(true);
    });
  }

  private void buscarReserva() {
    String id = reservasView.getTextField().getText();

    for ( int i = 0; i < reservas.size(); i++ ) {
      if ( reservas.get(i).getId().equals(id) ) {
        JOptionPane.showMessageDialog(reservasView, reservas.get(i).toString());
        return;
      }
    }

    JOptionPane.showMessageDialog(reservasView, "Reserva no encontrado.");
  }
  private Reserva buscarReserva(String id) {
    for ( int i = 0; i < reservas.size(); i++ ) 
      if ( reservas.get(i).getId().equals(id) )
        return reservas.get(i);
    return null;
  }
  private void filtrarReserva() {
    String tipoReserva = (String) reservasView.getComboBox().getSelectedItem();
    DefaultTableModel model = (DefaultTableModel) reservasView.getTabla().getModel();
    model.setRowCount(0);

    for ( int i = 0; i < reservas.size(); i++ ) {
      Reserva reserva = reservas.get(i);
      String tipo;
      if ( reserva.getCancelado() ) 
        tipo = "Cancelados";
      else 
        tipo = "Por Cancelar";

      boolean coincide = false;
      if ( tipo.equals(tipoReserva) || tipoReserva.equals("Todos"))
        coincide = true;
      
      if ( coincide ) {
        model.addRow(new Object[]{
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getMontoActual(),
                reserva.getMontoTotal()
        });
      }
    }
  }
  private void initRegistroReservaView() {
    if (temp3) return; // Evita inicializaciones múltiples
      temp3 = true;

    registroReservasView.getBtnRegister().addActionListener(e -> {
        try {

          int diasReservados = (Integer) registroReservasView.getSpinner().getValue();
          String dniCliente = registroReservasView.getTextCliente().getText();
          String matriculaVehiculo = registroReservasView.getTextVehiculo().getText();

          Vehiculo vehiculo = buscarVehiculo(matriculaVehiculo);
          Trabajadores cliente = buscarTrabajador(dniCliente);

          if ( vehiculo == null ) 
            throw new Exception("No se encontro la matricula");
          else if ( cliente == null ) 
            throw new Exception("No se encontro el DNI");

          if ( vehiculo.getDisponible() == false ) 
            throw new Exception ("Vehiculo no disponible");
          if ( !cliente.getTipoTrabajador().equals("Cliente") )
            throw new Exception ( "La reserva debe ser de un Cliente");

          vehiculo.setDisponible(false);

          Reserva  reserva = new Reserva(diasReservados, cliente, vehiculo);
          VehiculosDAO.updateDisponibilidad(matriculaVehiculo, false);
          ReservaDAO.addReserva(reserva);
          reservas.add(reserva);
          //totalReserva++;

          actualizarTablaReserva();

          registroReservasView.setVisible(false);
          reservasView.setVisible(true);
          JOptionPane.showMessageDialog(reservasView, "Reserva registrado con exito.");

        } catch ( Exception ex ) {
          JOptionPane.showMessageDialog( registroReservasView, "Error al registrar Reserva: " + ex.getMessage());
          
        }

    });

    registroReservasView.getBtnCancelar().addActionListener(e -> {
        registroReservasView.setVisible(false);
        reservasView.setVisible(true);
    });
    actualizarTablaReserva();

  }
  private void actualizarTablaReserva () {
    reservas = ReservaDAO.getAllReservas();
    DefaultTableModel model = (DefaultTableModel) reservasView.getTabla().getModel();
    model.setRowCount(0);

    for ( int i = 0; i < reservas.size(); i++ ) {
      Reserva reserva = reservas.get(i);
          model.addRow(new Object[]{
            reserva.getId(),
            reserva.getFechaInicio(),
            reserva.getFechaFin(),
            reserva.getMontoActual(),
            reserva.getMontoTotal()
          });
    }
  }
  private Vehiculo buscarVehiculo ( String matricula ) {
    VehiculoController vehiculoController = VehiculoController.getInstance();

    for ( int i = 0; i < vehiculoController.vehiculos.size(); i++ ) {
      if ( vehiculoController.vehiculos.get(i).getMatricula().equals(matricula))
        return vehiculoController.vehiculos.get(i);
    }
    return null;
  }
  private Trabajadores buscarTrabajador ( String dni ) {
    TrabajadoresController trabajadoresController = TrabajadoresController.getInstance();

    for ( int i = 0; i < trabajadoresController.trabajadores.size(); i++ ) {
      if ( trabajadoresController.trabajadores.get(i).getDni().equals(dni))
        return trabajadoresController.trabajadores.get(i);
    }
    return null;
  }
}

