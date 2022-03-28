package application;

public class Parking {

	private int nParking;
	
	private String nom;
	
	private int capacite;
	
	private String rue;
	
	private String arrondissement;

	public Parking() {
		super();
	}

	public int getnParking() {
		return nParking;
	}

	public void setnParking(int nParking) {
		this.nParking = nParking;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getCapacite() {
		return capacite;
	}

	public void setCapacite(int capacite) {
		this.capacite = capacite;
	}

	public String getRue() {
		return rue;
	}

	public void setRue(String rue) {
		this.rue = rue;
	}

	public String getArrondissement() {
		return arrondissement;
	}

	public void setArrondissement(String arrondissement) {
		this.arrondissement = arrondissement;
	}

	public Parking(int nParking, String nom, int capacite, String rue, String arrondissement) {
		super();
		this.nParking = nParking;
		this.nom = nom;
		this.capacite = capacite;
		this.rue = rue;
		this.arrondissement = arrondissement;
	}
	
	
}
