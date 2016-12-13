public class Requisicao implements java.io.Serializable{
   public static final int SAQUE    = 1;
   public static final int DEPOSITO = 2;
   public static final int CONSULTA = 3;
   public static final int TRANSF_IN   = 4;
   public static final int TRANSF_OUT  = 5;
   
   private int      codigo;
   private int contaOrigem;
   private int contaDestino;
   private int numeroBanco;
   private int valor;
   
   public Requisicao(int      codigo, int contaOrigem, int contaDestino, int numeroBanco, int valor){
   	this.codigo = codigo;
  	this.contaOrigem = contaOrigem;
  	this.contaDestino = contaDestino;
   	this.numeroBanco = numeroBanco;
	this.valor = valor;
   }
   
   public int getCodigo(){
   	return this.codigo;
   }
   
    public int getContaOrigem(){
   	return this.contaOrigem;
   }
   
    public int getContaDestino(){
   	return this.contaDestino;
   }
   
    public int getNumeroBanco(){
   	return this.numeroBanco;
   }
   
    public int getValor(){
   	return this.valor;
   }
}
