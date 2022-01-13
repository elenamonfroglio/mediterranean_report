package monfroglio.elena.model;

public class Macronutriente {
	private String nome;
	private int punteggio;
	private double punteggioEnvironment;
	private boolean moreIsBetter;
	private boolean goodForEnvironment;
	
	public Macronutriente(String nome, int punteggio, double punteggioEnvironment, boolean moreisbetter, boolean goodForEnvironment) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.punteggioEnvironment = punteggioEnvironment;
		this.moreIsBetter = moreisbetter;
		this.goodForEnvironment = goodForEnvironment;
	}
	
	public String toString() {
		String ret = nome + ": "+punteggio+"\nemissioni carbonio: "+punteggioEnvironment+"\n";
		if(moreIsBetter) ret += ", more is better";
		else	ret += ", less is better";
		return ret;
	}
	
	public void print() {
		System.out.println(this);
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getPunteggio() {
		return punteggio;
	}
	
	public double getPunteggioEnvironment() {
		return punteggioEnvironment;
	}
	
	public boolean getMoreIsBetter() {
		return moreIsBetter;
	}
	
	public boolean getGoodForEnvironment() {
		return goodForEnvironment;
	}
	
}
