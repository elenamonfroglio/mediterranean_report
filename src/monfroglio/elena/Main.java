package monfroglio.elena;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import monfroglio.elena.controller.DatabaseManager;
import monfroglio.elena.controller.ReportRealiser;
import monfroglio.elena.controller.SentencePlanner;
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
			Utente u = dbmgr.getUtente("RSSFNC80A01A001I");
			int idTest = 91000;
			
			//creo una settimana relativa all'utente u
			LocalDate start = LocalDate.of(2022, Month.JANUARY, 3);
			LocalDate end = LocalDate.of(2022, Month.JANUARY, 9);
			Settimana sem = new Settimana(start,end,idTest);
			//recupero i pasti della settimana e l'indice di mediterraneità
			sem.setPasti(dbmgr.getPasti(sem));
			sem.setIndiceMed(dbmgr.getIndiceMed(sem));
			sem.setMacronutrienti(dbmgr.getPunteggiComponenti(sem));
			
			//Create a textPlanner
			TextPlanner tp = new TextPlanner("italiano",u,sem);
			tp.contentDetermination();
			tp.textStructuring();
			
			SentencePlanner sp = new SentencePlanner(tp.getFileName(),tp.getOrder());
			sp.sentenceAggregation();
			sp.lexicalisation();
			
			
			ReportRealiser rr = new ReportRealiser(sp.getFileName(), sp.phrases);
			//rr.createLongSentence_old();
			rr.createLongSentence_new();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
