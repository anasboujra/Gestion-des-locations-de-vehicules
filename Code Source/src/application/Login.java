package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;



public class Login {
	@FXML
	private Label connectionLabel;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField password2;
	@FXML
	private PasswordField cle;
	
	/*===================== Bouton de fermeture de la fenêtre =====================*/
	public void exitButton() {
		Main.stage.close();
	}
	
	/*===================== Bouton de reduire la fenêtre =====================*/
	public void minimizeButton() {
		Main.stage.setIconified(true);
	}
	
	/*===================== connection a la base de donnees =====================*/
	public static Connection connectDB(){
		Connection C = null;
		try {
			C = DriverManager.getConnection("jdbc:mysql://localhost/java","root","");
			System.out.println("Connected to DB");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return C;
	}
	
	/*===================== etat de la connection =====================*/
	public void connectionStat(ActionEvent e) throws SQLException{
		if(!Login.connectDB().isClosed())
			connectionLabel.setText("connecté");
		else
			connectionLabel.setText("pas connecté");
	}
	
	/*===================== pour le button "ENTRER" =====================*/
	public void enterKey() {
		username.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		        	try {
						loginButton();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }
		    }
		});
		password.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		        	try {
						loginButton();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }
		    }
		});
	}
	
	/*===================== Bouton de connection de l'utilisateur =====================*/
	public void loginButton() throws IOException, SQLException {
		String adminSql = "SELECT * FROM `administrateur` WHERE username='"+username.getText()+"';";
		Connection adminC = Login.connectDB();
		PreparedStatement adminPS = adminC.prepareStatement(adminSql);
		ResultSet adminResult = adminPS.executeQuery();
		if(adminResult.next()) {
			if(password.getText().equals(adminResult.getString(2))) {
				Main.ad=true;
				Parent panelRoot = FXMLLoader.load(getClass().getResource(("GUI/AdminPanel.fxml")));
				panelRoot.setOnMousePressed(Main.handlerPressed);
				panelRoot.setOnMouseDragged(Main.handlerDragged);
				Scene panelScene = new Scene(panelRoot);
				Main.stage.setScene(panelScene);
				Main.stage.centerOnScreen();
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("L'identifiant ou le mot de passe est incorrect");
				alert.show();
			}
		}
		else {
			String userSql = "SELECT * FROM `utilisateur` WHERE cin='"+username.getText()+"';";
			Connection userC = Login.connectDB();
			PreparedStatement userPS = userC.prepareStatement(userSql);
			ResultSet userResult = userPS.executeQuery();
			if(userResult.next() && password.getText().equals(userResult.getString(2))) {
				if(userResult.getString(7).equals("continuer")) {
					Main.ad=false;
					Parent panelRoot = FXMLLoader.load(getClass().getResource(("GUI/UserPanel.fxml")));
					panelRoot.setOnMousePressed(Main.handlerPressed);
					panelRoot.setOnMouseDragged(Main.handlerDragged);
					Scene panelScene = new Scene(panelRoot);
					Main.stage.setScene(panelScene);
					Main.stage.centerOnScreen();
				}
				else if(userResult.getString(7).equals("suspendre")) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("INFORMATION");
					alert.setHeaderText("Cet utilisateur a été suspendu");
					alert.show();
				}
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText("L'identifiant ou le mot de passe est incorrect");
				alert.show();
			}
		}
	}
	
	/*===================== Charger l'interface "mot de passe oublie" =====================*/
	public void interfaceMotDePasseOublie() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/MotDePasseOublie.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
	
	/*===================== le button de changer le mot de passe oublie =====================*/
	public void changer(ActionEvent e) throws IOException, SQLException {
		if( !username.getText().equals("") && !cle.getText().equals("") && !password.getText().equals("") && 
				!password2.getText().equals("") ) 
		{
			String adminSql = "SELECT * FROM `administrateur` WHERE username='"+username.getText()+"';";
			Connection adminC = Login.connectDB();
			PreparedStatement adminPS = adminC.prepareStatement(adminSql);
			ResultSet adminResult = adminPS.executeQuery();
			if(adminResult.next()) {
				if(!cle.getText().equals("ensa2020")) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText("Le clé est incorrect");
					alert.show();
				}
				else if(!password.getText().equals(password2.getText())) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText("La confirmation du mot de passe n'est pas valide");
					alert.show();
				}
				else {
					String sql2 = "UPDATE `administrateur` SET `password`=? WHERE username=?;";
					Connection C2 = Login.connectDB();
					PreparedStatement ps2 = C2.prepareStatement(sql2);
					ps2.setString(1, password.getText());
					ps2.setString(2, username.getText());
					ps2.executeUpdate();
					retourLogin();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Succès");
					alert.setHeaderText("Les données ont été enregistrées");
					alert.show();
				}
			}
			else {
				String userSql = "SELECT * FROM `utilisateur` WHERE cin='"+username.getText()+"';";
				Connection userC = Login.connectDB();
				PreparedStatement userPS = userC.prepareStatement(userSql);
				ResultSet userResult = userPS.executeQuery();
				if(userResult.next()) {
					if(!cle.getText().equals("ensa2020")) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Erreur");
						alert.setHeaderText("Le clé est incorrect");
						alert.show();
					}
					else if(!password.getText().equals(password2.getText())) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Erreur");
						alert.setHeaderText("La confirmation du mot de passe n'est pas valide");
						alert.show();
					}
					else {
						String sql2 = "UPDATE `utilisateur` SET `password`=? WHERE cin=?;";
						Connection C2 = Login.connectDB();
						PreparedStatement ps2 = C2.prepareStatement(sql2);
						ps2.setString(1, password.getText());
						ps2.setString(2, username.getText());
						ps2.executeUpdate();
						retourLogin();
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Succès");
						alert.setHeaderText("Les données ont été enregistrées");
						alert.show();
					}
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText("L'identifiant est incorrect");
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
	
	/*===================== Bouton de retour au menu de connection =====================*/
	public void retourLogin() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(("GUI/Login.fxml")));
		root.setOnMousePressed(Main.handlerPressed);
		root.setOnMouseDragged(Main.handlerDragged);
		Scene scene = new Scene(root);
		Main.stage.setScene(scene);
		Main.stage.centerOnScreen();
	}
}
