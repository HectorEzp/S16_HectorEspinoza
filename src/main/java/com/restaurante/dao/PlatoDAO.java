package com.restaurante.dao;

import com.restaurante.model.Plato;
import com.restaurante.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos (CRUD) de la tabla plato usando JDBC.
 */
public class PlatoDAO {

    private static final String SQL_LISTAR =
            "SELECT id_plato, nombre, categoria, descripcion, precio, tiempo_preparacion, disponibilidad "
                    + "FROM plato ORDER BY id_plato";

    private static final String SQL_BUSCAR =
            "SELECT id_plato, nombre, categoria, descripcion, precio, tiempo_preparacion, disponibilidad "
                    + "FROM plato WHERE nombre LIKE ? OR categoria LIKE ? ORDER BY id_plato";

    private static final String SQL_INSERTAR =
            "INSERT INTO plato (nombre, categoria, descripcion, precio, tiempo_preparacion, disponibilidad) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE plato SET nombre = ?, categoria = ?, descripcion = ?, precio = ?, "
                    + "tiempo_preparacion = ?, disponibilidad = ? WHERE id_plato = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM plato WHERE id_plato = ?";

    /** CONSULTAR: devuelve todos los platos. */
    public List<Plato> listar() throws SQLException {
        List<Plato> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** CONSULTAR: busca por nombre o categoría. */
    public List<Plato> buscar(String texto) throws SQLException {
        List<Plato> lista = new ArrayList<>();
        String filtro = "%" + texto + "%";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** REGISTRAR: inserta un nuevo plato. */
    public boolean insertar(Plato p) throws SQLException {
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getTiempoPreparacion());
            ps.setBoolean(6, p.isDisponibilidad());
            return ps.executeUpdate() > 0;
        }
    }

    /** ACTUALIZAR: modifica un plato existente. */
    public boolean actualizar(Plato p) throws SQLException {
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getTiempoPreparacion());
            ps.setBoolean(6, p.isDisponibilidad());
            ps.setInt(7, p.getIdPlato());
            return ps.executeUpdate() > 0;
        }
    }

    /** ELIMINAR: borra un plato por su id. */
    public boolean eliminar(int idPlato) throws SQLException {
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idPlato);
            return ps.executeUpdate() > 0;
        }
    }

    private Plato mapear(ResultSet rs) throws SQLException {
        return new Plato(
                rs.getInt("id_plato"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getString("descripcion"),
                rs.getBigDecimal("precio"),
                rs.getInt("tiempo_preparacion"),
                rs.getBoolean("disponibilidad"));
    }
}
