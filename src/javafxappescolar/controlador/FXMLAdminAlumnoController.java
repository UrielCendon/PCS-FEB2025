package javafxappescolar.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.interfaz.INotificacion;
import javafxappescolar.modelo.dao.AlumnoDAO;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.ResultadoOperacion;
import javafxappescolar.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author eugen
 */
public class FXMLAdminAlumnoController implements Initializable, INotificacion {

    @FXML
    private TextField tfBuscar;
    @FXML
    private TableView<Alumno> tvAlumnos;
    @FXML
    private TableColumn colMatricula;
    @FXML
    private TableColumn colApePaterno;
    @FXML
    private TableColumn colApeMaterno;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colFacultad;
    @FXML
    private TableColumn colCarrera;

    private ObservableList<Alumno> alumnos;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioALumno(false, null);
    }

    @FXML
    private void btnClicModificar(ActionEvent event) {
        Alumno alumno = tvAlumnos.getSelectionModel().getSelectedItem();
        if(alumno != null){
            irFormularioALumno(true, alumno);
        }else{
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "No se seleciono un alumno",
                    "Selecciona un alumno de la tabla para editar");
        }
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
        int pos = tvAlumnos.getSelectionModel().getSelectedIndex();
        if(pos >= 0){
            Alumno alumno = alumnos.get(pos);
            String confirmacion = String.format("¿Estás seguro de eliminar al alumno(a)? %s %s \nUna vez eliminado el registro no podrá ser recuperado.", alumno.getNombre(), alumno.getApellidoPaterno());
            if(Utilidad.mostrarConfirmacion("Eliminar alumno(a)", confirmacion)){
                eliminarAlumno(alumno.getIdAlumno());
            }
        }else{
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "No se seleciono un alumno",
                    "Selecciona un alumno de la tabla para editar");
        }
    }
    
    private void configurarTabla(){
        colMatricula.setCellValueFactory(new PropertyValueFactory("matricula"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApePaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApeMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colCarrera.setCellValueFactory(new PropertyValueFactory("carrera"));
        colFacultad.setCellValueFactory(new PropertyValueFactory("facultad"));
    }
    
    private void cargarInformacionTabla(){
        try {
            alumnos = FXCollections.observableArrayList();
            ArrayList<Alumno> alumnosDAO = AlumnoDAO.obtenerAlumnos();
            alumnos.addAll(alumnosDAO);
            tvAlumnos.setItems(alumnos);
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", "Por el momento no se"
                    + " puede mostrar la informacion de los alumnos");
            cerrarVentana();
        }
                
    }
    
    private void cerrarVentana(){
        Stage ventana = (Stage) tfBuscar.getScene().getWindow();
        ventana.close();
    }
    
    private void irFormularioALumno(boolean esEdicion, Alumno alumnoEdicion){
        try {
            Stage escenarioFormulario = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLFormularioAlumno.fxml"));
            Parent vista = loader.load();
            //TODO paso de parametros
            FXMLFormularioAlumnoController controlador = loader.getController();
            controlador.inicializarInformacion(esEdicion, alumnoEdicion, this);
            Scene escena = new Scene(vista);
            escenarioFormulario.setScene(escena);
            escenarioFormulario.setTitle("Formulario Alumno");
            escenarioFormulario.initModality(Modality.APPLICATION_MODAL);
            escenarioFormulario.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(FXMLAdminAlumnoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void operacionExitosa(String tipo, String nombreAlumno) {
        cargarInformacionTabla();
    }
    
    private void eliminarAlumno(int idAlumno){
        try {
            ResultadoOperacion resultado = AlumnoDAO.eliminarAlumno(idAlumno);
            if(!resultado.isError()){
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Alumno(a) eliminado.", "El registro del alumno fue eliminado correctamente.");
                cargarInformacionTabla();
            }else{
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Problemas al eliminar.", resultado.getMensaje());
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Problemas al eliminar", "Lo sentimos, por el momento no se puede eliminar.");
        }
    }
}