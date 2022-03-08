package monfroglio.elena.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import monfroglio.elena.Main.Point;

public class Settimana {
	private LocalDate giornoInizio;//TBC
	private LocalDate giornoFine;//TBC
	private ArrayList<Pasto> pasti;
	private int idTest;
	private ArrayList<Macronutriente> macronutrienti;
	private int indiceMed;
	
	
	public Settimana(LocalDate giornoInizio, LocalDate giornoFine, ArrayList<Pasto> pasti, int idTest, int indiceMed) {
		this.giornoInizio = giornoInizio;
		this.giornoFine = giornoFine;
		this.pasti = pasti;
		this.idTest = idTest;
		this.indiceMed = indiceMed;
	}
	
	public Settimana(LocalDate giornoInizio, LocalDate giornoFine, int idTest) {
		this.giornoInizio = giornoInizio;
		this.giornoFine = giornoFine;
		this.idTest = idTest;
	}
	
	public Settimana(int idTest) {
		this.idTest = idTest;
	}
	
	public String toString() {
		return "Settimana dal "+giornoInizio.toString()+" al "+giornoFine.toString()+
				": \n\nIndice di mediterraneità: "+indiceMed+
				"\n";
	}
	
	public void print() {
		System.out.println(this);
		//printPasti();
		printMacronutrienti();
	}
	
	public ArrayList<Pasto> getPasti() {
		return pasti;
	}
	
	public void setPasti(ArrayList<Pasto> pasti) {
		this.pasti = pasti;
	}
	
	public ArrayList<Macronutriente> getMacronutrienti() {
		return macronutrienti;
	}
	
	public void setMacronutrienti(ArrayList<Macronutriente> macronutrienti) {
		this.macronutrienti = macronutrienti;
	}

	public int getIndiceMed() {
		return indiceMed;
	}
	
	public int getIdTest() {
		return idTest;
	}
	
	public void setIndiceMed(int indiceMed) {
		this.indiceMed = indiceMed;
	}
	
	public void printPasti() {
		for(Pasto p:pasti) {
			System.out.print(p + ", ");
		}
	}
	
	public void printMacronutrienti() {
		for(Macronutriente m:macronutrienti) {
			System.out.print(m + ";\n");
		}
	}
	
	public ArrayList<Macronutriente> getAllMoreIsBetter() {
		ArrayList<Macronutriente> ret = new ArrayList<Macronutriente>(7);
		for(Macronutriente m: macronutrienti){
			if(m.getMoreIsBetter()) {
				ret.add(m);
			}
		}
		return ret;
	}
	
	public ArrayList<Macronutriente> getAllLessIsBetter() {
		ArrayList<Macronutriente> ret = new ArrayList<Macronutriente>(7);
		for(Macronutriente m: macronutrienti){
			if(!m.getMoreIsBetter()) {
				ret.add(m);
			}
		}
		return ret;
	}
	
	public int countBadEnvironment() {
		int ret = 0;
		
		for (Macronutriente m: macronutrienti) {
			if (!m.getGoodForEnvironment()) {
				ret += m.getPunteggio();
			}
		}
		
		return ret;
	}
	
	public Pasto getParetoOptimal(ArrayList<String> veryGoodMacros) {
		Pasto bestPasto = null;
		
		createComparator(veryGoodMacros);
		
		for(Pasto p:pasti) {
			HashMap<String,Float> consumption = p.getPunteggi();
			//devo considerare i tipi verygood e trovare il pasto con il più alto punteggio per queste categorie
			
		}
		
		return bestPasto;
	}
	
	public void createComparator(ArrayList<String> veryGoodMacros) {
		ParetoComparator<Point> comparator = new ParetoComparator<Point>();
	}
	
	public Pasto getPastoWithMacros(ArrayList<String> veryGoodMacros,HashMap<String, ArrayList<String>> dictionary) {
		Pasto bestPasto = null;
		float maxScore = -1;
		
		for(Pasto p:pasti) {
			HashMap<String,Float> consumption = p.getPunteggi();
			//devo considerare i tipi verygood e trovare il pasto con il più alto punteggio per queste categorie
			float currentScore = p.getScore(veryGoodMacros,dictionary);
			if(currentScore>maxScore) {
				maxScore = currentScore;
				bestPasto = p;
			}
		}
		
		return bestPasto;
	}
	
	/*
	public Settimana(DatabaseManager dbmgr, int idtest, String cf) {
		try {
			pasti = dbmgr.getPasti(idtest);
			utente = dbmgr.getUtente(cf);
			calcolaIndiceMed(idtest);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
