package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.Popup;
import formabois.stock.entities.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class ProductArrivalsController {
    @FXML
    TableView<ProductArrival> productArrivalsTable;
    @FXML
    ChoiceBox<Product> productFilter;
    @FXML
    ChoiceBox<Site> siteFilter;
    @FXML
    ChoiceBox<Product> productDetail;
    @FXML
    ChoiceBox<Site> siteDetail;
    @FXML
    TextField countDetail;
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
    ProductArrival selectedRow;

    /**
     * Initialise la page.
     * Définit les éléments des listes, de la table, le comportement des boutons...
     */
    @FXML
    protected void initialize() {
        productFilter.setItems(FXCollections.observableArrayList(DatabaseSession.products));
        siteFilter.setItems(FXCollections.observableArrayList(DatabaseSession.sites));
        productDetail.setItems(FXCollections.observableArrayList(DatabaseSession.products));
        siteDetail.setItems(FXCollections.observableArrayList(DatabaseSession.sites));
        countDetail.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        productArrivalsTable.setRowFactory(tv -> {
            TableRow<ProductArrival> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && !isInCreateMode.get()) {
                    selectedRow = row.getItem();
                    productDetail.setValue(new Product(Integer.parseInt(selectedRow.getProductId()), selectedRow.getProduct(), 0));
                    siteDetail.setValue(new Site(Integer.parseInt(selectedRow.getSiteId()), selectedRow.getSite()));
                    countDetail.setText(selectedRow.getCount());
                    if (DatabaseSession.isAdmin) {
                        productDetail.setDisable(false);
                        siteDetail.setDisable(false);
                        countDetail.setDisable(false);
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
        final ObservableList<ProductArrival> list = FXCollections.observableArrayList(
                ProductArrival.getProductArrivals(productFilter.getValue(), siteFilter.getValue())
        );
        productArrivalsTable.setItems(list);
    }

    /**
     * Cette méthode permet de créer une nouvelle entité et l'insère en base de données.
     */
    @FXML
    void createEntity() {
        ProductArrival.insertProductArrival(Integer.toString(productDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Fonction appelée par le bouton de mise à jour, et lance le mise à jour d'une entité.
     */
    @FXML
    void updateEntity() {
        ProductArrival.updateProductArrival(selectedRow, Integer.toString(productDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Annule le mode création d'entité.
     */
    @FXML
    void cancelCreate() {
        isInCreateMode.set(false);
        productDetail.setDisable(true);
        siteDetail.setDisable(true);
        countDetail.setDisable(true);
        productDetail.setValue(null);
        siteDetail.setValue(null);
        countDetail.setText("");
        selectedRow = null;
    }

    /**
     * Passe le volet de droite en mode création d'entité.
     */
    @FXML
    void createMode() {
        isInCreateMode.set(true);
        productDetail.setDisable(false);
        productDetail.setValue(null);
        siteDetail.setDisable(false);
        siteDetail.setValue(null);
        countDetail.setDisable(false);
        countDetail.setText("");
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
     * Permet de se rendre à la page de gestion des matériaux
     */
    @FXML
    void gotoMaterials() { Application.loadPage("materials.fxml"); }

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
