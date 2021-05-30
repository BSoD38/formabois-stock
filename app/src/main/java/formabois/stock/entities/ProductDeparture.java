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
public class ProductDeparture {
    private final SimpleStringProperty dbId = new SimpleStringProperty("");
    private final SimpleStringProperty product = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private final SimpleStringProperty destination = new SimpleStringProperty("");
    private float price;
    private String productId;
    private String siteId;

    public ProductDeparture() {
        this("", "", "", "", "", 0, "", "");
    }

    public ProductDeparture(String dbId, String product, String count, String site, String destination, float price, String productId, String siteId) {
        setDbId(dbId);
        setProduct(product);
        setCount(count);
        setSite(site);
        setDestination(destination);
        setPrice(price);
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

    public String getDestination() {
        return destination.get();
    }

    public void setDestination(String destination) {
        this.destination.set(destination);
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Fait une requête de sorties de produits finis à la base de données. Peut filtrer avec les arguments.
     *
     * @param destinationFilter filtre sur la destination, si vide, aucun filtre.
     * @param product           filtre sur le produit, si ID=0, aucun filtre.
     * @param site              filtre sur le site, si ID=0, aucun filtre.
     * @return La liste de matériaux bruts.
     */
    public static ArrayList<ProductDeparture> getProductDepartures(String destinationFilter, Product product, Site site) {
        final ArrayList<ProductDeparture> array = new ArrayList<>();
        try {
            String query = "SELECT product_departures.id, products.name AS product_name, sites.name AS site_name, count, price, destination, product_id, site_id FROM product_departures " +
                    "INNER JOIN products ON products.id = product_departures.product_id " +
                    "INNER JOIN sites ON sites.id = product_departures.site_id";
            if (destinationFilter != null && destinationFilter.length() > 0) {
                query = DatabaseSession.addCondition(query, "destination LIKE '%" + destinationFilter + "%'");
            }
            if (product != null && product.getId() > 0) {
                query = DatabaseSession.addCondition(query, "product_id = " + product.getId());
            }
            if (site != null && site.getId() > 0) {
                query = DatabaseSession.addCondition(query, "site_id = " + site.getId());
            }
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new ProductDeparture(
                        rs.getString("id"),
                        rs.getString("product_name"),
                        rs.getString("count"),
                        rs.getString("site_name"),
                        rs.getString("destination"),
                        rs.getFloat("price"),
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
     * Ajoute un départ de produits finis à la base de données.
     * Va également mettre à jour le stock.
     *
     * @param productId   ID du produit
     * @param siteId      ID du site
     * @param count       nombre d'éléments dans le départ
     * @param destination nom de la destination
     * @param price       prix de vente
     */
    public static void insertProductDeparture(String productId, String siteId, String count, String destination, String price) {
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO product_departures (product_id, site_id, count, destination, price) VALUES (%s, %s, %s, '%s', %s)", productId, siteId, count, destination, price)
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
                final int newStock = stock - Integer.parseInt(count);
                final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                        "UPDATE product_site_stock SET count = " + newStock + " WHERE site_id = " + siteId + " AND product_id = " + productId
                );
                ps3.executeQuery();
                return;
            }
            final PreparedStatement ps3 = DatabaseSession.connection.prepareStatement(
                    String.format("INSERT INTO product_site_stock (product_id, count, site_id) VALUES (%s, %s, %s)", productId, count, siteId)
            );
            ps3.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Permet de mettre à jour un départ de produit fini.
     * Met automatiquement le stock à jour.
     *
     * @param currentValue   le départ de produit de départ
     * @param newProductId   le nouveau produit de départ
     * @param newSiteId      le nouveau site de destination
     * @param newCount       le nouveau nombre de départs
     * @param newDestination la nouvelle destination
     * @param newPrice       le nouveau prix
     */
    public static void updateProductDeparture(ProductDeparture currentValue, String newProductId, String newSiteId, String newCount, String newDestination, String newPrice) {
        newProductId = newProductId.equals("0") ? currentValue.getProductId() : newProductId;
        newSiteId = newSiteId.equals("0") ? currentValue.getSiteId() : newSiteId;
        try {
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(
                    String.format("UPDATE product_departures SET product_id = %s, site_id = %s, count = %s, destination = '%s', price = %s WHERE id = %s", newProductId, newSiteId, newCount, newDestination, newPrice, currentValue.getDbId())
            );
            ps.executeQuery();
            final PreparedStatement ps2 = DatabaseSession.connection.prepareStatement("SELECT count FROM product_site_stock WHERE product_id = " + currentValue.getProductId() + " AND site_id = " + currentValue.getSiteId());
            ResultSet rs = ps2.executeQuery();
            int stock = -1;
            while (rs.next()) {
                stock = rs.getInt("count");
            }
            if (stock > -1) {
                final int newStock = stock + Integer.parseInt(currentValue.getCount());
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
