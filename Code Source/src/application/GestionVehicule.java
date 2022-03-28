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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;

public class GestionVehicule {

	int rNumImm;

	@FXML
	private TextField numImmatriculation;
	@FXML
	private ComboBox <String> comboMarque;
	@FXML
	private TextField type;
	@FXML
	private ComboBox <String> comboCarburant;
	@FXML
	private TextField compteurKM;
	@FXML
	private DatePicker dateMiseCirculation;
	@FXML
	private TextField rechercher;
	@FXML
	private TextField infoMarque;
	@FXML
	private TextField infoCarburant;
	@FXML
	private TextField infoMise;

	// Table d'affichage
	@FXML
	private TableView<Vehicule> tableview;
	@FXML
	private TableColumn<Vehicule,Integer> tableImmatriculation;
	@FXML
	private TableColumn<Vehicule,String> tableMarque;
	@FXML
	private TableColumn<Vehicule,String> tableType;
	@FXML
	private TableColumn<Vehicule,String> tableCarburant;
	@FXML
	private TableColumn<Vehicule,Integer> tableCompteurKM;
	@FXML
	private TableColumn<Vehicule,LocalDate> tableDateMiseCirculation;
	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Liste des marques dans le ComboBox =====================*/
	public void  ajouterMarque() {
		comboMarque.getItems().removeAll(comboMarque.getItems());
		comboMarque.getItems().addAll("Audi","BMW","Citroën","Dacia","Ford","Hyundai","Mercedes","Nissan","Opel","Peugeot","Renault","Volkswagen");
	}
	
	/*===================== Liste des carburants dans le ComboBox =====================*/
	public void ajouterCarburant()
	{
		comboCarburant.getItems().removeAll(comboMarque.getItems());
		comboCarburant.getItems().addAll("Essence", "Diesel");
	}
	
	/*===================== Charger l'interface "Ajouter un vehicule" =====================*/
	public void interfaceAjouterVehicule(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterVehicule.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton d'ajouter le vehicule =====================*/
	public void ajouterVehicule(ActionEvent e) throws IOException, SQLException {
		if(!numImmatriculation.getText().equals("") && comboMarque.getValue()!=null && !type.getText().equals("") 
				&& comboCarburant.getValue()!=null  && !compteurKM.getText().equals("")  
				&& dateMiseCirculation.getValue()!=null ) 
		{
			String vSql = "SELECT * FROM `vehicule` WHERE numImmatriculation='"+numImmatriculation.getText()+"';";
			Connection vC = Login.connectDB();
			PreparedStatement vPS = vC.prepareStatement(vSql);
			ResultSet vResult = vPS.executeQuery();
			if(vResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un vehicule avec le même N° Immatriculation");
				alert.show();
			} 
			else {
				int intNumIm = Integer.parseInt(numImmatriculation.getText());
				int intComKM = Integer.parseInt(compteurKM.getText());
				Vehicule vehicule = new Vehicule(intNumIm, comboMarque.getValue(), type.getText(), comboCarburant.getValue(), intComKM, dateMiseCirculation.getValue(),null);
				String sql = "INSERT INTO `vehicule`(`numImmatriculation`, `marque`, `type`, `carburant`, `compteurKM`, `dateMiseCirculation`) VALUES (?,?,?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setInt(1, vehicule.getNumImmatriculation());
				ps.setString(2, vehicule.getMarque());
				ps.setString(3, vehicule.getType());
				ps.setString(4, vehicule.getCarburant());
				ps.setInt(5, vehicule.getCompteurKM());				
				ps.setObject(6, vehicule.getDateMiseCirculation());
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
	
	/*===================== Charger l'interface "Modifier/Supprimer un vehicule" =====================*/
	public void interfaceModifierVehicule(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierVehicule.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}

	/*===================== Bouton de rechercher un vehicule par numImmatriculation (modifier/suppimer)=====================*/
	public void rechercherVehicule() throws SQLException {
		rNumImm = Integer.parseInt(rechercher.getText());
		String sql = "SELECT * FROM vehicule WHERE numImmatriculation='"+rNumImm+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			numImmatriculation.setText(result.getString(1));
			comboMarque.setValue(result.getString(2));
			type.setText(result.getString(3));
			comboCarburant.setValue(result.getString(4));
			compteurKM.setText(result.getString(5));
			dateMiseCirculation.setValue(result.getDate(6).toLocalDate());
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun Vehicule avec ce N° Immatriculation");
			alert.show();
		}
		C.close();
	}
	
	/*===================== Bouton de modifier le vehicule =====================*/
	public void modifierVehicule(ActionEvent e) throws IOException, SQLException {
		if(!numImmatriculation.getText().equals("") && comboMarque.getValue()!=null && !type.getText().equals("") 
				&& comboCarburant.getValue()!=null  && !compteurKM.getText().equals("")  
				&& dateMiseCirculation.getValue()!=null )
		{
			String numIm = numImmatriculation.getText();
			String comKM = compteurKM.getText();
			int intNumIm = Integer.parseInt(numIm);
			int intComKM = Integer.parseInt(comKM);
			
			Vehicule vehicule = new Vehicule(intNumIm, comboMarque.getValue(), type.getText(), comboCarburant.getValue(), intComKM, dateMiseCirculation.getValue(), null);
			String sql = "SELECT `numImmatriculation` FROM `vehicule` WHERE numImmatriculation='"+intNumIm+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && vehicule.getNumImmatriculation()!=rNumImm) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un vehicule avec le même N° Immatriculation");
				alert.show();
			}
			else {
				String sql2 = "UPDATE `vehicule` SET `numImmatriculation`=?,`marque`=?,`type`=?,`carburant`=?,`compteurKM`=?,`dateMiseCirculation`=?  WHERE numImmatriculation=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setInt(1, vehicule.getNumImmatriculation());
				ps2.setString(2, vehicule.getMarque());
				ps2.setString(3, vehicule.getType());
				ps2.setString(4, vehicule.getCarburant());
				ps2.setInt(5, vehicule.getCompteurKM());
				ps2.setObject(6, vehicule.getDateMiseCirculation());
				ps2.setInt(7, rNumImm);
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
	
	/*===================== Bouton de supprimer le vehicule =====================*/
	public void supprimerVehicule(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `vehicule` WHERE numImmatriculation=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setInt(1, rNumImm);
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("Le vehicule a été supprimé");
		alert.show();
	}
	
	/*===================== Charger l'interface "informations d'un vehicule" =====================*/
	public void interfaceInfosVehicule(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosVehicule.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher un vehicule par numImmatriculation (informations)=====================*/
	public void infosVehicule() throws SQLException {
		rNumImm = Integer.parseInt(rechercher.getText());
		String sql = "SELECT * FROM vehicule WHERE numImmatriculation='"+rNumImm+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			numImmatriculation.setText(result.getString(1));
			infoMarque.setText(result.getString(2));
			type.setText(result.getString(3));
			infoCarburant.setText(result.getString(4));
			compteurKM.setText(result.getString(5));
			infoMise.setText(result.getDate(6).toString());
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun Vehicule avec ce N° Immatriculation");
			alert.show();
		}
		C.close();
	}
	
	/*===================== Charger l'interface "Liste des vehicules" ==================*/
	/*===================== Charger l'interface "Liste des vehicules" ==================*/
	public void interfaceVehiculesListe(ActionEvent e) throws IOException, SQLException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/VehiculesListe.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Liste des vehicules" ====================*/
	/*===================== Charger les données dans l'interface "Liste des vehicules" ====================*/
	public void actualiser(ActionEvent e) throws SQLException {
		ObservableList<Vehicule> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM vehicule ;";
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
		tableImmatriculation.setCellValueFactory(new PropertyValueFactory<Vehicule,Integer>("numImmatriculation"));
		tableMarque.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("marque"));
		tableType.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("Type"));
		tableCarburant.setCellValueFactory(new PropertyValueFactory<Vehicule,String>("carburant"));
		tableCompteurKM.setCellValueFactory(new PropertyValueFactory<Vehicule,Integer>("compteurKM"));
		tableDateMiseCirculation.setCellValueFactory(new PropertyValueFactory<Vehicule,LocalDate>("dateMiseCirculation"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionVehicule.fxml")));
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
