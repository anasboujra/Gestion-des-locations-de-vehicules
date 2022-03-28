package application;

import java.io.IOException;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionReservation {
	
	String rCode;
	@FXML
	private TextField codeReservation;
	@FXML
	private TextField rechercher;
	@FXML
	private DatePicker dateReservation;
	@FXML
	private DatePicker dateDepart;
	@FXML
	private DatePicker dateRetour;
	@FXML
	private ComboBox <String> comboStatut;
	@FXML
	private ComboBox <String> comboClient;
	@FXML
	private ComboBox <String> comboVehicule;
	@FXML
	private TextField infoClient;
	@FXML
	private TextField infoVehicule;
	@FXML
	private TextField infoDateRes;
	@FXML
	private TextField infoDateDep;
	@FXML
	private TextField infoDateRet;
	@FXML
	private TextField infoStatut;
	
	// Table d'affichage
	@FXML
	private TableView<Reservation> tableview;
	@FXML
	private TableColumn<Reservation,String> code;
	@FXML
	private TableColumn<Reservation,String> client;
	@FXML
	private TableColumn<Reservation,String> vehicule;
	@FXML
	private TableColumn<Reservation,?> dateRe;
	@FXML
	private TableColumn<Reservation,?> dateD;
	@FXML
	private TableColumn<Reservation,?> dateRo;
	@FXML
	private TableColumn<Reservation,String> statusRe;
	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Liste des Clients dans le ComboBox =====================*/
	public void choixClient() throws SQLException {
		String sql = "SELECT codeClient FROM client;";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		comboClient.getItems().removeAll(comboClient.getItems());
        while (resultSet.next()){  
            comboClient.getItems().add(resultSet.getString(1)); 
        }
	}
	
	/*===================== Liste des Vehicules dans le ComboBox =====================*/
	public void choixVehicule() throws SQLException {
		String sql = "SELECT numImmatriculation FROM vehicule;";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
        comboVehicule.getItems().removeAll(comboVehicule.getItems());
        while (resultSet.next()){  
            comboVehicule.getItems().add(resultSet.getString(1)); 
        }
	}
	
	/*===================== Liste des statuts dans le ComboBox =====================*/
	public void  choixStatut( ) {
		comboStatut.getItems().removeAll(comboStatut.getItems());
		comboStatut.getItems().addAll("NonValidée", "Validée", "Annulée");
	}
		
	/*===================== Charger l'interface "Ajouter une reservation" =====================*/
	public void interfaceAjouterReservation(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterReservation.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton d'ajouter la reservation =====================*/
	public void ajouterReservation(ActionEvent e) throws IOException, SQLException {
		if( !codeReservation.getText().equals("") && comboClient.getValue()!=null && comboVehicule.getValue()!=null &&  
				dateReservation.getValue()!=null && dateDepart.getValue()!=null && dateRetour.getValue()!=null && 
				comboStatut.getValue()!=null ) 
		{
			String cSql = "SELECT * FROM `reservation` WHERE codeReservation='"+codeReservation.getText()+"';";
			Connection cC = Login.connectDB();
			PreparedStatement cPS = cC.prepareStatement(cSql);
			ResultSet reservationResult = cPS.executeQuery();
			if(reservationResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà une reservation avec le même code");
				alert.show();
			} 
			else {
				Reservation reservation = new Reservation(codeReservation.getText(), comboClient.getValue(), 
						comboVehicule.getValue(), dateReservation.getValue(), dateDepart.getValue(),dateRetour.getValue(), 
						comboStatut.getValue());
				String sql = "INSERT INTO `reservation`(`codeReservation`, `codeClient`, `numImmatriculation`, "
						+ "`dateReservation`, `dateDepart`, `dateRetour`, `status`) VALUES (?,?,?,?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setString(1, reservation.getCodeReservation());
				ps.setString(2, reservation.getCodeClient());
				ps.setString(3, reservation.getNumImmatriculation());
				ps.setObject(4, reservation.getDateReservation());
				ps.setObject(5, reservation.getDateDepart());
				ps.setObject(6, reservation.getDateRetour());
				ps.setString(7, reservation.getStatus());
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
	
	/*===================== Charger l'interface "Modifier/Supprimer une reservation" =====================*/
	public void interfaceModifierReservation(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierReservation.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher une reservation par code (modifier/suppimer)=====================*/
	public void rechercherReservation(ActionEvent e) throws SQLException {
		rCode = rechercher.getText();
		String sql = "SELECT * FROM `reservation` WHERE codeReservation='"+ rCode +"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeReservation.setText(result.getString(1));
			comboClient.setValue(result.getString(2));
			comboVehicule.setValue(result.getString(3));
			dateReservation.setValue(result.getDate(4).toLocalDate());
			dateDepart.setValue(result.getDate(5).toLocalDate());
			dateRetour.setValue(result.getDate(6).toLocalDate());
			comboStatut.setValue(result.getString(7));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucune reservation avec ce CODE");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Bouton de modifier la reservation =====================*/
	public void modifierReservation(ActionEvent e) throws IOException, SQLException {
		if( !codeReservation.getText().equals("") && comboClient.getValue()!=null && comboVehicule.getValue()!=null &&  
				dateReservation.getValue()!=null && dateDepart.getValue()!=null && dateRetour.getValue()!=null && 
				comboStatut.getValue()!=null ) 
		{
			Reservation reservation = new Reservation(codeReservation.getText(), comboClient.getValue(), 
					comboVehicule.getValue(), dateReservation.getValue(), dateDepart.getValue(),dateRetour.getValue(), 
					comboStatut.getValue());
			String sql = "SELECT codeReservation FROM reservation WHERE codeReservation='"+reservation.getCodeReservation()+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && !reservation.getCodeReservation().equalsIgnoreCase(rCode)) {	
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà une reservation avec le même CODE");
				alert.show();
			}
			else {
				String sql2 = "UPDATE reservation SET codeReservation=?, codeClient=?, numImmatriculation=?, dateReservation=?, "
						+ "dateDepart=?, dateRetour=?, status=?  WHERE codeReservation=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setString(1, reservation.getCodeReservation());
				ps2.setString(2, reservation.getCodeClient());
				ps2.setString(3, reservation.getNumImmatriculation());
				ps2.setObject(4, reservation.getDateReservation());
				ps2.setObject(5, reservation.getDateDepart());
				ps2.setObject(6, reservation.getDateRetour());
				ps2.setString(7, reservation.getStatus());
				ps2.setString(8, rCode);
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
	
	/*===================== Bouton de supprimer la reservation =====================*/
	public void supprimerReservation(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `reservation` WHERE codeReservation=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setString(1, rCode);
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("La réservation a été supprimée");
		alert.show();
	}
	
	/*===================== Charger l'interface "informations d'une reservation" =====================*/
	public void interfaceInfosReservation(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosReservation.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher une reservation par code (informations)=====================*/
	public void infosReservation(ActionEvent e) throws SQLException {
		rCode = rechercher.getText();
		String sql = "SELECT * FROM `reservation` WHERE codeReservation='"+ rCode +"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeReservation.setText(result.getString(1));
			infoClient.setText(result.getString(2));
			infoVehicule.setText(result.getString(3));
			infoDateRes.setText(result.getDate(4).toString());
			infoDateDep.setText(result.getDate(5).toString());
			infoDateRet.setText(result.getDate(6).toString());
			infoStatut.setText(result.getString(7));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucune reservation avec ce CODE");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Charger l'interface "Réservations par ordre décroissant de la date de rés" =====================*/
	public void interfaceReservationDecroissance(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ReservationDecroissance.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Réservations par ordre décroissant" =====================*/
	public void actualiser(ActionEvent e) throws SQLException {
		ObservableList<Reservation> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM reservation ORDER BY dateReservation DESC ;";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Reservation reservation = new Reservation();
			reservation.setCodeReservation(result.getString(1));
			reservation.setCodeClient(result.getString(2));
			reservation.setNumImmatriculation(result.getString(3));
			reservation.setDateReservation(result.getDate(4).toLocalDate());
			reservation.setDateDepart(result.getDate(5).toLocalDate());
			reservation.setDateRetour(result.getDate(6).toLocalDate());
			reservation.setStatus(result.getString(7));
			data.add(reservation);
		}
		code.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeReservation"));
		client.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeClient"));
		vehicule.setCellValueFactory(new PropertyValueFactory<Reservation,String>("numImmatriculation"));
		dateRe.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
		dateD.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
		dateRo.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
		statusRe.setCellValueFactory(new PropertyValueFactory<Reservation,String>("status"));
		tableview.setItems(data);
		C.close();
	}

	/*===================== Charger l'interface "Réservations validées" =====================*/
	public void interfaceReservationsValidees(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ReservationsValidees.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Réservations validées" =====================*/
	public void actualiserReservationsValidees(ActionEvent e) throws SQLException {
		ObservableList<Reservation> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM `reservation` WHERE `status` = 'Validée';";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Reservation reservation = new Reservation();
			reservation.setCodeReservation(result.getString(1));
			reservation.setCodeClient(result.getString(2));
			reservation.setNumImmatriculation(result.getString(3));
			reservation.setDateReservation(result.getDate(4).toLocalDate());
			reservation.setDateDepart(result.getDate(5).toLocalDate());
			reservation.setDateRetour(result.getDate(6).toLocalDate());
			reservation.setStatus(result.getString(7));
			data.add(reservation);
		}
		code.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeReservation"));
		client.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeClient"));
		vehicule.setCellValueFactory(new PropertyValueFactory<Reservation,String>("numImmatriculation"));
		dateRe.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
		dateD.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
		dateRo.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
		statusRe.setCellValueFactory(new PropertyValueFactory<Reservation,String>("status"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Charger l'interface "Réservations non validées" =====================*/
	public void interfaceReservationsNonValidees(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ReservationsNonValidees.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Réservations non validées" =====================*/
	public void actualiserReservationsNonValidees(ActionEvent e) throws SQLException {
		ObservableList<Reservation> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM `reservation` WHERE `status` = 'NonValidée';";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Reservation reservation = new Reservation();
			reservation.setCodeReservation(result.getString(1));
			reservation.setCodeClient(result.getString(2));
			reservation.setNumImmatriculation(result.getString(3));
			reservation.setDateReservation(result.getDate(4).toLocalDate());
			reservation.setDateDepart(result.getDate(5).toLocalDate());
			reservation.setDateRetour(result.getDate(6).toLocalDate());
			reservation.setStatus(result.getString(7));
			data.add(reservation);
		}
		code.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeReservation"));
		client.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeClient"));
		vehicule.setCellValueFactory(new PropertyValueFactory<Reservation,String>("numImmatriculation"));
		dateRe.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
		dateD.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
		dateRo.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
		statusRe.setCellValueFactory(new PropertyValueFactory<Reservation,String>("status"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Charger l'interface "Réservations annulées" =====================*/
	public void interfaceReservationsAnnulees(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ReservationsAnnulees.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Réservations annulées" =====================*/
	public void actualiserReservationsAnnulees(ActionEvent e) throws SQLException {
		ObservableList<Reservation> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM `reservation` WHERE `status` = 'Annulée';";
		Connection C = Login.connectDB();
		PreparedStatement ps = (PreparedStatement)C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Reservation reservation = new Reservation();
			reservation.setCodeReservation(result.getString(1));
			reservation.setCodeClient(result.getString(2));
			reservation.setNumImmatriculation(result.getString(3));
			reservation.setDateReservation(result.getDate(4).toLocalDate());
			reservation.setDateDepart(result.getDate(5).toLocalDate());
			reservation.setDateRetour(result.getDate(6).toLocalDate());
			reservation.setStatus(result.getString(7));
			data.add(reservation);
		}
		code.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeReservation"));
		client.setCellValueFactory(new PropertyValueFactory<Reservation,String>("codeClient"));
		vehicule.setCellValueFactory(new PropertyValueFactory<Reservation,String>("numImmatriculation"));
		dateRe.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
		dateD.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
		dateRo.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
		statusRe.setCellValueFactory(new PropertyValueFactory<Reservation,String>("status"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionReservation.fxml")));
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
