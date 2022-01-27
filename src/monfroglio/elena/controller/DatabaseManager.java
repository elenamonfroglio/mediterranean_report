package monfroglio.elena.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import monfroglio.elena.model.Macronutriente;
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
			ret = new Utente(nome,cognome,cf,eta,sesso);
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
			Macronutriente cereali = new Macronutriente("Cereali",rs.getInt("Cereali"),emissioniCereali,true,true);
			ret.add(cereali);
			Macronutriente patate = new Macronutriente("Patate",rs.getInt("Patate"),emissioniPatate,true,true);
			ret.add(patate);
			Macronutriente frutta = new Macronutriente("Frutta",rs.getInt("Frutta"),emissioniFrutta,true,true);
			ret.add(frutta);
			Macronutriente verdura = new Macronutriente("Verdura",rs.getInt("Verdura"),emissioniVerdura,true,true);
			ret.add(verdura);
			Macronutriente legumi = new Macronutriente("Legumi",rs.getInt("Legumi"),emissioniLegumi,true,true);
			ret.add(legumi);
			Macronutriente pesce = new Macronutriente("Pesce",rs.getInt("Pesce"),emissioniPesce,true,false);
			ret.add(pesce);
			Macronutriente carneRossa = new Macronutriente("CarneRossa",rs.getInt("CarneRossa"),emissioniCarneRossa,false,false);
			ret.add(carneRossa);
			Macronutriente pollame = new Macronutriente("Pollame",rs.getInt("Pollame"),emissioniPollame,false,false);
			ret.add(pollame);
			Macronutriente latticini = new Macronutriente("Latticini",rs.getInt("Latticini"),emissioniLatticini,false,false);
			ret.add(latticini);
			Macronutriente usoOlioOliva = new Macronutriente("UsoOlioOliva",rs.getInt("UsoOlioOliva"),emissioniOlioOliva,true,true);
			ret.add(usoOlioOliva);
			
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
	
}
