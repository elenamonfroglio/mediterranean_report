package monfroglio.elena.model;

public class Emotion {
	private String nome;
	private String type;
	private int intensity;
	
	public Emotion(String nome) {
		this.nome = nome;
		this.type = EmotionType.getType(nome);
		this.intensity = EmotionName.getIntesity(nome);
	}
	
	public boolean equals(Emotion e) {
		return (this.nome.equals(e.nome) && this.type.equals(e.type) && this.intensity==e.intensity);
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getIntesity() {
		return intensity;
	}
	
	public String getType() {
		return type;
	}
}
