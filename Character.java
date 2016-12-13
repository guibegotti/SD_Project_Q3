public class Character implements java.io.Serializable
{
    private String id;
	private String name;
	private String race;
	private String classType;
	private float level;
	private float hp;
	
	public Character(String id, String name, String race, String classType,
					 float level, float hp)
	{
		this.id    = id;
		this.name  = name;
		this.race  = race;
		this.classType = classType;
		this.level = level;
		this.hp	   = hp;
	}
	
	public String getId(){
	    return this.id;
	}
	public String getName(){
	    return this.name;
	}
	public String getRace(){
	    return this.race;
	}
	public String getClassType(){
	    return this.classType;
	}
	public float getLevel(){
	    return this.level;
	}
		public float getHp(){
	    return this.hp;
	}
	public void setHp(float hp){
		this.hp = hp;
	}
	public String toString(){
	    return "Personagem: ["+id+"] "+name+" ("+race+", "+classType+") - Level: "+level+" - HP: "+hp+".";
	}
}
