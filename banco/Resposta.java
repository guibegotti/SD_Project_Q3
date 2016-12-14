public class Resposta implements java.io.Serializable{
   public static final int OP_REALIZADA    = 0;
   public static final int OP_INVALIDA = 1;
   public static final int NOT_FOUND   = 2;
   public static final int SALDO_INSUFICIENTE = 3;
   public static final int OP_INDISPONIVEL = 4;
   
   private int   resultado;
   private Conta conta;
   
   public Resposta(int resultado, Conta conta){
      this.resultado = resultado;
      this.conta = conta;
   }
   public int getResultado(){
      return this.resultado;
   }
   public Conta getConta(){
   	return this.conta;
   }
   public void setResultado(int resultado){
      this.resultado = resultado;
   }
}
