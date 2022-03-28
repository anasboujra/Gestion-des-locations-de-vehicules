package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionSanction {
	
	int montant;
	
	@FXML
	private ComboBox<String> comboContratSan;
	@FXML
	private Label labelMontant;
	
	// Table d'affichage
	@FXML
	private TableView<Sanction> tableview;
	@FXML
	private TableColumn<Sanction,String> contrat;
	@FXML
	private TableColumn<Sanction,String> client;
	@FXML
	private TableColumn<Sanction,Integer> sanction;

	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Charger l'interface "Clients Sanctionnes" =====================*/
	public void interfaceClientsSanctionnes(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ClientsSanctionnes.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans l'interface "Clients Sanctionnes" =====================*/
	public void actualiserClientsSanctionnes(ActionEvent e) throws SQLException {
		ObservableList<Sanction> data = FXCollections.observableArrayList();	
		String sql = "SELECT * FROM `contrat`;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		while(result.next()){
			if(LocalDate.now().isAfter(result.getDate(4).toLocalDate()) && result.getInt(5)==0) {
				Sanction san = new Sanction();
				san.setContrat(result.getString(1));
				String sql2 = "SELECT codeClient FROM reservation WHERE codeReservation='"+result.getString(2)+"';";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ResultSet result2 = ps2.executeQuery();
				if(result2.next()) {
					san.setClient(result2.getString(1));
					Period period = Period.between(result.getDate(4).toLocalDate(), LocalDate.now());
					san.setSanction(2000*(period.getDays()+period.getMonths()*30));
					data.add(san);
				}	
			}
		}
		contrat.setCellValueFactory(new PropertyValueFactory<Sanction,String>("contrat"));
		client.setCellValueFactory(new PropertyValueFactory<Sanction,String>("client"));
		sanction.setCellValueFactory(new PropertyValueFactory<Sanction,Integer>("sanction"));
		tableview.setItems(data);
		C.close();
	}
	
	/*===================== Charger l'interface "Regler les sanctions" =====================*/
	public void interfaceReglerSanction(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ReglerSanction.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Charger les données dans le combobox des contrats =====================*/
	public void contratsARegler() throws SQLException {
		String sql = "SELECT * FROM `contrat`;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		comboContratSan.getItems().removeAll(comboContratSan.getItems());
		while(result.next()){
			if(LocalDate.now().isAfter(result.getDate(4).toLocalDate()) && result.getInt(5)==0) {
				comboContratSan.getItems().add(result.getString(1));
			}
		}
		C.close();
	}

	/*===================== Charger la sanction =====================*/
	public void santionParContrat(ActionEvent e) throws SQLException {
		String sql = "SELECT dateEcheance FROM `contrat` WHERE codeContrat='"+comboContratSan.getValue()+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			Period period = Period.between(result.getDate(1).toLocalDate(), LocalDate.now());
			montant =2000*(period.getDays()+period.getMonths()*30);
			labelMontant.setText(montant+" DH");
		}
	}

	/*===================== Regler la sanction =====================*/
	public void reglerSanction(ActionEvent e) throws SQLException, IOException {
		if(comboContratSan.getValue()!=null) {
			String sql = "UPDATE contrat SET reglSanction='"+montant+"' where codeContrat=?;";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ps.setString(1,comboContratSan.getValue());
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
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionSanction.fxml")));
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

