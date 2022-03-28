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


public class GestionFacture {
	
	String rCodeFacture;

	@FXML
	private TextField codeFacture;
	@FXML
	private ComboBox<String> comboContrat;
	@FXML
	private DatePicker dateFacture;
	@FXML
	private TextField montantPayer;
	@FXML
	private TextField rechercher;
	@FXML
	private TextField infoContrat;
	@FXML
	private TextField infoDateFacture;
	
	// Table d'affichage
	@FXML
	private TableView<Facture> tableview;
	@FXML
	private TableColumn<Facture,String> code;
	@FXML
	private TableColumn<Facture,String> contrat;
	@FXML
	private TableColumn<Facture,?> dateF;
	@FXML
	private TableColumn<Facture,Double> mPayer;
	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Liste des factures dans le ComboBox =====================*/
	public void choixReservation() throws SQLException {
		String sql = "SELECT codeContrat FROM contrat;";
		Connection C = Login.connectDB();
		PreparedStatement PS = C.prepareStatement(sql);
		ResultSet resultSet = PS.executeQuery();
		comboContrat.getItems().removeAll(comboContrat.getItems());
        while (resultSet.next()){  
        	comboContrat.getItems().add(resultSet.getString(1)); 
        }
	}
	
	/*===================== Charger l'interface "Ajouter une facture" =====================*/
	public void interfaceAjouterFacture(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterFacture.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton d'ajouter le contrat =====================*/
	public void ajouterFacture(ActionEvent e) throws IOException, SQLException {
		if( !codeFacture.getText().equals("") && comboContrat.getValue()!=null &&  dateFacture.getValue()!=null && 
				!montantPayer.getText().equals("") ) 
		{
			String cSql = "SELECT * FROM `facture` WHERE codeFacture='"+codeFacture.getText()+"';";
			Connection cC = Login.connectDB();
			PreparedStatement cPS = cC.prepareStatement(cSql);
			ResultSet contratResult = cPS.executeQuery();
			if(contratResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà une facture avec le même Code");
				alert.show();
			} 
			else {
				String montantP = montantPayer.getText();
				double montant= Double.parseDouble(montantP);
				Facture facture = new Facture(codeFacture.getText(), comboContrat.getValue(), dateFacture.getValue(), montant);
				String sql = "INSERT INTO `facture`(`codeFacture`,`codeContrat`,`dateFacture`,`montantPayer`) VALUES (?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setString(1, facture.getCodeFacture());
				ps.setString(2, facture.getCodeContrat());
				ps.setObject(3, facture.getDateFacture());
				ps.setDouble(4, facture.getMontantPayer());
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
	
	/*===================== Charger l'interface "Modifier/Supprimer une facture" =====================*/
	public void interfaceModifierFacture(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierFacture.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}

	/*===================== Bouton de rechercher une facture par code (modifier/suppimer)=====================*/
	public void rechercherFacture() throws SQLException {
		rCodeFacture = rechercher.getText();
		String sql = "SELECT * FROM facture WHERE codeFacture='"+rCodeFacture+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeFacture.setText(result.getString(1));
			comboContrat.setValue(result.getString(2));
			dateFacture.setValue(result.getDate(3).toLocalDate());
			montantPayer.setText(result.getString(4));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucune Facture avec ce Code");
			alert.show();
		}
		C.close();
	}
	
	/*===================== Bouton de modifier la facture =====================*/
	public void modifierFacture(ActionEvent e) throws IOException, SQLException {
		if( !codeFacture.getText().equals("") && comboContrat.getValue()!=null &&  dateFacture.getValue()!=null && 
				!montantPayer.getText().equals("") ) 
		{
			String montantP = montantPayer.getText();
			double montant= Double.parseDouble(montantP);
			Facture facture = new Facture(codeFacture.getText(), comboContrat.getValue(), dateFacture.getValue(), montant);
			String sql = "SELECT `codeFacture` FROM `facture` WHERE codeFacture='"+facture.getCodeFacture()+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && !facture.getCodeFacture().equalsIgnoreCase(rCodeFacture)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà une Facture avec le meme code ");
				alert.show();
			}
			else {
				String sql2 = "UPDATE `facture` SET `codeFacture`=?, `codeContrat`=?, `dateFacture`=?, `montantPayer`=?  "
						+ "WHERE codeFacture=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setString(1, facture.getCodeFacture());
				ps2.setString(2, facture.getCodeContrat());
				ps2.setObject(3, facture.getDateFacture());
				ps2.setDouble(4, facture.getMontantPayer());
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
	
	/*===================== Bouton de supprimer la facture =====================*/
	public void supprimerFacture(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `facture` WHERE codeFacture=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setString(1, rechercher.getText());
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("La facture a été supprimé");
		alert.show();
	}
	
	/*===================== Charger l'interface "informations d'une facture" =====================*/
	public void interfaceInfosFacture(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/InfosFacture.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	 
	/*===================== Bouton de rechercher une facture par code (informations)=====================*/
	public void infosFacture() throws SQLException {
		String rCodeFacture = rechercher.getText();
		String sql = "SELECT * FROM facture WHERE codeFacture='"+rCodeFacture+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			codeFacture.setText(result.getString(1));
			infoContrat.setText(result.getString(2));
			infoDateFacture.setText(result.getDate(3).toString());
			montantPayer.setText(result.getString(4));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucune Facture avec ce Code");
			alert.show();
		}
		C.close();
	}

	/*===================== Charger l'interface "informations des factures par ordre décroissant de la date" ==================*/
	public void interfaceFactureDecroissance(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/FactureDecroissance.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Factures par ordre décroissant de date" ====================*/
	public void actualiser(ActionEvent e) throws SQLException {
		ObservableList<Facture> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM facture ORDER BY dateFacture DESC;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		while(result.next()){
			Facture facture = new Facture();
			facture.setCodeFacture(result.getString(1));
			facture.setCodeContrat(result.getString(2));
			facture.setDateFacture(result.getDate(3).toLocalDate());
			facture.setMontantPayer(result.getDouble(4));
			data.add(facture);
		}
		code.setCellValueFactory(new PropertyValueFactory<Facture,String>("codeFacture"));
		contrat.setCellValueFactory(new PropertyValueFactory<Facture,String>("codeContrat"));
		dateF.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
		mPayer.setCellValueFactory(new PropertyValueFactory<Facture,Double>("montantPayer"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionFacture.fxml")));
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
