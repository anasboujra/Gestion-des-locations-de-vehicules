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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GestionContrat {
	
	String rCode;

	@FXML
	private TextField codeContrat;
	@FXML
	private ComboBox <String> comboReservation;
	@FXML
	private TextField rechercher;
	@FXML
	private DatePicker dateContrat;
	@FXML
	private DatePicker dateEcheance;
	@FXML
	private TextField infoReservation;
	@FXML
	private TextField infoDateC;
	@FXML
	private TextField infoDateE;
	
	// Table d'affichage
	@FXML
	private TableView<Contrat> tableview;
	@FXML
	private TableColumn<Contrat,String> code;
	@FXML
	private TableColumn<Contrat,String> reservation;
	@FXML
	private TableColumn<Contrat,?> dateC;
	@FXML
	private TableColumn<Contrat,?> dateE;
	
		
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Liste des reservations dans le ComboBox =====================*/
	public void choixReservation() throws SQLException {
		String sql = "SELECT codeReservation FROM reservation;";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		comboReservation.getItems().removeAll(comboReservation.getItems());
        while (resultSet.next()){  
        	comboReservation.getItems().add(resultSet.getString(1)); 
        }
	}

	/*===================== Charger l'interface "Ajouter un contrat" =====================*/
	public void interfaceAjouterContrat(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterContrat.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton d'ajouter le contrat =====================*/
	public void ajouterContrat(ActionEvent e) throws IOException, SQLException {
		if( !codeContrat.getText().equals("") && comboReservation.getValue()!=null &&  dateContrat.getValue()!=null && 
				dateEcheance.getValue()!=null ) 
		{
			String cSql = "SELECT * FROM `contrat` WHERE codeContrat='"+codeContrat.getText()+"';";
			Connection cC = Login.connectDB();
			PreparedStatement cPS = cC.prepareStatement(cSql);
			ResultSet contratResult = cPS.executeQuery();
			if(contratResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un contat avec le même Code");
				alert.show();
			} 
			else {
				 
				Contrat contrat = new Contrat(codeContrat.getText(), comboReservation.getValue(), dateContrat.getValue(), dateEcheance.getValue());
				String sql = "INSERT INTO `contrat`(`codeContrat`, `codeReservation`, `dateContrat`, `dateEcheance`) VALUES (?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setString(1, contrat.getCodeContrat());
				ps.setString(2, contrat.getCodeReservation());
				ps.setObject(3, contrat.getDateContrat());
				ps.setObject(4, contrat.getDateEcheance());
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
	
	/*===================== Charger l'interface "Modifier/Supprimer un contrat" =====================*/
	public void interfaceModifierContrat(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierContrat.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher un contrat par code (modifier/suppimer)=====================*/
	public void rechercherContrat(ActionEvent e) throws SQLException {
		rCode = rechercher.getText();
		String sql = "SELECT * FROM `contrat` WHERE codeContrat='"+rCode+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeContrat.setText(result.getString(1));
			comboReservation.setValue(result.getString(2));
			dateContrat.setValue(result.getDate(3).toLocalDate());
			dateEcheance.setValue(result.getDate(4).toLocalDate());
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun contrat avec ce CODE");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Bouton de modifier le contrat =====================*/
	public void modifierContrat(ActionEvent e) throws IOException, SQLException {
		if( !codeContrat.getText().equals("") && comboReservation.getValue()!=null &&  dateContrat.getValue()!=null && 
				dateEcheance.getValue()!=null ) 
		{
			Contrat contrat = new Contrat(codeContrat.getText(),"" ,dateContrat.getValue(),dateEcheance.getValue());
			String sql = "SELECT `codeContrat` FROM `contrat` WHERE codeContrat='"+contrat.getCodeContrat()+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && !contrat.getCodeContrat().equalsIgnoreCase(rCode)) {	
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un utilisateur avec le même CIN");
				alert.show();
			}
			else {
				String sql2 = "UPDATE `contrat` SET `codeContrat`=?, `codeReservation`=?, `dateContrat`=?, `dateEcheance`=?  "
						+ "WHERE codeContrat=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setString(1, contrat.getCodeContrat());
				ps2.setString(2, contrat.getCodeReservation());
				ps2.setObject(3, contrat.getDateContrat());
				ps2.setObject(4, contrat.getDateEcheance());
				ps2.setString(5, rechercher.getText());
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
	
	/*===================== Bouton de supprimer le contrat =====================*/
	public void supprimerContrat(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `contrat` WHERE codeContrat=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setString(1, rechercher.getText());
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("Le contrat a été supprimé");
		alert.show();
	}
	
	/*===================== Charger l'interface "informations d'un contrat" =====================*/
	public void interfaceInfosContrat(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosContrat.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher un contrat par code (informations)=====================*/
	public void infosContrat(ActionEvent e) throws SQLException {
		rCode = rechercher.getText();
		String sql = "SELECT * FROM `contrat` WHERE codeContrat='"+rCode+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeContrat.setText(result.getString(1));
			infoReservation.setText(result.getString(2));
			infoDateC.setText(result.getDate(3).toString());
			infoDateE.setText(result.getDate(4).toString());
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun contrat avec ce CODE");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Charger l'interface "informations des contrats par ordre décroissant de la date" ==================*/
	public void interfaceContratDecroissance(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ContratDecroissance.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Contrats par ordre décroissant de date" ====================*/
	public void actualiser(ActionEvent e) throws SQLException {
		ObservableList<Contrat> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM contrat ORDER BY dateContrat DESC;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Contrat contrat = new Contrat();
			contrat.setCodeContrat(result.getString(1));
			contrat.setCodeReservation(result.getString(2));
			contrat.setDateContrat(result.getDate(3).toLocalDate());
			contrat.setDateEcheance(result.getDate(4).toLocalDate());
			data.add(contrat);
		}
		code.setCellValueFactory(new PropertyValueFactory<Contrat,String>("codeContrat"));
		reservation.setCellValueFactory(new PropertyValueFactory<Contrat,String>("codeReservation"));
		dateC.setCellValueFactory(new PropertyValueFactory<>("dateContrat"));
		dateE.setCellValueFactory(new PropertyValueFactory<>("dateEcheance"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionContrat.fxml")));
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
