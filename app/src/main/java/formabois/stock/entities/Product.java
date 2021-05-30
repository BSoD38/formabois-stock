package formabois.stock.entities;

import formabois.stock.DatabaseSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Product {
    private int id;
    private String name;
    private int threshold;

    public Product(int id, String name, int threshold) {
        this.id = id;
        this.name = name;
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Requête tous les matériaux de la base de données
     *
     * @return liste des matériaux
     */
    public static ArrayList<Product> getProducts() {
        final ArrayList<Product> array = new ArrayList<>();
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_threshold")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    /**
     * Requête tous les matériaux contenant le terme de recherche dans son nom
     *
     * @param name nom ou partie du nom du matériau
     * @return liste des matériaux contenant le terme de recherche
     */
    public static ArrayList<Product> getProducts(String name) {
        final ArrayList<Product> array = new ArrayList<>();
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement("SELECT * FROM products WHERE name LIKE '%" + name + "%'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_threshold")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    /**
     * Ajoute un matériau brut à la base de données.
     *
     * @param name           nom du matériau
     * @param stockThreshold limite d'alerte de stock du matériau
     */
    public static void insertProducts(String name, String stockThreshold) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO products (name, stock_threshold) VALUES ('%s', %s)", name, stockThreshold)
            );
            ps.executeQuery();
            DatabaseSession.refreshMaterials();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Modifie un matériau brut dans la base de données.
     *
     * @param currentMaterial id du matériau existant
     * @param name            nom du matériau
     * @param stockThreshold  limite d'alerte de stock du matériau
     */
    public static void updateProducts(Product currentMaterial, String name, String stockThreshold) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE products SET name = '%s', stock_threshold = %s WHERE id = %s", name, stockThreshold, currentMaterial.getId())
            );
            ps.executeQuery();
            DatabaseSession.refreshMaterials();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
