package javafxappescolar.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappescolar.JavaFXAppEscolar;
import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.dao.InicioSesionDAO;
import javafxappescolar.modelo.pojo.Usuario;
import javafxappescolar.utilidades.Utilidad;

/**
 *
 * @author uriel
 */
public class FXMLInicioSesionController implements Initializable{

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField tfContraseña;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorContraseña;
    
    @Override
    public void initialize (URL url, ResourceBundle rb){
        
    }

    @FXML
    private void btnClicVerificarSesion(ActionEvent event) {
        String usuario = tfUsuario.getText();
        String contrasena = tfContraseña.getText();
        if(validarCampos(usuario, contrasena))
            validarCredenciales(usuario, contrasena);
    }
    
    private boolean validarCampos(String usuario, String contrasena){
        //Limpiar campos
        lbErrorUsuario.setText("");
        lbErrorContraseña.setText("");
        boolean camposValidos = true;
        if(usuario.isEmpty()){
            lbErrorUsuario.setText("Usuario obligatorio");
            camposValidos = false;
        }
        if(contrasena.isEmpty()){
            lbErrorContraseña.setText("Contraseña obligatoria");
            camposValidos = false;
        }
        return camposValidos;
    }
    
    private void validarCredenciales(String usuario, String contrasena){
        try{
            Usuario usuarioSesion = InicioSesionDAO.verificarCredenciales
            (usuario, contrasena);
            if (usuarioSesion != null){
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "Credenciales correctas", "Bienvenido(a) " 
                        + usuarioSesion.toString() + " al sistema.");
                irPantallaPrincipal(usuarioSesion);
            }else{                
                Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                        "Credenciales incorrectas", "Usuario y/o contraseña "
                        + "incorrectos, por favor verifica tu información.");
            }
        } catch (SQLException ex){
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, 
                    "Problemas de conexión", ex.getMessage());
        }
    }
    
    private void irPantallaPrincipal(Usuario usuario){
        try {
            Stage escenarioBase = (Stage) tfUsuario.getScene().getWindow();
            //Parent vista = FXMLLoader.load(JavaFXAppEscolar.class.getResource("vista/FXMLPrincipal.fxml"));
            FXMLLoader cargador = new FXMLLoader(JavaFXAppEscolar.class.getResource("vista/FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            FXMLPrincipalController controlador = cargador.getController();
            controlador.inicializarInformacion(usuario);
            Scene escenaPrincipal = new Scene(vista);
            escenarioBase.setScene(escenaPrincipal);
            escenarioBase.setTitle("Home");
            escenarioBase.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
