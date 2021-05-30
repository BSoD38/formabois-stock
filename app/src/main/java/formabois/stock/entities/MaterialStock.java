package formabois.stock.entities;

import formabois.stock.DatabaseSession;
import javafx.beans.property.SimpleStringProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Cette classe représente une entrée en base de donnée du stock de matériaux d'un site donné.
 */
public class MaterialStock {
    private final SimpleStringProperty material = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private String materialId;
    private String siteId;

    public MaterialStock() {
        this("", "", "", "", "");
    }

    public MaterialStock(String material, String site, String count, String materialId, String siteId) {
        setMaterial(material);
        setSite(site);
        setCount(count);
        setMaterialId(materialId);
        setSiteId(siteId);
    }


    public String getMaterial() {
        return material.get();
    }

    public void setMaterial(String material) {
        this.material.set(material);
    }

    public String getCount() {
        return count.get();
    }

    public void setCount(String count) {
        this.count.set(count);
    }

    public String getSite() {
        return site.get();
    }

    public void setSite(String site) {
        this.site.set(site);
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * Fait une requête des stocks de matériaux bruts à la base de données. Peut filtrer avec les arguments.
     *
     * @param material filtre sur le matériau, si ID=0, aucun filtre.
     * @param site     filtre sur le site, si ID=0, aucun filtre.
     * @return La liste des stocks de matériaux bruts.
     */
    public static ArrayList<MaterialStock> getMaterialStocks(Material material, Site site) {
        final ArrayList<MaterialStock> array = new ArrayList<>();
        try {
            String query = "SELECT materials.name AS material_name, sites.name AS site_name, count, material_id, site_id FROM material_site_stock " +
                    "INNER JOIN materials ON materials.id = material_site_stock.material_id " +
                    "INNER JOIN sites ON sites.id = material_site_stock.site_id";
            if (material != null && material.getId() > 0) {
                query = DatabaseSession.addCondition(query, "material_id = " + material.getId());
            }
            if (site != null && site.getId() > 0) {
                query = DatabaseSession.addCondition(query, "site_id = " + site.getId());
            }
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new MaterialStock(
                        rs.getString("material_name"),
                        rs.getString("site_name"),
                        rs.getString("count"),
                        rs.getString("material_id"),
                        rs.getString("site_id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    public static ArrayList<MaterialStock> getLowStock() {
        final ArrayList<MaterialStock> array = new ArrayList<>();
        try {
            String query = "SELECT materials.name AS material_name, sites.name AS site_name, count, material_id, site_id, materials.stock_threshold FROM material_site_stock " +
                    "INNER JOIN materials ON materials.id = material_site_stock.material_id " +
                    "INNER JOIN sites ON sites.id = material_site_stock.site_id";
            query = DatabaseSession.addCondition(query, "count < stock_threshold");
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new MaterialStock(
                        rs.getString("material_name"),
                        rs.getString("site_name"),
                        rs.getString("count"),
                        rs.getString("material_id"),
                        rs.getString("site_id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }
}
