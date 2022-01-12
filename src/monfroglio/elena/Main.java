package monfroglio.elena;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import monfroglio.elena.controller.DatabaseManager;
import monfroglio.elena.controller.TextPlanner;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;

public class Main {
	public static void main(String[] args) {
		DatabaseManager dbmgr = new DatabaseManager();
		test1(dbmgr);
		
	}
	
	public static void test2(DatabaseManager dbmgr) {
	}
	
	public static void test1(DatabaseManager dbmgr) {
		try {
			//recupero user model dal codice fiscale
			Utente u = dbmgr.getUtente("prmdnn75b44l219r");
			int idTest = 91000;
			
			//creo una settimana relativa all'utente u
			LocalDate start = LocalDate.of(2022, Month.JANUARY, 3);
			LocalDate end = LocalDate.of(2022, Month.JANUARY, 9);
			Settimana sem = new Settimana(start,end,idTest);
			//recupero i pasti della settimana e l'indice di mediterraneità
			sem.pasti = dbmgr.getPasti(sem);
			sem.indiceMed = dbmgr.getIndiceMed(sem);
			sem.macronutrienti = dbmgr.getPunteggiComponenti(sem);
			
			sem.print();			
			
			//Create a textPlanner
			TextPlanner tp = new TextPlanner("it",u,sem);
			tp.contentDetermination();
			tp.textStructuring();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TBD
	/*
	public static void getPasti(DatabaseManager dbmgr) throws SQLException {
		//ArrayList<String> meals = dbmgr.getMealsName(91000);
		//System.out.println(meals);
	}
	
	//TBD
	public static void getUtente(DatabaseManager dbmgr) throws SQLException {
		//User usr = dbmgr.getUsersDetails(91000);
		//System.out.println(meals);
	}
	*/
}
