public class Response implements java.io.Serializable{
   public static final int ATTACK = 1;
   
   private String message;
   private Boss boss;
   private float damage;
   
   public Response(String message, Boss boss, float damage){
      this.message = message;
	  this.boss  = boss;
	  this.damage = damage;
   }
   public String getMessage(){
      return this.message;
   }
   public Boss getBoss(){
      return this.boss;
   }
   public float getDamage(){
   	return this.damage;
   }
   public void setMessage(String message){
      this.message = message;
   }
   
}
