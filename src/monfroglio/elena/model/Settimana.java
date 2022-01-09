package monfroglio.elena.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Settimana {
	public LocalDate giornoInizio;//TBC
	public LocalDate giornoFine;//TBC
	public ArrayList<Pasto> pasti;
	public int idTest;
	public ArrayList<Macronutriente> macronutrienti;
	public int indiceMed;
	
	
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
