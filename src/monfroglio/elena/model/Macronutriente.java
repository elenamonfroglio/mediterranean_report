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
	
	public Macronutriente(String nome, int punteggio, double punteggioEnvironment, boolean moreisbetter) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.punteggioEnvironment = punteggioEnvironment;
		this.moreIsBetter = moreisbetter;
	}
	
	public String toString() {
		String ret = nome + ": "+punteggio+"\nemissioni carbonio: "+punteggioEnvironment;
		if(moreIsBetter) ret += ", more is better";
		else	ret += ", less is better";
		return ret;
	}
	
	public static boolean isMoreBetter(String nome) {
		boolean ret = true;
		switch(nome) {
			case "carne rossa":
				ret = false;
				break;
			case "latticini":
				ret = false;
				break;
			case "pollame":
				ret = false;
				break;
		}
		return ret;
	}
	
	public void print() {
		System.out.println("\n\n\n" +this);
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
