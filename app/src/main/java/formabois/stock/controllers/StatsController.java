package formabois.stock.controllers;

import formabois.stock.DatabaseSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsController {
    @FXML
    Label soldProductsNumber;
    @FXML
    Label mostSoldProduct;

    /**
     * Initialise la page.
     * Charge les éléments des statistiques.
     */
    @FXML
    protected void initialize() {
        try {
            PreparedStatement psSoldProducts = DatabaseSession.connection.prepareStatement("SELECT SUM(count) FROM product_departures");
            ResultSet rsSoldProducts = psSoldProducts.executeQuery();
            while (rsSoldProducts.next()) {
                soldProductsNumber.setText(rsSoldProducts.getString(1));
            }
            PreparedStatement psMostSoldProduct = DatabaseSession.connection.prepareStatement(
                        "SELECT r.product_name, r.site_name, MAX(r.total) total_max FROM " +
                            "(SELECT p.name product_name, s.name site_name, SUM(count) total FROM product_departures " +
                            "INNER JOIN products p on product_departures.product_id = p.id " +
                            "INNER JOIN sites s on product_departures.site_id = s.id " +
                            "GROUP BY site_id, product_id) r "
            );
            ResultSet rsMostSoldProduct = psMostSoldProduct.executeQuery();
            while (rsMostSoldProduct.next()) {
                mostSoldProduct.setText(
                        String.format("%s - %s - %s", rsMostSoldProduct.getString("total_max"), rsMostSoldProduct.getString("product_name"), rsMostSoldProduct.getString("site_name"))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Ferme la page lors du clic du bouton OK.
     */
    @FXML
    private void onPressOK() {
        ((Stage) soldProductsNumber.getScene().getWindow()).close();
    }
}
