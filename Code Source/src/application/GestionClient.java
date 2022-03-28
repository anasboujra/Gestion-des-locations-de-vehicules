package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GestionClient {

	FileChooser fileChooser = new FileChooser();	
	File selectedFile;
	InputStream is;
	String rNomComplet, rCodeClient;

	@FXML
	private TextField rechercher;
	@FXML
	private TextField codeClient;
	@FXML
	private TextField nomComplet;
	@FXML
	private TextField adresse;
	@FXML
	private TextField nGSM;
	@FXML
	private ImageView image;
	@FXML
	private Button selectionner;
	
	// Table d'affichage
	@FXML
	private TableView<Client> tableview;
	@FXML
	private TableColumn<Client,String> code;
	@FXML
	private TableColumn<Client,String> nom;
	@FXML
	private TableColumn<Client,String> adresseClient;
	@FXML
	private TableColumn<Client,Integer> numero;

	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Charger l'interface "Ajouter un client" =====================*/
	public void interfaceAjouterClient(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterClient.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton d'ajouter le client =====================*/
	public void ajouterClient(ActionEvent e) throws IOException, SQLException {
		if(is!=null && !codeClient.getText().equals("") && !nomComplet.getText().equals("") && !adresse.getText().equals("") && !nGSM.getText().equals("")  ) 
		{
			String cSql = "SELECT * FROM `client` WHERE codeClient='"+codeClient.getText()+"';";
			Connection cC = Login.connectDB();
			PreparedStatement cPS = cC.prepareStatement(cSql);
			ResultSet clientResult = cPS.executeQuery();
			if(clientResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un client avec le même CIN");
				alert.show();
			} 
			else {
				String GSM = nGSM.getText();
				int nGSM = Integer.parseInt(GSM);
				Client client = new Client(codeClient.getText(), nomComplet.getText(), adresse.getText(), nGSM, is);
				String sql = "INSERT INTO client(`codeClient`, `nomComplet`, `adresse`, `GSM`, `permisConduire` ) VALUES (?,?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setString(1, client.getCodeClient());
				ps.setString(2, client.getNomComplet());
				ps.setString(3, client.getAdresse());
				ps.setInt(4, client.getNGSM());
				ps.setBlob(5, client.getPermisConduire());
				ps.executeUpdate();
				C.close();
				retourGestion();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Succès");
				alert.setHeaderText("Les données ont été enregistrées");
				alert.show();
			}
		}
		
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Veuillez remplir tous les champs");
			alert.show();
		}
	}
	
	/*===================== Charger l'interface "Modifier/Supprimer un client" =====================*/
	public void interfaceModifierClient(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierClient.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher un client par nom (modifier/suppimer)=====================*/
	public void rechercherClient() throws SQLException {
		rNomComplet = rechercher.getText();
		String sql = "SELECT * FROM client WHERE nomComplet='"+rNomComplet+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			rCodeClient = result.getString(1);
			codeClient.setText(result.getString(1));
			nomComplet.setText(result.getString(2));
			adresse.setText(result.getString(3));
			nGSM.setText(result.getString(4));
			Image imagePermis = new Image(result.getBinaryStream("permisConduire"));
			image.setImage(imagePermis); 
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun Client avec ce Nom");
			alert.show();
		}
		C.close();
	}
	
	/*===================== Bouton de modifier les informations de le client =====================*/
	public void modifierClient(ActionEvent e) throws IOException, SQLException {
		if(!codeClient.getText().equals("") && !nomComplet.getText().equals("") && !adresse.getText().equals("") 
				&& !nGSM.getText().equals("")) {
			
			String GSM = nGSM.getText();
			int nGSM = Integer.parseInt(GSM);
			Client client = new Client(codeClient.getText(), nomComplet.getText(), adresse.getText(), nGSM,is);
			String sql = "SELECT `codeClient` FROM `client` WHERE codeClient='"+client.getCodeClient()+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && !client.getCodeClient().equalsIgnoreCase(rCodeClient)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un client avec le même Code Client");
				alert.show();
			}
			else {
				String sql3 = "SELECT `nomComplet` FROM `client` WHERE nomComplet='"+client.getNomComplet()+"';";
				Connection C3 = Login.connectDB();
				PreparedStatement ps3 = C3.prepareStatement(sql3);
				ResultSet result3 = ps3.executeQuery();
				if(result3.next() && !client.getNomComplet().equalsIgnoreCase(rNomComplet)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText("Il y'a déjà un client avec le même Nom Complet");
					alert.show();
				}
				else {
					if(is != null) {
						String sql2 = "UPDATE `client` SET `codeClient`=?,`nomComplet`=?,`adresse`=?,`GSM`=?,`permisConduire`=?  WHERE nomComplet=?;";
						Connection C2 = Login.connectDB();
						PreparedStatement ps2 = C2.prepareStatement(sql2);
						ps2.setString(1, client.getCodeClient());
						ps2.setString(2, client.getNomComplet());
						ps2.setString(3, client.getAdresse());
						ps2.setInt(4, client.getNGSM());
						ps2.setBlob(5, client.getPermisConduire());
						ps2.setString(6, rNomComplet);
						ps2.executeUpdate();
					}
					else{
						String sql2 = "UPDATE `client` SET `codeClient`=?,`nomComplet`=?,`adresse`=?,`GSM`=?  WHERE nomComplet=?;";
						Connection C2 = Login.connectDB();
						PreparedStatement ps2 = C2.prepareStatement(sql2);
						ps2.setString(1, client.getCodeClient());
						ps2.setString(2, client.getNomComplet());
						ps2.setString(3, client.getAdresse());
						ps2.setInt(4, client.getNGSM());
						ps2.setString(5, rNomComplet);
						ps2.executeUpdate();
					}
					retourGestion();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Succès");
					alert.setHeaderText("Les données ont été enregistrées");
					alert.show();
				}
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Veuillez remplir tous les champs");
			alert.show();
		}
	}
	
	/*===================== Bouton de supprimer le client =====================*/
	public void supprimerClient(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `client` WHERE nomComplet=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setString(1, rNomComplet);
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("Le client a été supprimé");
		alert.show();
	}
	
	/*===================== Charger l'interface "informations d'un client" =====================*/
	public void interfaceInfosClient(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosClient.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger l'interface "informations des clients par ordre alphabetique" =====================*/
	public void interfaceClientsAlpha(ActionEvent e) throws IOException, SQLException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ClientsAlpha.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "informations des clients par ordre alphabetique" =====================*/
	public void actualiser(ActionEvent e) throws SQLException {
		ObservableList<Client> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM client ORDER BY codeClient;";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next())
			{
			Client client = new Client();
				client.setCodeClient(result.getString(1));
				client.setNomComplet(result.getString(2));
				client.setAdresse(result.getString(3));
				client.setNGSM(result.getInt (4));
				data.add(client);
			}
				code.setCellValueFactory(new PropertyValueFactory<Client,String>("codeClient"));
				nom.setCellValueFactory(new PropertyValueFactory<Client,String>("nomComplet"));
				adresseClient.setCellValueFactory(new PropertyValueFactory<Client,String>("Adresse"));
				numero.setCellValueFactory(new PropertyValueFactory<Client,Integer>("nGSM"));
				tableview.setItems(data);
		C.close();
	}
	
	
	
	/*===================== Bouton de charger l'image du client =====================*/
	public void browse(ActionEvent e) throws FileNotFoundException{
		fileChooser.getExtensionFilters().add(new ExtensionFilter("IMAGES", "*.JPEG", "*.JPG", "*.PNG", "*.GIF", "*.TIFF"));
		selectedFile = fileChooser.showOpenDialog(null);
    	if(selectedFile != null) {
    		is = new FileInputStream(selectedFile);
        	selectionner.setText(selectedFile.getName());
        	if(image != null) {
            	FileInputStream cadre = new FileInputStream(selectedFile);
        		Image imagePermis = new Image(cadre);
    			image.setImage(imagePermis); 
        	}
    	}
    	
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionClient.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de retour au menu principal =====================*/
	public void retourMenu() throws IOException {
		Parent root;
		if(Main.ad) {
			root = FXMLLoader.load(getClass().getResource(("GUI/AdminPanel.fxml")));
		}
		else {
			root = FXMLLoader.load(getClass().getResource(("GUI/UserPanel.fxml")));
		}
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();

	}
}
