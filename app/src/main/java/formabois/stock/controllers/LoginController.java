package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.Popup;
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
            Application.loadPage("arrivals_materials.fxml");
        } catch (SQLException error) {
            Popup.showMessage("Une erreur est survenue lors de la connexion.\nVeuillez vérifier vos identifiants.");
            System.out.println(error.getMessage());
            System.out.println(error.getErrorCode());
        }
    }
}
