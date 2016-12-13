import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class BancoCentral{

	public static void main(String args[]){
		ServerSocket server; // socket para aguardar pedidos de conexao
		Socket       client; // socket para comunicar com o cliente
		HashMap<Integer,Integer> bancos;

		try{
			bancos = new HashMap<Integer,Integer>();
			server = new ServerSocket(9999);		 
			System.out.println("LOG - Servidor no ar na porta 9999");
			while(true){
				client = server.accept();
				System.out.println("LOG - Cliente conectado - "+client.getInetAddress().toString());

				ObjectInputStream obInput;
				obInput = new ObjectInputStream(client.getInputStream());
				RequisicaoStatus req = (RequisicaoStatus) obInput.readObject(); 
				System.out.println("LOG - Requisicao recebida - "+req.getCodigo());

				int resultado=0, porta =0;

				switch(req.getCodigo()){
				case RequisicaoStatus.GET:
					System.out.println("***"+req.getNumeroBanco());
					porta = bancos.get(req.getNumeroBanco());
					System.out.println("___"+porta);
					if(porta!=0){
						resultado = RespostaStatus.FOUND;
					}	
					else
						resultado = RespostaStatus.NOT_FOUND;		 
					break;
				case RequisicaoStatus.ONLINE:
					bancos.put(req.getNumeroBanco(), req.getPortaBanco());
					System.out.println("Banco "+req.getNumeroBanco()+" online");
					/*
				case Requisicao.DEPOSITO:

				   break;

				case Requisicao.TRANSF:
					 */
				default:
					resultado = Resposta.NOT_FOUND;
				}

				ObjectOutputStream obOutput;
				obOutput = new ObjectOutputStream(client.getOutputStream());
				RespostaStatus rep = new RespostaStatus(resultado, porta); 
				obOutput.writeObject(rep);           		
			}	

		}
		catch(Exception ex){
			System.err.println(" Erro ");
			ex.printStackTrace();
		}
	}
}
