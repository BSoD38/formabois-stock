package formabois.stock.controllers;

import formabois.stock.Application;
import formabois.stock.DatabaseSession;
import formabois.stock.Popup;
import formabois.stock.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;

public class ProductStockController {
    @FXML
    TableView<ProductStock> productStocksTable;
    @FXML
    ChoiceBox<Product> productFilter;
    @FXML
    ChoiceBox<Site> siteFilter;

    /**
     * Initialise la page.
     * Définit les éléments des listes, de la table, le comportement des boutons...
     */
    @FXML
    protected void initialize() {
        productFilter.setItems(FXCollections.observableArrayList(DatabaseSession.products));
        siteFilter.setItems(FXCollections.observableArrayList(DatabaseSession.sites));
        updateTable();
    }

    /**
     * Cette méthode va faire une requête à la base de données et mettre à jour la table d'éléments.
     */
    @FXML
    void updateTable() {
        final ObservableList<ProductStock> list = FXCollections.observableArrayList(
                ProductStock.getProductStocks(productFilter.getValue(), siteFilter.getValue())
        );
        productStocksTable.setItems(list);
    }

    /**
     * Permet de se rendre à la page des arrivages de matériaux
     */
    @FXML
    void gotoMaterialArrivals() {
        Application.loadPage("arrivals_materials.fxml");
    }

    /**
     * Permet de se rendre à la page des départs de matériaux
     */
    @FXML
    void gotoMaterialDepartures() {
        Application.loadPage("departures_materials.fxml");
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
