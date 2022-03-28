package application;

public class Utilisateur {
	
	private String cin;
	private String password;
	private String nom;
	private String prenom;
	private String email;
	private String adresse;
	
	public Utilisateur() {
		super();
	}
	public Utilisateur(String cin, String password, String nom, String prenom, String email, String adresse) {
		super();
		this.cin = cin;
		this.password = password;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.adresse = adresse;
	}

	public String getCin() {
		return cin;
	}
	public void setCin(String cin) {
		this.cin = cin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	

}
