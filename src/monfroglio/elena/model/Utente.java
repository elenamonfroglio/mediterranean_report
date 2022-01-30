package monfroglio.elena.model;

public class Utente {
	private String nome;//*
	private String cognome;//*
	private String cf;
	private int eta;//*
	private float peso;
	private int altezza;
	private float bmi;
	private String categoriaPeso;//TBC
	private String valutazioneComplessiva;//TBC
	private String sesso;//*
	private int abitudiniAlimentari;
	private int indiceAttivitaFisica;
	private int conoscenzaDominio;//*
	private int stress;//*
	private boolean interesseAmbientale;//*
	
	public Utente(String nome, String cognome, String cf, int eta, float peso, int altezza, float bmi,
			String categoriaPeso, String valutazioneComplessiva,
			String sesso, int abitudiniAlimentari, int indiceAttivitaFisica, 
			int conoscenzaDominio, int stress, boolean interesseAmbientale) {
		this.nome = nome;
		this.cognome = cognome;
		this.cf = cf;
		this.eta = eta;
		this.peso = peso;
		this.altezza = altezza;
		this.bmi = bmi;
		this.categoriaPeso = categoriaPeso;
		this.valutazioneComplessiva = valutazioneComplessiva;
		this.sesso = sesso;
		this.abitudiniAlimentari = abitudiniAlimentari;
		this.indiceAttivitaFisica = indiceAttivitaFisica;
		this.conoscenzaDominio = conoscenzaDominio;
		this.stress = stress;
		this.interesseAmbientale = interesseAmbientale;
	}
	
	//TOBE
	
	public Utente(String nome, String cognome, int eta, String sesso,
			int conoscenzaDominio, int stress, boolean interesseAmbientale) {
		this.nome = nome;
		this.cognome = cognome;
		this.cf = "";
		this.eta = eta;
		this.peso = -1;
		this.altezza = -1;
		this.bmi = -1;
		this.categoriaPeso = "";
		this.valutazioneComplessiva = "";
		this.sesso = sesso;
		this.conoscenzaDominio = conoscenzaDominio;
		this.stress = stress;
		this.interesseAmbientale = interesseAmbientale;
	}
	
	//ASIS
	public Utente(String nome, String cognome, String cf, int eta, String sesso) {
		this.nome = nome;
		this.cognome = cognome;
		this.cf = cf;
		this.eta = eta;
		this.peso = -1;
		this.altezza = -1;
		this.bmi = -1;
		this.categoriaPeso = "";
		this.valutazioneComplessiva = "";
		this.sesso = sesso;
		this.conoscenzaDominio = -1;
		this.stress = -1;
		this.interesseAmbientale = false;
	}
	
	public void print() {
		System.out.println(this);
	}
	
	public String toString() {
		return nome+" "+cognome+", CF: "+cf+" \n";//TBC
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getSesso() {
		return sesso;
	}
	
	public int getEta() {
		return eta;
	}
	
	public String getCognome() {
		return cognome;
	}
	
	public boolean getInteresseAmbientale() {
		return interesseAmbientale;
	}
}
