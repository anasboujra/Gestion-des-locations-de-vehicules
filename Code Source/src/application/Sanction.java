package application;

public class Sanction {

	private String contrat;
	private String client;
	private int sanction;
	
	public Sanction() {
		super();
	}

	public Sanction(String contrat, String client, int sanction) {
		super();
		this.contrat = contrat;
		this.client = client;
		this.sanction = sanction;
	}

	public String getContrat() {
		return contrat;
	}

	public void setContrat(String contrat) {
		this.contrat = contrat;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public int getSanction() {
		return sanction;
	}

	public void setSanction(int sanction) {
		this.sanction = sanction;
	}
	
	
}
