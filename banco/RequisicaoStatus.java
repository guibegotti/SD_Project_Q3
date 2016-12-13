public class RequisicaoStatus implements java.io.Serializable{
	public static final int ONLINE = 1;
	public static final int OFFLINE = 2;
	public static final int GET = 3;

	private int codigo;
	private int numeroBanco;
	private int portaBanco;

	public RequisicaoStatus(int codigo, int numeroBanco, int portaBanco){
		this.codigo = codigo;
		this.numeroBanco = numeroBanco;
		this.portaBanco = portaBanco;
	}
	public int getCodigo(){
		return this.codigo;
	}
	public int getNumeroBanco(){
		return this.numeroBanco;
	}

	public int getPortaBanco(){
		return this.portaBanco;
	}

}
