package formabois.stock.entities;

import formabois.stock.DatabaseSession;
import javafx.beans.property.SimpleStringProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Cette classe représente une entrée en base de donnée du stock d'un produit fini pour un site donné.
 */
public class ProductStock {
    private final SimpleStringProperty product = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    private final SimpleStringProperty site = new SimpleStringProperty("");
    private String materialId;
    private String siteId;

    public ProductStock() {
        this("", "", "", "", "");
    }

    public ProductStock(String product, String site, String count, String materialId, String siteId) {
        setProduct(product);
        setSite(site);
        setCount(count);
        setMaterialId(materialId);
        setSiteId(siteId);
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
     * Fait une requête des stocks de produits finis à la base de données. Peut filtrer avec les arguments.
     *
     * @param product filtre sur le produit, si ID=0, aucun filtre.
     * @param site     filtre sur le site, si ID=0, aucun filtre.
     * @return La liste des stocks de matériaux bruts.
     */
    public static ArrayList<ProductStock> getProductStocks(Product product, Site site) {
        final ArrayList<ProductStock> array = new ArrayList<>();
        try {
            String query = "SELECT products.name AS product_name, sites.name AS site_name, count, product_id, site_id FROM product_site_stock " +
                    "INNER JOIN products ON products.id = product_site_stock.product_id " +
                    "INNER JOIN sites ON sites.id = product_site_stock.site_id";
            if (product != null && product.getId() > 0) {
                query = DatabaseSession.addCondition(query, "product_id = " + product.getId());
            }
            if (site != null && site.getId() > 0) {
                query = DatabaseSession.addCondition(query, "site_id = " + site.getId());
            }
            final PreparedStatement ps = DatabaseSession.connection.prepareStatement(query);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                array.add(new ProductStock(
                        rs.getString("product_name"),
                        rs.getString("site_name"),
                        rs.getString("count"),
                        rs.getString("product_id"),
                        rs.getString("site_id")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }
}
