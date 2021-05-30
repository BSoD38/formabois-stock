package formabois.stock;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Classe helper qui permet un affichage simple de messages popup
 * @since 1.0
 */
public class Popup {
    /**
     * Affiche une popup de type message.
     * @param message le message à afficher
     */
    public static void showMessage(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Message");
        window.setResizable(false);
        Label label = new Label(message);
        Button button = new Button("OK");
        button.setOnAction(e -> window.close());
        VBox layout = new VBox(15);
        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 70);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Permet de faire une popup avec un FXML au choix.
     * @param fxml le nom de la ressource FXML
     * @param title le titre de fenêtre de la popup
     */
    public static void popupFXML(String fxml, String title) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setResizable(false);
        final URL url = Popup.class.getResource(fxml);
        final FXMLLoader loader = new FXMLLoader(url);
        try {
            final Parent root = loader.load();
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            Popup.showMessage("Une erreur est survenue lors du chargement de la page:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
