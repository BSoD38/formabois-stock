package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.Popup;
import formabois.stock.entities.Product;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class ProductController {
    @FXML
    TableView<Product> productTable;
    @FXML
    TextField nameFilter;
    @FXML
    TextField nameDetail;
    @FXML
    TextField thresholdDetail;
    @FXML
    Button editButton;
    @FXML
    Button addModeButton;
    @FXML
    Button cancelCreateButton;
    @FXML
    Button createButton;
    /**
     * Si un administrateur passe en mode création d'entité, cette variable sera vraie.
     */
    final SimpleBooleanProperty isInCreateMode = new SimpleBooleanProperty(false);
    Product selectedRow;

    /**
     * Initialise la page.
     * Définit les éléments des listes, de la table, le comportement des boutons...
     */
    @FXML
    protected void initialize() {
        thresholdDetail.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        productTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && !isInCreateMode.get()) {
                    selectedRow = row.getItem();
                    nameDetail.setText(selectedRow.getName());
                    thresholdDetail.setText(Integer.toString(selectedRow.getThreshold()));
                    if (DatabaseSession.isAdmin) {
                        nameDetail.setDisable(false);
                        thresholdDetail.setDisable(false);
                        editButton.setDisable(false);
                    }
                }
            });
            return row;
        });
        if (DatabaseSession.isAdmin) {
            cancelCreateButton.managedProperty().bind(isInCreateMode);
            createButton.managedProperty().bind(isInCreateMode);
            editButton.managedProperty().bind(isInCreateMode.not());
            addModeButton.managedProperty().bind(isInCreateMode.not());
            cancelCreateButton.visibleProperty().bind(isInCreateMode);
            createButton.visibleProperty().bind(isInCreateMode);
            editButton.visibleProperty().bind(isInCreateMode.not());
            addModeButton.visibleProperty().bind(isInCreateMode.not());
        }
        updateTable();
    }

    /**
     * Cette méthode va faire une requête à la base de données et mettre à jour la table d'éléments.
     */
    @FXML
    void updateTable() {
        DatabaseSession.refreshProducts();
        final ObservableList<Product> list = FXCollections.observableArrayList(DatabaseSession.products.subList(1, DatabaseSession.products.size()));
        productTable.setItems(list);
    }

    /**
     * Cette méthode permet de créer une nouvelle entité et l'insère en base de données.
     */
    @FXML
    void createEntity() {
        Product.insertProducts(nameDetail.getText(), thresholdDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Fonction appelée par le bouton de mise à jour, et lance le mise à jour d'une entité.
     */
    @FXML
    void updateEntity() {
        Product.updateProducts(selectedRow, nameDetail.getText(), thresholdDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Annule le mode création d'entité.
     */
    @FXML
    void cancelCreate() {
        isInCreateMode.set(false);
        nameDetail.setDisable(true);
        thresholdDetail.setDisable(true);
        nameDetail.setText("");
        thresholdDetail.setText("");
        selectedRow = null;
    }

    /**
     * Passe le volet de droite en mode création d'entité.
     */
    @FXML
    void createMode() {
        isInCreateMode.set(true);
        nameDetail.setDisable(false);
        nameDetail.setText("");
        thresholdDetail.setDisable(false);
        thresholdDetail.setText("");
        selectedRow = null;
    }

    /**
     * Permet de se rendre à la page des départs de matériaux
     */
    @FXML
    void gotoMaterialDepartures() {
        Application.loadPage("departures_materials.fxml");
    }

    /**
     * Permet de se rendre à la page des stocks de matériaux
     */
    @FXML
    void gotoMaterialStocks() {
        Application.loadPage("stock_materials.fxml");
    }

    /**
     * Permet de se rendre à la page des arrivages de matériaux
     */
    @FXML
    void gotoMaterialArrivals() {
        Application.loadPage("arrivals_materials.fxml");
    }

    /**
     * Permet de se rendre à la page des arrivages de produits
     */
    @FXML
    void gotoProductArrivals() {
        Application.loadPage("arrivals_products.fxml");
    }


    /**
     * Permet de se rendre à la page des départs de produits
     */
    @FXML
    void gotoProductDepartures() {
        Application.loadPage("departures_products.fxml");
    }

    /**
     * Permet de se rendre à la page des stocks de produits
     */
    @FXML
    void gotoProductStocks() {
        Application.loadPage("stock_products.fxml");
    }

    /**
     * Permet de se rendre à la page de gestion de produits
     */
    @FXML
    void gotoProducts() {
        Application.loadPage("products.fxml");
    }

    /**
     * Permet d'ouvrir la page de statistiques
     */
    @FXML
    void gotoStats() {
        Popup.popupFXML("stats.fxml", "Statistiques");
    }
}
