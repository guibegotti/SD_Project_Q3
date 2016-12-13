public class Boss implements java.io.Serializable{
	private String name;
	private float hp;
	
	public Boss(String name, float hp){
		this.name = name;
		this.hp = hp;
	}
	
	public String getName(){
		return this.name;	
	}
	
	public float getHp(){
		return this.hp;
	}
	
	public void setHp(float hp){
		this.hp = hp;
	}
}
