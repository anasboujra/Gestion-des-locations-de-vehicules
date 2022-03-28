package application;


import java.time.LocalDate;


public class Vehicule {
	
	private int numImmatriculation;
	private String marque;
	private String type;
	private String carburant;
	private int compteurKM;
	private LocalDate dateMiseCirculation;
	private String parking;
	
	public Vehicule() {
		super();
	}
	
	
	public Vehicule(int numImmatriculation, String marque, String type, String carburant, int compteurKM,
			LocalDate dateMiseCirculation, String parking) {
		super();
		this.numImmatriculation = numImmatriculation;
		this.marque = marque;
		this.type = type;
		this.carburant = carburant;
		this.compteurKM = compteurKM;
		this.dateMiseCirculation = dateMiseCirculation;
		this.parking = parking;
	}


	public String getParking() {
		return parking;
	}


	public void setParking(String parking) {
		this.parking = parking;
	}


	public int getNumImmatriculation() {
		return numImmatriculation;
	}
	public void setNumImmatriculation(int numImmatriculation) {
		this.numImmatriculation = numImmatriculation;
	}
	public String getMarque() {
		return marque;
	}
	public void setMarque(String marque) {
		this.marque = marque;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCarburant() {
		return carburant;
	}
	public void setCarburant(String carburant) {
		this.carburant = carburant;
	}
	public int getCompteurKM() {
		return compteurKM;
	}
	public void setCompteurKM(int compteurKM) {
		this.compteurKM = compteurKM;
	}
	public LocalDate getDateMiseCirculation() {
		return dateMiseCirculation;
	}
	public void setDateMiseCirculation(LocalDate dateMiseCirculation) {
		this.dateMiseCirculation = dateMiseCirculation;
	}
	 
	
}
