public class RespostaStatus implements java.io.Serializable{
   public static final int FOUND    = 0;
   public static final int NOT_FOUND = 1;
   
   private int   resultado;
   private int porta;
   
   public RespostaStatus(int resultado, int porta){
      this.resultado = resultado;
      this.porta = porta;
   }
   public int getResultado(){
      return this.resultado;
   }
   public int getPorta(){
   	return this.porta;
   }
   public void setResultado(int resultado){
      this.resultado = resultado;
   }
}
