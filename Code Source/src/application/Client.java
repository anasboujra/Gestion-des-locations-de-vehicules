package application;
import java.io.InputStream;

public class Client {

	private String codeClient;
	private String nomComplet;
	private String Adresse;
	private int    nGSM;
	private InputStream permisConduire;
	
	public Client() {
		super();
	}

	public Client(String codeClient, String nomComplet, String adresse, int nGSM, InputStream permisConduire) {
		super();
		this.codeClient = codeClient;
		this.nomComplet = nomComplet;
		Adresse = adresse;
		this.nGSM = nGSM;
		this.permisConduire = permisConduire;
	}

	public String getCodeClient() {
		return codeClient;
	}

	public void setCodeClient(String codeClient) {
		this.codeClient = codeClient;
	}

	public String getNomComplet() {
		return nomComplet;
	}

	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}

	public String getAdresse() {
		return Adresse;
	}

	public void setAdresse(String adresse) {
		Adresse = adresse;
	}

	public int getNGSM() {
		return nGSM;
	}

	public void setNGSM(int nGSM) {
		this.nGSM = nGSM;
	}

	public InputStream getPermisConduire() {
		return permisConduire;
	}

	public void setPermisConduire(InputStream permisConduire) {
		this.permisConduire = permisConduire;
	}

	
}
