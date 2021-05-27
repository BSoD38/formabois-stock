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
}
