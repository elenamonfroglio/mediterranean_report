package monfroglio.elena.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.MacronutrienteType;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;


public class DatabaseManager{
	//valori normalizzati tra 1 e 2
	private static double emissioniCarneRossa = 100.00;//
	private static double emissioniLatticini = 29.64;//
	private static double emissioniPesce = 23.81;
	private static double emissioniPollame = 21.43;//
	private static double emissioniOlioOliva = 21.43;
	private static double emissioniCereali = 7.62;
	private static double emissioniVerdura = 5.00;
	private static double emissioniLegumi = 3.39;
	private static double emissioniFrutta = 3.00;
	private static double emissioniPatate = 1.43;
	
	
	public String url = "jdbc:postgresql://localhost:5432/dietdb2.0";
	public String usr = "elena";
	public String psw = "MealDB";
	public Connection conn;
	public Properties props;
	public Statement stmt;
	
	
	public DatabaseManager() {
		props = new Properties();
		props.setProperty("user",usr);
		props.setProperty("password",psw);
		try {
			creaConnection();
			creaStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void creaConnection() throws SQLException{
		conn = DriverManager.getConnection(url, props);
		System.out.println("Connection established successfully");
	}
	
	
	private void creaStatement() throws SQLException{
		stmt = conn.createStatement();
		System.out.println("Statement created successfully");
	}
	
	public String getNomeRicettaFromId(int idRicetta) throws SQLException{
		String ret = "";
		String query = "SELECT ricetta.nome "+
				"FROM diet.ricetta "+ 
				"WHERE ricetta.\"id\" = '"+idRicetta+"'";
		
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			ret = rs.getString("nome");
		}
		
		return ret;
	}
	
	
	public ArrayList<Pasto> getPasti(Settimana sem) throws SQLException{
		int idTest = sem.getIdTest();
		ArrayList<Pasto> ret = new ArrayList<>();
		
		String query = "SELECT ricetta.nome "+
				"FROM simulation_tests.ricettetest "+ 
				"FULL JOIN diet.ricetta ON ricettetest.\"Ricetta\" = ricetta.id "+
				"WHERE ricettetest.\"Test\" = '"+idTest+"'";
		
		ResultSet rs = stmt.executeQuery(query);
			
		while (rs.next()) {
			String nomePasto = rs.getString("nome");
			Pasto p = new Pasto(nomePasto);
			//p.print();
			ret.add(p);
		}
		return ret;
	}
	
	public ArrayList<Integer> getPastiId(Settimana sem) throws SQLException{
		int idTest = sem.getIdTest();
		ArrayList<Integer> ret = new ArrayList<>();
		
		String query = "SELECT ricettetest.\"Ricetta\" "+
				"FROM simulation_tests.ricettetest "+ 
				"WHERE ricettetest.\"Test\" = '"+idTest+"'";
		
		ResultSet rs = stmt.executeQuery(query);
			
		while (rs.next()) {
			Integer idPasto = rs.getInt("ricetta");
			//p.print();
			ret.add(idPasto);
		}
		return ret;
	}
	

	public Utente getUtente(String cf) throws SQLException{
		Utente ret = null;
		
		String query = "SELECT * "
				+ "FROM diet.users "
				+ "WHERE users.cf = '"+cf+"'";
		
		ResultSet rs = stmt.executeQuery(query);
			
		while (rs.next()) {
			String nome = rs.getString("nome");
			String cognome = rs.getString("cognome");
			int eta = rs.getInt("età");
			String sesso = rs.getString("sesso");
			boolean interesseAmbientale = rs.getBoolean("interesseAmbientale");
			int conoscenzaDominio  = rs.getInt("conoscenzaDominio");
			int stress  = rs.getInt("stress");
			ret = new Utente(nome,cognome,eta,sesso,conoscenzaDominio,stress,interesseAmbientale);
			ret.setCF(cf);
			//ret.print();
		}
		return ret;
	}
	
	
	public ArrayList<Macronutriente> getPunteggiComponenti(Settimana sem) throws SQLException{
		int idTest = sem.getIdTest();
		ArrayList<Macronutriente> ret = new ArrayList<Macronutriente>(10);
		
		String query = "SELECT * "
				+ "FROM simulation_tests.punteggitest "
				+ "WHERE punteggitest.\"Test\" = '"+idTest+"'";
		
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			Macronutriente cereali = new Macronutriente(MacronutrienteType.CEREALI,rs.getInt("Cereali"),emissioniCereali,true,true);
			ret.add(cereali);
			Macronutriente patate = new Macronutriente(MacronutrienteType.PATATE,rs.getInt("Patate"),emissioniPatate,true,true);
			ret.add(patate);
			Macronutriente frutta = new Macronutriente(MacronutrienteType.FRUTTA,rs.getInt("Frutta"),emissioniFrutta,true,true);
			ret.add(frutta);
			Macronutriente verdura = new Macronutriente(MacronutrienteType.VERDURA,rs.getInt("Verdura"),emissioniVerdura,true,true);
			ret.add(verdura);
			Macronutriente legumi = new Macronutriente(MacronutrienteType.LEGUMI,rs.getInt("Legumi"),emissioniLegumi,true,true);
			ret.add(legumi);
			Macronutriente pesce = new Macronutriente(MacronutrienteType.PESCE,rs.getInt("Pesce"),emissioniPesce,true,false);
			ret.add(pesce);
			Macronutriente carneRossa = new Macronutriente(MacronutrienteType.CARNEROSSA,rs.getInt("CarneRossa"),emissioniCarneRossa,false,false);
			ret.add(carneRossa);
			Macronutriente pollame = new Macronutriente(MacronutrienteType.POLLAME,rs.getInt("Pollame"),emissioniPollame,false,false);
			ret.add(pollame);
			Macronutriente latticini = new Macronutriente(MacronutrienteType.LATTICINI,rs.getInt("Latticini"),emissioniLatticini,false,false);
			ret.add(latticini);
			Macronutriente usoOlioOliva = new Macronutriente(MacronutrienteType.USOOLIOOLIVA,rs.getInt("UsoOlioOliva"),emissioniOlioOliva,true,true);
			ret.add(usoOlioOliva);
			
		}
		
		return ret;
	}
	
	public HashMap<String,Float> getConsumptionFromRicetta(int idRicetta) throws SQLException{
		HashMap<String,Float> ret = new HashMap<String,Float>();
		
		String query = "SELECT * "
				+ "FROM recipes_scores.punteggiricette "
				+ "WHERE punteggiricette.\"Ricetta\" = '"+idRicetta+"'";
		
		ResultSet rs = stmt.executeQuery(query);
		
		float cons = 0;
		
		while (rs.next()) {
			cons = rs.getFloat("Cereali");
			ret.put(MacronutrienteType.CEREALI,cons);
			cons = rs.getFloat("Patate");
			ret.put(MacronutrienteType.PATATE,cons);
			cons = rs.getFloat("Frutta");
			ret.put(MacronutrienteType.FRUTTA,cons);
			cons = rs.getFloat("Verdura");
			ret.put(MacronutrienteType.VERDURA,cons);
			cons = rs.getFloat("Legumi");
			ret.put(MacronutrienteType.LEGUMI,cons);
			cons = rs.getFloat("Pesce");
			ret.put(MacronutrienteType.PESCE,cons);
			cons = rs.getFloat("CarneRossa");
			ret.put(MacronutrienteType.CARNEROSSA,cons);
			cons = rs.getFloat("Pollame");
			ret.put(MacronutrienteType.POLLAME,cons);
			cons = rs.getFloat("Latticini");
			ret.put(MacronutrienteType.LATTICINI,cons);
			cons = rs.getFloat("UsoOlioOliva");
			ret.put(MacronutrienteType.USOOLIOOLIVA,cons);
		}
		
		
		return ret;
	}
	
	public int getIndiceMed(Settimana sem) throws SQLException{
		int idTest = sem.getIdTest();
		int ret = 0;
		
		String query = "SELECT * "
				+ "FROM simulation_tests.tests "
				+ "WHERE tests.\"Test\" = '"+idTest+"'";
		
		ResultSet rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			ret = rs.getInt("indice");
		}
		
		return ret;
	}
	
	public ArrayList<Pasto> getEatingHistory(int idTest) throws SQLException {
		String query = "SELECT * "
				+ "FROM diet.eatinghistory "
				+ "WHERE eatinghistory.\"id_test\" = '"+idTest+"'";
		
		int idRicetta = 0;
		ResultSet rs = stmt.executeQuery(query);
		
		ArrayList<Pasto> ret = new ArrayList<>();
		
		while (rs.next()) {
			idRicetta = rs.getInt("alimento");
			Date date = rs.getDate("giorno");
			Time orario = rs.getTime("orario");
			int slot = rs.getInt("slot");
			
			Pasto p = new Pasto(idRicetta, orario, date, slot);
			ret.add(p);
		}
		
		for(Pasto p:ret) {
			p.setNome(getNomeRicettaFromId(p.getIdRicetta()));
		}
		
		return ret;
	}
	
	public void insertSingleMealEatingHistory(int idMeal, String user, int ricetta, LocalTime orario, LocalDate giorno, int slot, LocalDate week, int idTest) throws SQLException {
		//System.out.println(idMeal + ",   " + user  + ",   " + ricetta + ",   " + orario + ",   "+ giorno + ",   " + slot + ",   " + week + ",   " + idTest);
		
		String query = "INSERT INTO diet.eatinghistory (id, utente, alimento, orario, giorno, slot, week, id_test)"
				+ " VALUES('"+idMeal+"','"+user+"', '"+ricetta+"', '"+orario+"', '"+giorno+"', '"+slot+"', '"+week+"', '"+idTest+"');";
		System.out.println(query);
		
		stmt.executeUpdate(query);
		
	}
	
}
