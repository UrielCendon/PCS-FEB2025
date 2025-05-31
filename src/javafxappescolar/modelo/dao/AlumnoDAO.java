package javafxappescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafxappescolar.modelo.ConexionBD;
import javafxappescolar.modelo.pojo.Alumno;
import javafxappescolar.modelo.pojo.ResultadoOperacion;

/**
 *
 * @author uriel
 */
public class AlumnoDAO {
    public static ArrayList<Alumno> obtenerAlumnos() throws SQLException{
        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        Connection conexionBD = ConexionBD.abrirConexion();
        
        if(conexionBD != null){
            String consulta = "SELECT idAlumno, a.nombre, apellidoPaterno, apellidoMaterno, matricula, email, a.idCarrera, " +
                "fechaNacimiento, c.nombre AS 'carrera', c.idFacultad,f.nombre AS 'facultad' " +
                "FROM alumno a " +
                "INNER JOIN carrera c ON c.idCarrera = a.idCarrera " +
                "INNER JOIN facultad f ON f.idFacultad = c.idFacultad";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while(resultado.next()){
                alumnos.add(convertirRegistroAlumno(resultado));
            }
            sentencia.close();
            resultado.close();
            conexionBD.close();
        }else{
            throw new SQLException("Sin conexión en la BD");
        }
        return alumnos;
    }
    
    public static ResultadoOperacion registrarAlumno(Alumno alumno) throws SQLException{
        ResultadoOperacion resultado = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if(conexionBD != null){
            String consulta = "INSERT INTO alumno (nombre, apellidoPaterno, apellidoMaterno, matricula, "
                    + "email, fechaNacimiento, idCarrera, foto) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellidoPaterno());
            sentencia.setString(3, alumno.getApellidoMaterno());
            sentencia.setString(4, alumno.getMatricula());
            sentencia.setString(5, alumno.getEmail());
            sentencia.setString(6, alumno.getFechaNacimiento());
            sentencia.setInt(7, alumno.getIdCarrera());
            sentencia.setBytes(8, alumno.getFoto());
            int filasAfectadas = sentencia.executeUpdate();
            resultado.setError(filasAfectadas <= 0);
            resultado.setMensaje(resultado.isError() ? 
            "Lo sentimos, no fue posible registrar al alumno." : 
            "Alumno(a) registrado(a) correctamente.");
        }else{
            throw new SQLException("Sin conexion a la base de datos");
        }
        return resultado;    
    }
    
    public static byte[] obtenerFotoAlumno(int idAlumno) throws SQLException {
        byte[] foto = null;
        Connection conexionBD = ConexionBD.abrirConexion();
        if(conexionBD != null){
            String consulta = "SELECT foto FROM alumno WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumno);
            ResultSet resultado = sentencia.executeQuery();
            if(resultado.next()){
               foto = resultado.getBytes("foto");
            }else{
                throw new SQLException("Sin conexion a la base de datos");
            }
        }
        return foto;
    }
    
    public static boolean verificarExistenciaMatricula(String matricula, int idAlumnoExcluir) throws SQLException {
        boolean existe = false;
        Connection conexionBD = ConexionBD.abrirConexion();

        if (conexionBD != null) {
            String consulta = "SELECT COUNT(*) FROM alumno WHERE matricula = ? AND idAlumno != ?";
            PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, matricula);
            sentencia.setInt(2, idAlumnoExcluir);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                existe = resultado.getInt(1) > 0;
            }
        } else {
            throw new SQLException("Sin conexión a la base de datos");
        }

        return existe;
    }


    
    public static ResultadoOperacion editarAlumno(Alumno alumno) throws SQLException{
        //TODO -- Restriccion de edicion de matricula
        ResultadoOperacion resultadoOperacion = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if (verificarExistenciaMatricula(alumno.getMatricula(), alumno.getIdAlumno())) {
            throw new SQLException("Matrícula ya registrada en el sistema, ingrese una matrícula diferente");
        }
        if(conexionBD != null){
            String consulta = "UPDATE alumno SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, "
                    + "matricula = ?, email = ?, fechaNacimiento = ?, idCarrera =?, foto = ? WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareCall(consulta);
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellidoPaterno());
            sentencia.setString(3, alumno.getApellidoMaterno());
            sentencia.setString(4, alumno.getMatricula());
            sentencia.setString(5, alumno.getEmail());
            sentencia.setString(6, alumno.getFechaNacimiento());
            sentencia.setInt(7, alumno.getIdCarrera());
            sentencia.setBytes(8, alumno.getFoto());
            sentencia.setInt(9, alumno.getIdAlumno());
            
            int filasAfectadas = sentencia.executeUpdate();
            resultadoOperacion.setError(filasAfectadas <= 0);
            resultadoOperacion.setMensaje(resultadoOperacion.isError() ?
                    "Lo sentimos no fue posible actualizar los datos del alumno" :
                    "La informacion del alumno fue actualizada correctamente");
        }else{
            throw new SQLException("Sin conexion a la base de datos");
        }
        return resultadoOperacion;
    }
    
    public static ResultadoOperacion eliminarAlumno (int idAlumno) throws SQLException{
        ResultadoOperacion resultadoOperacion = new ResultadoOperacion();
        Connection conexionBD = ConexionBD.abrirConexion();
        if(conexionBD != null){
            String consulta = "DELETE FROM alumno WHERE idAlumno = ?";
            PreparedStatement sentencia = conexionBD.prepareCall(consulta);
            sentencia.setInt(1, idAlumno);
            int filasAfectadas = sentencia.executeUpdate();
            resultadoOperacion.setError(filasAfectadas <= 0);
            resultadoOperacion.setMensaje(resultadoOperacion.isError() ?
                    "Lo sentimos no fue posible eliminar al alumno" :
                    "La informacion del alumno fue eliminada correctamente");
        }else{
            throw new SQLException("Sin conexion a la base de datos");
        }
        return resultadoOperacion;        
    }
    
    private static Alumno convertirRegistroAlumno(ResultSet resultado) throws SQLException{
        Alumno alumno = new Alumno();
        alumno.setIdAlumno(resultado.getInt("idAlumno"));
        alumno.setNombre(resultado.getString("nombre"));
        alumno.setApellidoPaterno(resultado.getString("apellidoPaterno"));
        alumno.setApellidoMaterno(resultado.getString("apellidoMaterno"));
        alumno.setApellidoMaterno(resultado.getString("apellidoMaterno"));
        alumno.setMatricula(resultado.getString("matricula"));
        alumno.setEmail(resultado.getString("email"));
        alumno.setIdCarrera(resultado.getInt("idCarrera"));
        alumno.setFechaNacimiento(resultado.getString("fechaNacimiento"));
        alumno.setCarrera(resultado.getString("carrera"));
        alumno.setIdFacultad(resultado.getInt("idFacultad"));
        alumno.setFacultad(resultado.getString("facultad"));
        
        return alumno;
    }
}
