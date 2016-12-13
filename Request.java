public class Request implements java.io.Serializable{
   public static final int INSERT = 1;
   public static final int DELETE = 2;
   public static final int GET	  = 3;
   public static final int FIGHT  = 4;
   public static final int ATTACK = 5;
  
   
   private int       code;
   private Character character;
   
   public Request(int code, Character character){
      this.code = code;
	  this.character = character;
   }
   
   public int getCode(){
      return this.code;
   }
   public Character getCharacter(){
      return this.character;
   }
   public void setCode(int code){    
      this.code = code;
   }
   public void setCharacter(Character character){
      this.character = character;
   }
}
