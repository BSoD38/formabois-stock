package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.entities.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class ProductDeparturesController {
    @FXML
    TableView<ProductDeparture> materialArrivalsTable;
    @FXML
    ChoiceBox<Product> productFilter;
    @FXML
    ChoiceBox<Site> siteFilter;
    @FXML
    TextField destinationFilter;
    @FXML
    ChoiceBox<Product> productDetail;
    @FXML
    ChoiceBox<Site> siteDetail;
    @FXML
    TextField destinationDetail;
    @FXML
    TextField countDetail;
    @FXML
    TextField priceDetail;
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
    ProductDeparture selectedRow;

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
        materialArrivalsTable.setRowFactory(tv -> {
            TableRow<ProductDeparture> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && !isInCreateMode.get()) {
                    selectedRow = row.getItem();
                    productDetail.setValue(new Product(Integer.parseInt(selectedRow.getProductId()), selectedRow.getProduct(), 0));
                    siteDetail.setValue(new Site(Integer.parseInt(selectedRow.getSiteId()), selectedRow.getSite()));
                    destinationDetail.setText(selectedRow.getDestination());
                    countDetail.setText(selectedRow.getCount());
                    priceDetail.setText(Float.toString(selectedRow.getPrice()));
                    if (DatabaseSession.isAdmin) {
                        productDetail.setDisable(false);
                        siteDetail.setDisable(false);
                        destinationDetail.setDisable(false);
                        countDetail.setDisable(false);
                        priceDetail.setDisable(false);
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
        final ObservableList<ProductDeparture> list = FXCollections.observableArrayList(
                ProductDeparture.getProductDepartures(destinationFilter != null ? destinationFilter.getText() : "", productFilter.getValue(), siteFilter.getValue())
        );
        materialArrivalsTable.setItems(list);
    }

    /**
     * Cette méthode permet de créer une nouvelle entité et l'insère en base de données.
     */
    @FXML
    void createEntity() {
        ProductDeparture.insertProductDeparture(Integer.toString(productDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText(), destinationDetail.getText(), priceDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Fonction appelée par le bouton de mise à jour, et lance le mise à jour d'une entité.
     */
    @FXML
    void updateEntity() {
        ProductDeparture.updateProductDeparture(selectedRow, Integer.toString(productDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText(), destinationDetail.getText(), priceDetail.getText());
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
        destinationDetail.setDisable(true);
        countDetail.setDisable(true);
        productDetail.setValue(null);
        siteDetail.setValue(null);
        destinationDetail.setText("");
        countDetail.setText("");
        priceDetail.setDisable(true);
        priceDetail.setText("");
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
        destinationDetail.setDisable(false);
        destinationDetail.setText("");
        countDetail.setDisable(false);
        countDetail.setText("");
        priceDetail.setDisable(false);
        priceDetail.setText("");
        selectedRow = null;
    }

    /**
     * Permet de se rendre à la page des arrivages de matériaux
     */
    @FXML
    void gotoMaterialArrivals() {
        Application.loadPage("arrivals_materials.fxml");
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
     * Permet de se rendre à la page des arrivages de produits
     */
    @FXML
    void gotoProductArrivals() {
        Application.loadPage("arrivals_products.fxml");
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
}
