package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.entities.Material;
import formabois.stock.entities.MaterialArrival;
import formabois.stock.entities.MaterialDeparture;
import formabois.stock.entities.Site;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class MaterialDeparturesController {
    @FXML
    TableView<MaterialDeparture> materialArrivalsTable;
    @FXML
    ChoiceBox<Material> materialFilter;
    @FXML
    ChoiceBox<Site> siteFilter;
    @FXML
    TextField destinationFilter;
    @FXML
    ChoiceBox<Material> materialDetail;
    @FXML
    ChoiceBox<Site> siteDetail;
    @FXML
    TextField destinationDetail;
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
    MaterialDeparture selectedRow;

    /**
     * Initialise la page.
     * Définit les éléments des listes, de la table, le comportement des boutons...
     */
    @FXML
    protected void initialize() {
        materialFilter.setItems(FXCollections.observableArrayList(DatabaseSession.materials));
        siteFilter.setItems(FXCollections.observableArrayList(DatabaseSession.sites));
        materialDetail.setItems(FXCollections.observableArrayList(DatabaseSession.materials));
        siteDetail.setItems(FXCollections.observableArrayList(DatabaseSession.sites));
        countDetail.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        materialArrivalsTable.setRowFactory(tv -> {
            TableRow<MaterialDeparture> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && !isInCreateMode.get()) {
                    selectedRow = row.getItem();
                    materialDetail.setValue(new Material(Integer.parseInt(selectedRow.getMaterialId()), selectedRow.getMaterial(), 0));
                    siteDetail.setValue(new Site(Integer.parseInt(selectedRow.getSiteId()), selectedRow.getSite()));
                    destinationDetail.setText(selectedRow.getDestination());
                    countDetail.setText(selectedRow.getCount());
                    if (DatabaseSession.isAdmin) {
                        materialDetail.setDisable(false);
                        siteDetail.setDisable(false);
                        destinationDetail.setDisable(false);
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
        final ObservableList<MaterialDeparture> list = FXCollections.observableArrayList(
                MaterialDeparture.getMaterialDepartures(destinationFilter != null ? destinationFilter.getText() : "", materialFilter.getValue(), siteFilter.getValue())
        );
        materialArrivalsTable.setItems(list);
    }

    /**
     * Cette méthode permet de créer une nouvelle entité et l'insère en base de données.
     */
    @FXML
    void createEntity() {
        MaterialDeparture.insertMaterialDeparture(Integer.toString(materialDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText(), destinationDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Fonction appelée par le bouton de mise à jour, et lance le mise à jour d'une entité.
     */
    @FXML
    void updateEntity() {
        MaterialDeparture.updateMaterialDeparture(selectedRow, Integer.toString(materialDetail.getValue().getId()), Integer.toString(siteDetail.getValue().getId()), countDetail.getText(), destinationDetail.getText());
        cancelCreate();
        updateTable();
    }

    /**
     * Annule le mode création d'entité.
     */
    @FXML
    void cancelCreate() {
        isInCreateMode.set(false);
        materialDetail.setDisable(true);
        siteDetail.setDisable(true);
        destinationDetail.setDisable(true);
        countDetail.setDisable(true);
        materialDetail.setValue(null);
        siteDetail.setValue(null);
        destinationDetail.setText("");
        countDetail.setText("");
        selectedRow = null;
    }

    /**
     * Passe le volet de droite en mode création d'entité.
     */
    @FXML
    void createMode() {
        isInCreateMode.set(true);
        materialDetail.setDisable(false);
        materialDetail.setValue(null);
        siteDetail.setDisable(false);
        siteDetail.setValue(null);
        destinationDetail.setDisable(false);
        destinationDetail.setText("");
        countDetail.setDisable(false);
        countDetail.setText("");
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
}
