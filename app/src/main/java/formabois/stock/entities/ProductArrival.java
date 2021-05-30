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
public class ProductArrival {
    private final SimpleStringProperty dbId = new SimpleStringProperty("");
    private final SimpleStringProperty product = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private String productId;
    private String siteId;

    public ProductArrival() {
        this("", "", "", "", "", "");
    }

    public ProductArrival(String dbId, String product, String count, String site, String productId, String siteId) {
        setDbId(dbId);
        setProduct(product);
        setCount(count);
        setSite(site);
        setProductId(productId);
        setSiteId(siteId);
    }

    public String getDbId() {
        return dbId.get();
    }

    public void setDbId(String dbId) {
        this.dbId.set(dbId);
    }

    public String getProduct() {
        return product.get();
    }

    public void setProduct(String product) {
        this.product.set(product);
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * Fait une requête de produits finis à la base de données. Peut filtrer avec les arguments.
     *
     * @param product filtre sur le produit, si ID=0, aucun filtre.
     * @param site    filtre sur le site, si ID=0, aucun filtre.
     * @return La liste de matériaux bruts.
     */
    public static ArrayList<ProductArrival> getProductArrivals(Product product, Site site) {
        final ArrayList<ProductArrival> array = new ArrayList<>();
        try {
            String query = "SELECT product_arrivals.id, products.name AS material_name, sites.name AS site_name, count, product_id, site_id FROM product_arrivals " +
                    "INNER JOIN products ON products.id = product_arrivals.product_id " +
                    "INNER JOIN sites ON sites.id = product_arrivals.site_id";
            if (product != null && product.getId() > 0) {
                query = DatabaseSession.addCondition(query, "product_id = " + product.getId());
            }
            if (site != null && site.getId() > 0) {
                query = DatabaseSession.addCondition(query, "site_id = " + site.getId());
            }
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new ProductArrival(
                        rs.getString("id"),
                        rs.getString("material_name"),
                        rs.getString("count"),
                        rs.getString("site_name"),
                        rs.getString("product_id"),
                        rs.getString("site_id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    /**
     * Ajoute un arrivage de produits finis à la base de données.
     * Va également mettre à jour le stock.
     *
     * @param productId ID du produit
     * @param siteId    ID du site
     * @param count     nombre d'éléments dans l'arrivage
     */
    public static void insertProductArrival(String productId, String siteId, String count) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO product_arrivals (product_id, site_id, count) VALUES (%s, %s, %s)", productId, siteId, count)
            );
            ps.executeQuery();
            updateStock(productId, siteId, count);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Met à jour le stock de produits finis.
     *
     * @param productId l'id du produit
     * @param siteId    l'id du site
     * @param count     le nombre à mettre à jour
     */
    private static void updateStock(String productId, String siteId, String count) {
        try {
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM product_site_stock WHERE product_id = " + productId + " AND site_id = " + siteId);
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock + Integer.parseInt(count);
                final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                        "UPDATE product_site_stock SET count = " + newStock + " WHERE site_id = " + siteId + " AND product_id = " + productId
                );
                ps3.executeQuery();
                return;
            }
            final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(String.format(
                    "INSERT INTO product_site_stock (product_id, count, site_id) VALUES (%s, %s, %s)", productId, count, siteId
            ));
            ps3.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Permet de mettre à jour un arrivage de produit fini.
     * Met automatiquement le stock à jour.
     *
     * @param currentValue l'arrivage de produit fini de départ
     * @param newProductId le nouveau produit d'arrivage
     * @param newSiteId    le nouveau site de destination
     * @param newCount     le nouveau nombre d'arrivage
     */
    public static void updateProductArrival(ProductArrival currentValue, String newProductId, String newSiteId, String newCount) {
        newProductId = newProductId.equals("0") ? currentValue.getProductId() : newProductId;
        newSiteId = newSiteId.equals("0") ? currentValue.getSiteId() : newSiteId;
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE product_arrivals SET product_id = %s, site_id = %s, count = %s WHERE id = %s", newProductId, newSiteId, newCount, currentValue.getDbId())
            );
            ps.executeQuery();
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM product_site_stock WHERE product_id = " + currentValue.getProductId() + " AND site_id = " + currentValue.getSiteId());
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock - Integer.parseInt(currentValue.getCount());
                final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                        "UPDATE product_site_stock SET count = " + newStock + " WHERE site_id = " + currentValue.getSiteId() + " AND product_id = " + currentValue.getProductId()
                );
                ps3.executeQuery();
            }
            updateStock(newProductId, newSiteId, newCount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
