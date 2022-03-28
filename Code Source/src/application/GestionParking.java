package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionParking {
	
	int rNumPar; 
	
	@FXML
	private TextField nParking;
	@FXML
	private TextField nomParking;
	@FXML
	private TextField capaciteParking;
	@FXML
	private TextField rue;
	@FXML
	private TextField arrondissement;
	@FXML
	private TextField rechercher;
	@FXML
	private ComboBox <String> comboVehicule;
	@FXML
	private ComboBox <String> comboParking;
	@FXML
	private Label placesVides;
	
	// Table d'affichage Vehicules par parking
	@FXML
	private TableView<Vehicule> tableView;
	@FXML
	private TableColumn<Vehicule,Integer> TableImmatriculation;
	@FXML
	private TableColumn<Vehicule,String> TableMarque;
	@FXML
	private TableColumn<Vehicule,String> TableType;
	@FXML
	private TableColumn<Vehicule,String> TableCarburant;
	@FXML
	private TableColumn<Vehicule,Integer> TableCompteurKM;
	@FXML
	private TableColumn<Vehicule,LocalDate> TableDateMiseCirculation;
		
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Charger l'interface "Ajouter Un Parking" ==================*/
	public void interfaceAjouterParking(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterParking.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Liste des parkings dans le ComboBox =====================*/
	public void ajouterParkings() throws SQLException {
		String Parking = "SELECT nom FROM `parking`;";
		Connection vC = Login.connectDB();
		PreparedStatement vPS = vC.prepareStatement(Parking);
		ResultSet resultSet = vPS.executeQuery();
        while (resultSet.next()){  
            comboParking.getItems().addAll(resultSet.getString(1)); 
        }
	}

	/*===================== Bouton d'ajouter le parking =====================*/
	public void ajouterParking(ActionEvent e) throws IOException, SQLException {
		if(!nParking.getText().equals("") && !nomParking.getText().equals("") && !capaciteParking.getText().equals("") 
				&& !rue.getText().equals("")  && !arrondissement.getText().equals("") ) 
		{
			String pSql = "SELECT * FROM `parking` WHERE nParking='"+nParking.getText()+"';";
			Connection pC = Login.connectDB();
			PreparedStatement pPS = pC.prepareStatement(pSql);
			ResultSet pResult = pPS.executeQuery();
			if(pResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un parking avec le même N° Parking");
				alert.show();
			} 
			else {
				
				int nPar = Integer.parseInt(nParking.getText());
				int capacitePar = Integer.parseInt(capaciteParking.getText());
				
				Parking parking = new Parking(nPar, nomParking.getText(), capacitePar, rue.getText(), arrondissement.getText());
				String sql = "INSERT INTO `parking`(`nParking`, `nom`, `capacite`, `rue`, `arrondissement`) VALUES (?,?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setInt(1, parking.getnParking());
				ps.setString(2, parking.getNom());
				ps.setInt(3, parking.getCapacite());
				ps.setString(4, parking.getRue());
				ps.setString(5, parking.getArrondissement());				
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
	
	/*===================== Charger l'interface "Modifier/Supprimer Un Parking" ==================*/
	public void interfaceModifierParking(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierParking.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}

	/*===================== Bouton de rechercher un parking par num =====================*/
	public void rechercherParking() throws SQLException {
		rNumPar = Integer.parseInt(rechercher.getText());
		String sql = "SELECT * FROM parking WHERE nParking='"+ rNumPar +"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			nParking.setText(result.getString(1));
			nomParking.setText(result.getString(2));
			capaciteParking.setText(result.getString(3));
			rue.setText(result.getString(4));
			arrondissement.setText(result.getString(5));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun Parking avec ce N° Parking");
			alert.show();
		}
		C.close();
	}
	
	/*===================== Bouton de modifier le parking =====================*/
	public void modifierParking(ActionEvent e) throws IOException, SQLException {
		if(!nParking.getText().equals("") && !nomParking.getText().equals("") && !capaciteParking.getText().equals("") 
				&& !rue.getText().equals("")  && !arrondissement.getText().equals("") )
		{
			int numPar= Integer.parseInt(nParking.getText());
			int capacitePar = Integer.parseInt(capaciteParking.getText());
			
			Parking parking = new Parking(numPar, nomParking.getText(), capacitePar , rue.getText(), arrondissement.getText());
			String sql = "SELECT `nParking` FROM `parking` WHERE nParking='"+numPar+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && parking.getnParking()!=rNumPar) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un parking avec le même N° Immatriculation");
				alert.show();
			}
			else {
				String sql2 = "UPDATE `parking` SET `nParking`=? ,`nom`=? ,`capacite`=? ,`rue`=? ,`arrondissement`=? WHERE nParking=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setInt(1, parking.getnParking());
				ps2.setString(2, parking.getNom());
				ps2.setInt(3, parking.getCapacite());
				ps2.setString(4, parking.getRue());
				ps2.setString(5, parking.getArrondissement());	
				ps2.setInt(6, rNumPar);
				ps2.executeUpdate();
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
	
	/*===================== Bouton de supprimer le parking =====================*/
	public void supprimerParking(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `parking` WHERE nParking=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setInt(1, rNumPar);
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("Le parking a été supprimé");
		alert.show();
	}
	
	/*===================== Charger l'interface "Déposer un véhicule dans un parking" ==================*/
	public void interfaceDeposerVehicule(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("GUI/DeposerVehicule.fxml"));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger l'interface "Le nombre de places vides par parking" ==================*/
	public void interfaceNombrePlacesVides(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/NombrePlacesVides.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Calculer le nombre de places vides par le nom d'un parking =====================*/
	public int nombrePlacesVides(String nom) throws SQLException {
		String npSQL = "SELECT capacite FROM parking WHERE nom=?;";
		Connection C = Login.connectDB();
		PreparedStatement npPS = C.prepareStatement(npSQL);
		npPS.setString(1, nom);
		ResultSet result = npPS.executeQuery();
		result.next();
		int capacite = result.getInt(1);
		String vSQL = "SELECT * FROM vehicule WHERE parking=?;";
		PreparedStatement vPS = C.prepareStatement(vSQL);
		vPS.setString(1, nom);
		ResultSet vResult = vPS.executeQuery();
		int nVehicules = 0;
		while(vResult.next()) {
			nVehicules++;
		}
		return capacite-nVehicules;
	}
	
	/*===================== Afficher le nombre de place vides d'un parking =====================*/
	public void afficherNombrePlacesVides() throws SQLException {
		int n = nombrePlacesVides(comboParking.getValue());
		placesVides.setText("Le nombre de places vides dans ce parking: "+n);
	}
	
	/*===================== Liste des vehicules a deposer =====================*/
	public void vehiculeADeposer() throws SQLException {
		String sql = "SELECT numImmatriculation FROM vehicule WHERE parking='Aucun';";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		while (resultSet.next())
        {  
            comboVehicule.getItems().addAll(resultSet.getString(1));
        }
	}
	
	/*===================== Liste des parking dans le combox =====================*/
	public void parkingChoisi() throws SQLException {
		String sql = "SELECT nom FROM parking;";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		while (resultSet.next())
        {  
            comboParking.getItems().add(resultSet.getString(1));
        }
	}
	
	/*===================== le button de deposer un vehicule =====================*/
	public void deposerVehicule(ActionEvent e) throws SQLException, IOException {
		if(comboVehicule.getValue()==null || comboParking.getValue()==null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Veuillez remplir tous les champs");
			alert.show();
		}
		else if(nombrePlacesVides(comboParking.getValue())<1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Le parking est plein");
			alert.show();
		}
		else {
			String sql = "UPDATE vehicule SET parking=? where numImmatriculation=?;";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ps.setString(1, comboParking.getValue());
			ps.setInt(2, Integer.parseInt(comboVehicule.getValue()));
			ps.executeUpdate();
			C.close();
			retourGestion();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Succès");
			alert.setHeaderText("Les données ont été enregistrées");
			alert.show();
		}
	}
	
	/*===================== Charger l'interface "Faire sortir un véhicule d’un parking" ==================*/
	public void interfaceSortirVehicule(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("GUI/SortirVehicule.fxml"));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Liste des vehicules a sortir =====================*/
	public void vehiculeASortir() throws SQLException {
		String sql = "SELECT numImmatriculation FROM vehicule WHERE parking!='Aucun';";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		while (resultSet.next())
        {  
            comboVehicule.getItems().addAll(resultSet.getString(1));
        }
	}
	
	/*===================== le button de sortir un vehicule =====================*/
	public void sortirVehicule(ActionEvent e) throws SQLException, IOException {
		if(comboVehicule.getValue()!=null) {
			String sql = "UPDATE vehicule SET parking='Aucun' where numImmatriculation=?;";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(comboVehicule.getValue()));
			ps.executeUpdate();
			C.close();
			retourGestion();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Succès");
			alert.setHeaderText("Les données ont été enregistrées");
			alert.show();
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Veuillez remplir tous les champs");
			alert.show();
		}
	}

	/*===================== Charger l'interface "Afficher les informations d'un parking" ==================*/
	public void interfaceInfosParking(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosParking.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger l'interface "La liste de véhicules par parking" ==================*/
	public void interfaceVehiculeParking(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ListeVehiculeParking.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Afficher les informations du parking choisi =====================*/
	public void selectionner(ActionEvent e) throws SQLException {
		String search = comboParking.getValue();
		ObservableList<Vehicule> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM `vehicule` WHERE `parking` ='"+search+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()) {
			Vehicule vehicule = new Vehicule();
			vehicule.setNumImmatriculation(result.getInt(1));
			vehicule.setMarque(result.getString(2));
			vehicule.setType(result.getString(3));
			vehicule.setCarburant(result.getString(4));
			vehicule.setCompteurKM(result.getInt(5));
			vehicule.setDateMiseCirculation(result.getDate(6).toLocalDate());
			data.add(vehicule);
			}
		TableImmatriculation.setCellValueFactory(new PropertyValueFactory<Vehicule,Integer>("numImmatriculation"));
		TableMarque.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("marque"));
		TableType.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("Type"));
		TableCarburant.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("carburant"));
		TableCompteurKM.setCellValueFactory(new PropertyValueFactory<Vehicule,Integer>("compteurKM"));
		TableDateMiseCirculation.setCellValueFactory(new PropertyValueFactory<Vehicule,LocalDate>("dateMiseCirculation"));
		tableView.setItems(data);
		C.close();
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionParking.fxml")));
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
