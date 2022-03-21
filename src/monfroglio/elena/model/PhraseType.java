package monfroglio.elena.model;

public abstract class PhraseType {
	  public static final java.lang.String VERYGOOD = "very good";
	  public static final java.lang.String GOOD = "good";
	  public static final java.lang.String BAD = "bad";
	  public static final java.lang.String VERYBAD = "very bad";
	  public static final java.lang.String MEAL = "meal";
	  public static final java.lang.String WELCOME = "welcome";
	  public static final java.lang.String EXCLAMATION = "esclamation";
	  public static final java.lang.String ENVIRONMENT = "environment";
	  public static final java.lang.String KNOWLEDGE = "knowledge";
	  
	  public static boolean isOpposite(String a, String b) {
		  boolean ret = false;
		  if(a.equals("") || b.equals(""))	return false;
		  if((a.equals(PhraseType.VERYGOOD) && b.equals(PhraseType.VERYBAD)) || 
				  (b.equals(PhraseType.VERYGOOD) && a.equals(PhraseType.VERYBAD)))
			  ret = true;
		  else if((a.equals(PhraseType.VERYGOOD) && b.equals(PhraseType.BAD)) || 
				  (b.equals(PhraseType.VERYGOOD) && a.equals(PhraseType.BAD)))
			  ret = true;
		  else if((a.equals(PhraseType.GOOD) && b.equals(PhraseType.BAD)) || 
				  (b.equals(PhraseType.GOOD) && a.equals(PhraseType.BAD)))
			  ret = true;
		  else if((a.equals(PhraseType.GOOD) && b.equals(PhraseType.VERYBAD)) || 
				  (b.equals(PhraseType.GOOD) && a.equals(PhraseType.VERYBAD)))
			  ret = true;
		  return ret;
	  }
}
