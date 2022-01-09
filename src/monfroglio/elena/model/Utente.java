package monfroglio.elena.model;

public class Utente {
	public String nome;//*
	public String cognome;//*
	public String cf;
	public int eta;//*
	public float peso;
	public int altezza;
	public float bmi;
	public String categoriaPeso;//TBC
	public String attivitaComplessiva;//TBC
	public String valutazioneComplessiva;//TBC
	public String sesso;//*
	public int conoscenzaDominio;//*
	public int stress;//*
	public boolean interesseAmbientale;//*
	public int willness;//TBC
	
	public Utente(String nome, String cognome, String cf, int eta, float peso, int altezza, float bmi,
			String categoriaPeso, String attivitaComplessiva, String valutazioneComplessiva,
			String sesso, int conoscenzaDominio, int stress, boolean interesseAmbientale, int willness) {
		this.nome = nome;
		this.cognome = cognome;
		this.cf = cf;
		this.eta = eta;
		this.peso = peso;
		this.altezza = altezza;
		this.bmi = bmi;
		this.categoriaPeso = categoriaPeso;
		this.attivitaComplessiva = attivitaComplessiva;
		this.valutazioneComplessiva = valutazioneComplessiva;
		this.sesso = sesso;
		this.conoscenzaDominio = conoscenzaDominio;
		this.stress = stress;
		this.interesseAmbientale = interesseAmbientale;
		this.willness = willness;
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
		this.attivitaComplessiva = "";
		this.valutazioneComplessiva = "";
		this.sesso = sesso;
		this.conoscenzaDominio = conoscenzaDominio;
		this.stress = stress;
		this.interesseAmbientale = interesseAmbientale;
		this.willness = -1;
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
		this.attivitaComplessiva = "";
		this.valutazioneComplessiva = "";
		this.sesso = sesso;
		this.conoscenzaDominio = -1;
		this.stress = -1;
		this.interesseAmbientale = false;
		this.willness = -1;
	}
	
	public void print() {
		System.out.println(this);
	}
	
	public String toString() {
		return nome+" "+cognome+", CF: "+cf+" \n";//TBC
	}
}
