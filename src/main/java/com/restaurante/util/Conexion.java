package com.restaurante.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class Conexion {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = Conexion.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró config.properties");
            }
            CONFIG.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer config.properties", e);
        }
    }

    private Conexion() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.password"));
    }

    /** Verifica si la conexión con MySQL es correcta. */
    public static boolean probarConexion() {
        try (Connection cn = getConexion()) {
            return cn != null && !cn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
