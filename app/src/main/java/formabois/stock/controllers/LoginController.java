package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.Popup;
import formabois.stock.entities.MaterialStock;
import formabois.stock.entities.ProductStock;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

/**
 * Contrôleur de la page de connexion
 */
public class LoginController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;

    /**
     * Gère le bouton de connexion
     *
     * @param e évènement du clic
     */
    @FXML
    public void handleLogin(Event e) {
        try {
            DatabaseSession.connect(username.getText(), password.getText());
            StringBuilder materialAlertText = new StringBuilder();
            for (MaterialStock materialStock : MaterialStock.getLowStock()) {
                materialAlertText.append(String.format("\n%s - %s - %s restant(s)", materialStock.getSite(), materialStock.getMaterial(), materialStock.getCount()));
            }
            if(!materialAlertText.isEmpty()) {
                Popup.showMessage("Stocks faibles pour les materiaux suivants :" + materialAlertText);
            }
            StringBuilder productAlertText = new StringBuilder();
            for (ProductStock productStock : ProductStock.getLowStock()) {
                productAlertText.append(String.format("\n%s - %s - %s restant(s)", productStock.getSite(), productStock.getProduct(), productStock.getCount()));
            }
            if(!productAlertText.isEmpty()) {
                Popup.showMessage("Stocks faibles pour les produits suivants :" + productAlertText);
            }
            Application.loadPage("arrivals_materials.fxml");
        } catch (SQLException error) {
            Popup.showMessage("Une erreur est survenue lors de la connexion.\nVeuillez vérifier vos identifiants.");
            System.out.println(error.getMessage());
            System.out.println(error.getErrorCode());
        }
    }
}
