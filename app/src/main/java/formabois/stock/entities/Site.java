package formabois.stock.entities;

import formabois.stock.DatabaseSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Site {
    private int id;
    private String name;

    public Site(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
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

    /**
     * Obtient tous les sites de la base de données
     * @return un tableau avec tous les sites
     */
    public static ArrayList<Site> getSites() {
        final ArrayList<Site> array = new ArrayList<>();
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement("SELECT * FROM sites");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new Site(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }
}
