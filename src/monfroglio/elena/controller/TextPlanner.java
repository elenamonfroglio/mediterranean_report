package monfroglio.elena.controller;

import java.util.ArrayList;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;
import javax.json.*;


public class TextPlanner {
	public String lingua;
	public Utente user;
	public Settimana settimana;
	
	public TextPlanner() {
		lingua = "it";
	}
	
	public TextPlanner(String lingua) {
		this.lingua = lingua;
	}

	public TextPlanner(String lingua, Utente user, Settimana settimana) {
		this.lingua = lingua;
		this.user = user;
		this.settimana = settimana;
	}
	
	public void getTextPlan() {
		ArrayList<Macronutriente> mores = settimana.getAllMoreIsBetter();
		ArrayList<Macronutriente> lesses = settimana.getAllLessIsBetter();
		
		//
		if (user.interesseAmbientale) {
			int count = settimana.countBadEnvironment();
			int index = 0;
			//classifico il TextPlan con 4 diverse classi di gravità (0-5,5-10,10-15,15-20)
			if(count<5) index = 0;
			else if(count<10) index = 1;
			else if(count<15) index = 2;
			else index = 3;
		}
		
	}
	
}
