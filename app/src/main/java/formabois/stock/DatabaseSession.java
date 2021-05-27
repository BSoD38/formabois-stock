package formabois.stock;

import formabois.stock.entities.Material;
import formabois.stock.entities.Site;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Cette classe permet de faciliter la connexion à la base de données. Les entités s'en servent directement.
 *
 * @since 1.0
 */
public class DatabaseSession {
    public static Connection connection = null;
    public static boolean isAdmin = false;
    public static String currentUser = null;
    public static ArrayList<Material> materials;
    public static ArrayList<Site> sites;

    /**
     * Lance la connexion à la base de données. L'instance de la connexion sera stockée statiquement dans la classe.
     * Cette méthode va également stocker de manière statique le nom de l'utilisateur et son niveau de privilèges.
     * L'addresse et le nom de la base de données sont retrouvés depuis un fichier de configuration.
     *
     * @param username Nom d'utilisateur de la base de données
     * @param password Mot de passe de l'utilisateur de la base de données
     * @throws SQLException Levé lors d'une erreur de connexion.
     * @since 1.0
     */
    static public void connect(String username, String password) throws SQLException {
        if (connection != null) {
            connection.close();
        }
        try (InputStream input = new FileInputStream("./config.properties")) {
            final Properties dbProperties = new Properties();
            dbProperties.load(input);
            connection = DriverManager.getConnection(String.format(
                    "jdbc:mariadb://%s/%s?user=%s&password=%s",
                    dbProperties.getProperty("dbAddress"),
                    dbProperties.getProperty("dbName"),
                    username,
                    password
            ));
            currentUser = username;
            final PreparedStatement ps = connection.prepareStatement(String.format("SHOW GRANTS FOR '%s'@'localhost'", username));
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString(1).contains("INSERT")) {
                    isAdmin = true;
                    rs.close();
                    break;
                }
            }
            refreshMaterials();
            refreshSites();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void refreshMaterials() {
        materials = Material.getMaterials();
        materials.add(0, new Material(0, "", 0));
    }

    static public void refreshSites() {
        sites = Site.getSites();
        sites.add(0, new Site(0, ""));
    }

    /**
     * Fonction helper qui ajoute une condition à une requête SQL.
     * Gère automatiquement l'ajout des WHERE et AND.
     *
     * @param query     la requête à laquelle on veut ajouter la condition
     * @param condition la condition qu'on l'on veut ajouter à la requête
     * @return la requête SQL avec la condition ajoutée
     */
    static public String addCondition(String query, String condition) {
        if (query.contains("WHERE")) {
            return query + " AND " + condition;
        }
        return query + " WHERE " + condition;
    }
}
