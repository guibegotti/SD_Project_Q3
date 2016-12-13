public class Conta implements java.io.Serializable{
	private int numero;
	private int saldo;

	public Conta(int numero, int saldo){
		this.numero = numero;
		this.saldo = saldo;
	}
	
	public int getNumero(){
		return this.numero;
	}
	
	public int getSaldo(){
		return this.saldo;
	}
	public void setSaldo(int saldo){
		this.saldo = saldo;
	}
}
