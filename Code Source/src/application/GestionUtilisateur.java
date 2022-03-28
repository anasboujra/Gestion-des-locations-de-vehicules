package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class GestionUtilisateur {

	@FXML
	private TextField cin;
	@FXML
	private TextField password;
	@FXML
	private TextField nom;
	@FXML
	private TextField prenom;
	@FXML
	private TextField email;
	@FXML
	private TextField adresse;
	@FXML
	private TextField rechercher;
	@FXML
	private Label suspendre;
	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== Charger l'interface "Ajouter un utilisateur" =====================*/
	public void interfaceAjouterUtilisateur(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/AjouterUtilisateur.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}

	/*===================== Bouton d'ajouter l'utilisateur =====================*/
	public void ajouterUtilisateur(ActionEvent e) throws IOException, SQLException {
		if(!cin.getText().equals("") && !password.getText().equals("") && !nom.getText().equals("") && !prenom.getText().equals("") && !email.getText().equals("") && !adresse.getText().equals("")) {
			String userSql = "SELECT * FROM `utilisateur` WHERE cin='"+cin.getText()+"';";
			Connection userC = Login.connectDB();
			PreparedStatement userPS = userC.prepareStatement(userSql);
			ResultSet userResult = userPS.executeQuery();
			if(userResult.next()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un utilisateur avec le même CIN");
				alert.show();
			} 
			else {
				Utilisateur user = new Utilisateur(cin.getText(), password.getText(), nom.getText(), prenom.getText(), email.getText(), adresse.getText());
				String sql = "INSERT INTO utilisateur(`cin`, `password`, `nom`, `prenom`, `email`, `adresse`) VALUES (?,?,?,?,?,?)";
				Connection C = Login.connectDB();
				PreparedStatement ps = C.prepareStatement(sql);
				ps.setString(1, user.getCin());
				ps.setString(2, user.getPassword());
				ps.setString(3, user.getNom());
				ps.setString(4, user.getPrenom());
				ps.setString(5, user.getEmail());
				ps.setString(6, user.getAdresse());
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
	
	/*===================== Charger l'interface "Modifier/Supprimer un utilisateur" =====================*/
	public void interfaceModifierUtilisateur(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/ModifierUtilisateur.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher une reservation par CIN (modifier/suppimer)=====================*/
	public void rechercherUtilisateurModifier(ActionEvent e) throws SQLException {
		String rCIN = rechercher.getText();
		String sql = "SELECT * FROM `utilisateur` WHERE cin='"+rCIN+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			cin.setText(result.getString(1));
			password.setText(result.getString(2));
			nom.setText(result.getString(3));
			prenom.setText(result.getString(4));
			email.setText(result.getString(5));
			adresse.setText(result.getString(6));
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun utilisateur avec ce CIN");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Bouton de modifier l'utilisateur =====================*/
	public void modifierUtilisateur(ActionEvent e) throws IOException, SQLException {
		if(!cin.getText().equals("") && !password.getText().equals("") && !nom.getText().equals("") 
				&& !prenom.getText().equals("") && !email.getText().equals("") && !adresse.getText().equals("")) {
			
			Utilisateur user = new Utilisateur(cin.getText(), password.getText(), nom.getText(), 
					prenom.getText(), email.getText(), adresse.getText());
			String sql = "SELECT `cin` FROM `utilisateur` WHERE cin='"+user.getCin()+"';";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			if(result.next() && !user.getCin().equalsIgnoreCase(rechercher.getText())) {	
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("Il y'a déjà un utilisateur avec le même CIN");
				alert.show();
			}
			else {
				String sql2 = "UPDATE `utilisateur` SET `cin`=?,`password`=?,`nom`=?,`prenom`=?,`email`=?,`adresse`=?  WHERE cin=?;";
				Connection C2 = Login.connectDB();
				PreparedStatement ps2 = C2.prepareStatement(sql2);
				ps2.setString(1, user.getCin());
				ps2.setString(2, user.getPassword());
				ps2.setString(3, user.getNom());
				ps2.setString(4, user.getPrenom());
				ps2.setString(5, user.getEmail());
				ps2.setString(6, user.getAdresse());
				ps2.setString(7, rechercher.getText());
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
	
	/*===================== Bouton de supprimer l'utilisateur =====================*/
	public void supprimerUtilisateur(ActionEvent e) throws SQLException, IOException {
		String sql = "DELETE FROM `utilisateur` WHERE cin=?;";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ps.setString(1, rechercher.getText());
		ps.executeUpdate();
		C.close();
		retourGestion();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succès");
		alert.setHeaderText("L'utilisateur a été supprimé");
		alert.show();
	}

	/*===================== Charger l'interface "Suspendre un utilisateur" =====================*/
	public void interfaceSuspendreUtilisateur(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/SuspendreUtilisateur.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== Bouton de rechercher un utlisateur par cin (Suspendre/Continuer)=====================*/
	public void rechercherUtilisateurSuspendre(ActionEvent e) throws SQLException {
		String rCIN = rechercher.getText();
		String sql = "SELECT `suspendre` FROM `utilisateur` WHERE cin='"+rCIN+"';";
		Connection C = Login.connectDB();
		PreparedStatement ps = C.prepareStatement(sql);
		ResultSet result = ps.executeQuery();
		if(result.next()) {
			if(result.getString(1).equals("continuer")){
				suspendre.setText("Suspendre");
			}
			else if(result.getString(1).equals("suspendre")) {
				suspendre.setText("Continuer");
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText("Il n'y a aucun utilisateur avec ce CIN");
			alert.show();			
		}
		C.close();
	}
	
	/*===================== Bouton du suspendre un utilisateur=====================*/
	public void suspendreUtilisateur() throws SQLException, IOException {
		if(suspendre.getText()=="Suspendre"){
			String sql = "UPDATE `utilisateur` SET `suspendre`=?  WHERE `cin`=?;";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ps.setString(1, "suspendre");
			ps.setString(2, rechercher.getText());
			ps.executeUpdate();
			C.close();
			retourGestion();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Succès");
			alert.setHeaderText("Les données ont été enregistrées");
			alert.show();
		}
		else if(suspendre.getText()=="Continuer") {
			String sql = "UPDATE `utilisateur` SET `suspendre`=?  WHERE `cin`=?;";
			Connection C = Login.connectDB();
			PreparedStatement ps = C.prepareStatement(sql);
			ps.setString(1, "continuer");
			ps.setString(2, rechercher.getText());
			ps.executeUpdate();
			C.close();
			retourGestion();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Succès");
			alert.setHeaderText("Les données ont été enregistrées");
			alert.show();
		}
	}
	
	/*===================== Bouton de retour au menu de gestion =====================*/
	public void retourGestion() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/GestionUtilisateur.fxml")));
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
