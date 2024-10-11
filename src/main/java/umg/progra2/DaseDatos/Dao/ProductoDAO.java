package umg.progra2.DaseDatos.Dao;

import umg.progra2.DaseDatos.conexion.DatabaseConnection;
import umg.progra2.DaseDatos.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, pais, precio, existencia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getPais());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getExistencia());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    producto.setIdProducto(generatedKeys.getInt(1));
                }
            }
        }
    }


    public Producto obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("pais"),
                            rs.getDouble("precio"), rs.getInt("existencia"));
                }
            }
        }
        return null;
    }


    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY pais, precio, existencia, nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("pais"),
                        rs.getDouble("precio"), rs.getInt("existencia")));
            }
        }
        return productos;
    }




    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, pais = ?, precio = ?, existencia = ? WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getPais());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getExistencia());
            pstmt.setInt(5, producto.getIdProducto());
            pstmt.executeUpdate();
        }
    }


    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }


    public boolean eliminarCondicional(int id) throws SQLException {
        String sqlCheck = "SELECT precio FROM productos WHERE id_producto = ?";
        String sqlDelete = "DELETE FROM productos WHERE id_producto = ? AND precio = 0.00";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {

            pstmtCheck.setInt(1, id);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    double precio = rs.getDouble("precio");
                    if (precio != 0.00) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            pstmtDelete.setInt(1, id);
            int rowsAffected = pstmtDelete.executeUpdate();
            return rowsAffected > 0;
        }
    }

}