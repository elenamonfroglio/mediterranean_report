package monfroglio.elena.model;

public abstract class EmotionName {
	public static final java.lang.String ECSTASY = "#ecstasy";
	public static final java.lang.String JOY = "#joy";
	public static final java.lang.String CONTENTMENT = "#contentment";
	public static final java.lang.String MELANCHOLY = "#melancholy";
	public static final java.lang.String SADNESS = "#sadness";
	public static final java.lang.String GRIEF = "#grief";
	
	public static final java.lang.String BLISS = "#bliss";
	public static final java.lang.String CALMNESS = "#calmness";
	public static final java.lang.String SERENITY = "#serenity";
	public static final java.lang.String ANNOYANCE = "#annoyance";
	public static final java.lang.String ANGER = "#anger";
	public static final java.lang.String RAGE = "#rage";
	
	public static final java.lang.String DELIGHT = "#delight";
	public static final java.lang.String PLEASENTNESS = "#pleasentness";
	public static final java.lang.String ACCEPTANCE = "#acceptance";
	public static final java.lang.String DISLIKE = "#dislike";
	public static final java.lang.String DISGUST = "#disgust";
	public static final java.lang.String LOATHING = "#loathing";

	public static final java.lang.String ENTHUSIASM = "#enthusiasm";
	public static final java.lang.String EAGERNESS = "#eagerness";
	public static final java.lang.String RESPONSIVENESS = "#responsiveness";
	public static final java.lang.String ANXIETY = "#anxiety";
	public static final java.lang.String FEAR = "#fear";
	public static final java.lang.String TERROR = "#terror";
	
	
	public static int getIntesity(String name) {
		int ret = 0;
		if(name.equals(EmotionName.GRIEF) || 
				name.equals(EmotionName.RAGE) ||
				name.equals(EmotionName.LOATHING) ||
				name.equals(EmotionName.TERROR))
			ret = -3;
		else if(name.equals(EmotionName.SADNESS) || 
				name.equals(EmotionName.ANGER) ||
				name.equals(EmotionName.DISGUST) ||
				name.equals(EmotionName.FEAR))
			ret = -2;
		else if(name.equals(EmotionName.MELANCHOLY) || 
				name.equals(EmotionName.ANNOYANCE) ||
				name.equals(EmotionName.DISLIKE) ||
				name.equals(EmotionName.ANXIETY))
			ret = -1;
		else if(name.equals(EmotionName.CONTENTMENT) || 
				name.equals(EmotionName.SERENITY) ||
				name.equals(EmotionName.ACCEPTANCE) ||
				name.equals(EmotionName.RESPONSIVENESS))
			ret = 1;
		else if(name.equals(EmotionName.JOY) || 
				name.equals(EmotionName.CALMNESS) ||
				name.equals(EmotionName.PLEASENTNESS) ||
				name.equals(EmotionName.EAGERNESS))
			ret = 2;
		else if(name.equals(EmotionName.ECSTASY) || 
				name.equals(EmotionName.BLISS) ||
				name.equals(EmotionName.DELIGHT) ||
				name.equals(EmotionName.ENTHUSIASM))
			ret = 3;
		return ret;
	}
}
