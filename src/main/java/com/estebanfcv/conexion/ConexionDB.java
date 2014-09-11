package com.estebanfcv.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author estebanfcv
 */
public class ConexionDB {

    public static Connection getConexion() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String servidor = "jdbc:mysql://localhost/MAIL";
            String usuarioDB = "root";
            String passwordDB = "ubuntuDB";
            conexion = DriverManager.getConnection(servidor, usuarioDB, passwordDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }

}
