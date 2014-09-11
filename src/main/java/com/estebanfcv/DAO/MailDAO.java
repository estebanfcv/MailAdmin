package com.estebanfcv.DAO;

import com.estebanfcv.Util.Constantes;
import com.estebanfcv.Util.Util;
import com.estebanfcv.conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 *
 * @author estebanfcv
 */
public class MailDAO {

    public boolean verificarPermisoCliente(Calendar fecha) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean permiso = false;
        try {
//            con = DBConnectionManager.getInstance().getConnection();
            con = ConexionDB.getConexion();
            ps = con.prepareStatement("SELECT BLOQUEADO FROM CLIENTES WHERE NUMERO_CLIENTE =?");
            ps.setInt(1, Constantes.NUMERO_CLIENTE);
            rs = ps.executeQuery();
            if (rs.next()) {
                permiso = rs.getByte("BLOQUEADO") == 0;
            } else {
                System.out.println("No esta dado de alta en el sistema, favor de consultar al administrador del sistema");
                Util.agregarLog(Util.armarCadenaLog("[ERROR] No esta dado de alta en el sistema, favor de consultar al administrador del sistema"), fecha);
                JOptionPane.showMessageDialog(null,
                        "No esta dado de alta en el sistema, favor de consultar al administrador del sistema",
                        "MailAdmin", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.agregarDebug(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Util.agregarDebug(e);
            }
        }
        return permiso;
    }
}
