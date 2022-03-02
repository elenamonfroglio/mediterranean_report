package monfroglio.elena;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;

import monfroglio.elena.controller.DatabaseManager;
import monfroglio.elena.controller.ReportRealiser;
import monfroglio.elena.controller.SentencePlanner;
import monfroglio.elena.controller.SenticnetManager;
import monfroglio.elena.controller.TextPlanner;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;


public class Main {
	public static void main(String[] args) {
		DatabaseManager dbmgr = new DatabaseManager();
		test1(dbmgr);
		
	}
	
	public static void test2(DatabaseManager dbmgr) {
		SenticnetManager sm = new SenticnetManager("italiano");
		sm.readCSV();
		System.out.println("fine");
	}
	
	public static void test1(DatabaseManager dbmgr) {
		try {
			//recupero user model dal codice fiscale
			Utente u = dbmgr.getUtente("FRRLSS00A41F848C");
			int idTest = 91000;
			int idTestPreviousWeek = 91001;
			
			//creo una settimana relativa all'utente u
			LocalDate start = LocalDate.of(2022, Month.JANUARY, 3);
			LocalDate end = LocalDate.of(2022, Month.JANUARY, 9);
			Settimana sem = new Settimana(start,end,idTest);
			Settimana prevSem = new Settimana(idTestPreviousWeek);
			//recupero i pasti della settimana e l'indice di mediterraneità
			ArrayList<Pasto> pasti = dbmgr.getEatingHistory(idTest);
			for(Pasto p:pasti) {
				HashMap<String,Float> ret = dbmgr.getConsumptionFromRicetta(p.getIdRicetta());	
				p.setPunteggi(ret);
			}
			sem.setPasti(pasti);
			sem.setIndiceMed(dbmgr.getIndiceMed(sem));
			prevSem.setIndiceMed(dbmgr.getIndiceMed(prevSem));
			sem.setMacronutrienti(dbmgr.getPunteggiComponenti(sem));
			
			//Create a textPlanner
			TextPlanner tp = new TextPlanner("italiano",u,sem,prevSem);
			tp.contentDetermination();
			tp.textStructuring();
			
			SentencePlanner sp = new SentencePlanner(tp.getFileName(),tp.getOrder(), sem);
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
