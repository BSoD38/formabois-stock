package formabois.stock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Racine de l'application. Fournit quelques méthodes utiles.
 */
public class Application extends javafx.application.Application {
    final String configFilename = "./config.properties";
    static Stage stage = null;

    /**
     * Point d'entrée de l'application JavaFX.
     *
     * @param primaryStage le stage de l'application, fourni nativement par JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        if (!new File(configFilename).isFile()) {
            try (OutputStream output = new FileOutputStream(configFilename)) {
                Properties dbProperties = new Properties();
                dbProperties.setProperty("dbAddress", "localhost:3306");
                dbProperties.setProperty("dbName", "formabois");
                dbProperties.store(output, null);
                Popup.showMessage("Un fichier de configuration par defaut a ete cree.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        stage = primaryStage;
        AnchorPane pane = new AnchorPane();
        final Scene scene = new Scene(pane, 1024, 768);
        stage.setTitle("Formabois Stock Manager");
        stage.setScene(scene);
        loadPage("login.fxml");
        stage.show();
    }

    /**
     * Permet de charger une nouvelle page dans la scène de l'application.
     *
     * @param resourceName Le chemin de la ressource FXML.
     */
    public static void loadPage(String resourceName) {
        final URL url = Application.class.getResource(resourceName);
        final FXMLLoader loader = new FXMLLoader(url);
        try {
            final Parent root = loader.load();
            Application.stage.getScene().setRoot(root);
        } catch (IOException e) {
            Popup.showMessage("Une erreur est survenue lors du chargement de la page:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void startup() {
        launch();
    }
}
