package formabois.stock.entities;

import formabois.stock.DatabaseSession;
import javafx.beans.property.SimpleStringProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Cette classe représente une entrée en base de donnée d'un arrivage de matériaux bruts.
 */
public class MaterialArrival {
    private final SimpleStringProperty dbId = new SimpleStringProperty("");
    private final SimpleStringProperty material = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private final SimpleStringProperty supplier = new SimpleStringProperty("");
    private String materialId;
    private String siteId;

    public MaterialArrival() {
        this("", "", "", "", "", "", "");
    }

    public MaterialArrival(String dbId, String material, String count, String site, String supplier, String materialId, String siteId) {
        setDbId(dbId);
        setMaterial(material);
        setCount(count);
        setSite(site);
        setSupplier(supplier);
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

    public String getSupplier() {
        return supplier.get();
    }

    public void setSupplier(String supplier) {
        this.supplier.set(supplier);
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
     * Fait une requête de matériaux bruts à la base de données. Peut filtrer avec les arguments.
     * @param supplierFilter filtre sur le fournisseur, si vide, aucun filtre.
     * @param material filtre sur le matériau, si ID=0, aucun filtre.
     * @param site filtre sur le site, si ID=0, aucun filtre.
     * @return La liste de matériaux bruts.
     */
    public static ArrayList<MaterialArrival> getMaterialArrivals(String supplierFilter, Material material, Site site) {
        final ArrayList<MaterialArrival> array = new ArrayList<>();
        try {
            String query = "SELECT material_arrivals.id, materials.name AS material_name, sites.name AS site_name, count, supplier, price, material_id, site_id FROM material_arrivals " +
                    "INNER JOIN materials ON materials.id = material_arrivals.material_id " +
                    "INNER JOIN sites ON sites.id = material_arrivals.site_id";
            if (supplierFilter != null && supplierFilter.length() > 0) {
                query = DatabaseSession.addCondition(query, "supplier LIKE '%" + supplierFilter + "%'");
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
                array.add(new MaterialArrival(
                        rs.getString("id"),
                        rs.getString("material_name"),
                        rs.getString("count"),
                        rs.getString("site_name"),
                        rs.getString("supplier"),
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
     * Ajoute un arrivage de matériaux bruts à la base de données.
     * Va également mettre à jour le stock.
     * @param materialId ID du matériau
     * @param siteId ID du site
     * @param count nombre d'éléments dans l'arrivage
     * @param supplier nom du fournisseur
     */
    public static void insertMaterialArrival(String materialId, String siteId, String count, String supplier) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO material_arrivals (material_id, site_id, count, supplier) VALUES (%s, %s, %s, '%s')", materialId, siteId, count, supplier)
            );
            ps.executeQuery();
            updateStock(materialId, siteId, count);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Met à jour le stock de matériaux bruts.
     * @param materialId l'id du matériau
     * @param siteId l'id du site
     * @param count le nombre à mettre à jour
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
                final int newStock = stock + Integer.parseInt(count);
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
     * Permet de mettre à jour un arrivage de matériau brut.
     * Met automatiquement le stock à jour.
     * @param currentValue l'arrivage de matériau de départ
     * @param newMaterialId le nouveau matériau d'arrivage
     * @param newSiteId le nouveau site de destination
     * @param newCount le nouveau nombre d'arrivage
     * @param newSupplier le nouveau fournisseur
     */
    public static void updateMaterialArrival(MaterialArrival currentValue, String newMaterialId, String newSiteId, String newCount, String newSupplier) {
        newMaterialId = newMaterialId.equals("0") ? currentValue.getMaterialId() : newMaterialId;
        newSiteId = newSiteId.equals("0") ? currentValue.getSiteId() : newSiteId;
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE material_arrivals SET material_id = %s, site_id = %s, count = %s, supplier = '%s' WHERE id = %s", newMaterialId, newSiteId, newCount, newSupplier, currentValue.getDbId())
            );
            ps.executeQuery();
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM material_site_stock WHERE material_id = " + currentValue.getMaterialId() + " AND site_id = " + currentValue.getSiteId());
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock - Integer.parseInt(currentValue.getCount());
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
