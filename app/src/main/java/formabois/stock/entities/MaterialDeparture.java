package formabois.stock.entities;

import formabois.stock.DatabaseSession;
import javafx.beans.property.SimpleStringProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Cette classe représente une entrée en base de donnée d'une sortie de matériaux bruts.
 */
public class MaterialDeparture {
    private final SimpleStringProperty dbId = new SimpleStringProperty("");
    private final SimpleStringProperty material = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private final SimpleStringProperty destination = new SimpleStringProperty("");
    private String materialId;
    private String siteId;

    public MaterialDeparture() {
        this("", "", "", "", "", "", "");
    }

    public MaterialDeparture(String dbId, String material, String count, String site, String destination, String materialId, String siteId) {
        setDbId(dbId);
        setMaterial(material);
        setCount(count);
        setSite(site);
        setDestination(destination);
        setMaterialId(materialId);
        setSiteId(siteId);
    }

    public String getDbId() {
        return dbId.get();
    }

    public void setDbId(String dbId) {
        this.dbId.set(dbId);
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

    public String getDestination() {
        return destination.get();
    }

    public void setDestination(String destination) {
        this.destination.set(destination);
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
     * Fait une requête de sorties de matériaux bruts à la base de données. Peut filtrer avec les arguments.
     *
     * @param destinationFilter filtre sur la destination, si vide, aucun filtre.
     * @param material          filtre sur le matériau, si ID=0, aucun filtre.
     * @param site              filtre sur le site, si ID=0, aucun filtre.
     * @return La liste de matériaux bruts.
     */
    public static ArrayList<MaterialDeparture> getMaterialDepartures(String destinationFilter, Material material, Site site) {
        final ArrayList<MaterialDeparture> array = new ArrayList<>();
        try {
            String query = "SELECT material_departures.id, materials.name AS material_name, sites.name AS site_name, count, destination, material_id, site_id FROM material_departures " +
                    "INNER JOIN materials ON materials.id = material_departures.material_id " +
                    "INNER JOIN sites ON sites.id = material_departures.site_id";
            if (destinationFilter != null && destinationFilter.length() > 0) {
                query = DatabaseSession.addCondition(query, "destination LIKE '%" + destinationFilter + "%'");
            }
            if (material != null && material.getId() > 0) {
                query = DatabaseSession.addCondition(query, "material_id = " + material.getId());
            }
            if (site != null && site.getId() > 0) {
                query = DatabaseSession.addCondition(query, "site_id = " + site.getId());
            }
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new MaterialDeparture(
                        rs.getString("id"),
                        rs.getString("material_name"),
                        rs.getString("count"),
                        rs.getString("site_name"),
                        rs.getString("destination"),
                        rs.getString("material_id"),
                        rs.getString("site_id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    /**
     * Ajoute un départ de matériaux bruts à la base de données.
     * Va également mettre à jour le stock.
     *
     * @param materialId  ID du matériau
     * @param siteId      ID du site
     * @param count       nombre d'éléments dans le départ
     * @param destination nom de la destination
     */
    public static void insertMaterialDeparture(String materialId, String siteId, String count, String destination) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO material_departures (material_id, site_id, count, destination) VALUES (%s, %s, %s, '%s')", materialId, siteId, count, destination)
            );
            ps.executeQuery();
            updateStock(materialId, siteId, count);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Met à jour le stock de matériaux bruts.
     *
     * @param materialId l'id du matériau
     * @param siteId     l'id du site
     * @param count      le nombre à mettre à jour
     */
    private static void updateStock(String materialId, String siteId, String count) {
        try {
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM material_site_stock WHERE material_id = " + materialId + " AND site_id = " + siteId);
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock - Integer.parseInt(count);
                final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                        "UPDATE material_site_stock SET count = " + newStock + " WHERE site_id = " + siteId + " AND material_id = " + materialId
                );
                ps3.executeQuery();
                return;
            }
            final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO material_site_stock (material_id, count, site_id) VALUES (%s, %s, %s)", materialId, count, siteId)
            );
            ps3.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Permet de mettre à jour un départ de matériau brut.
     * Met automatiquement le stock à jour.
     *
     * @param currentValue   le départ de matériau de départ
     * @param newMaterialId  le nouveau matériau de départ
     * @param newSiteId      le nouveau site de destination
     * @param newCount       le nouveau nombre de départs
     * @param newDestination la nouvelle destination
     */
    public static void updateMaterialDeparture(MaterialDeparture currentValue, String newMaterialId, String newSiteId, String newCount, String newDestination) {
        newMaterialId = newMaterialId.equals("0") ? currentValue.getMaterialId() : newMaterialId;
        newSiteId = newSiteId.equals("0") ? currentValue.getSiteId() : newSiteId;
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE material_departures SET material_id = %s, site_id = %s, count = %s, destination = '%s' WHERE id = %s", newMaterialId, newSiteId, newCount, newDestination, currentValue.getDbId())
            );
            ps.executeQuery();
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM material_site_stock WHERE material_id = " + currentValue.getMaterialId() + " AND site_id = " + currentValue.getSiteId());
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock + Integer.parseInt(currentValue.getCount());
                final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                        "UPDATE material_site_stock SET count = " + newStock + " WHERE site_id = " + currentValue.getSiteId() + " AND material_id = " + currentValue.getMaterialId()
                );
                ps3.executeQuery();
            }
            updateStock(newMaterialId, newSiteId, newCount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
