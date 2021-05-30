package formabois.stock.entities;

import formabois.stock.DatabaseSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Material {
    private int id;
    private String name;
    private int threshold;

    public Material(int id, String name, int threshold) {
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
    public static ArrayList<Material> getMaterials() {
        final ArrayList<Material> array = new ArrayList<>();
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement("SELECT * FROM materials");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new Material(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_threshold")));
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
    public static ArrayList<Material> getMaterials(String name) {
        final ArrayList<Material> array = new ArrayList<>();
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement("SELECT * FROM materials WHERE name LIKE '%" + name + "%'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new Material(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_threshold")));
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
    public static void insertMaterial(String name, String stockThreshold) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO materials (name, stock_threshold) VALUES ('%s', %s)", name, stockThreshold)
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
    public static void updateMaterial(Material currentMaterial, String name, String stockThreshold) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE materials SET name = '%s', stock_threshold = %s WHERE id = %s", name, stockThreshold, currentMaterial.getId())
            );
            ps.executeQuery();
            DatabaseSession.refreshMaterials();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
