import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;
public class Banco{

	public static void main(String args[]){
		ServerSocket server; 
		Socket       client; 
		HashMap<Integer,Conta> contas;
		Scanner teclado = new Scanner(System.in);
		try{
			System.out.println("Insira o código do banco");
			int banco = Integer.parseInt(teclado.next());
			server = new ServerSocket(banco);		 
			System.out.println("LOG - Servidor no ar na porta "+banco);
			contas = new HashMap<Integer, Conta>(); 

			contas.put(1, new Conta(1, 10));
			contas.put(2, new Conta(2, 20));

			Socket socket2 = new Socket("localhost",9999); 
			
			RequisicaoStatus r = new RequisicaoStatus(RequisicaoStatus.ONLINE, banco, banco);
			ObjectOutputStream obOutStatus;
			obOutStatus = new ObjectOutputStream(socket2.getOutputStream());
			obOutStatus.writeObject(r);
			while(true){
				client = server.accept();
				System.out.println("LOG - Cliente conectado - "+client.getInetAddress().toString());

				ObjectInputStream obInput;
				obInput = new ObjectInputStream(client.getInputStream());
				Requisicao req = (Requisicao) obInput.readObject(); // recebi o objeto requisicao
				System.out.println("LOG - Requisicao recebida - "+req.getCodigo());

				int resultado=0;
				Conta conta = contas.get(req.getContaOrigem());

				if(conta!=null){
					switch(req.getCodigo()){
					case Requisicao.CONSULTA:
						resultado = Resposta.OP_REALIZADA;				 
						break;
					case Requisicao.SAQUE:
						conta.setSaldo(conta.getSaldo() - req.getValor());
						if(conta.getSaldo() >= 0){
							contas.put(req.getContaOrigem(), conta);
							resultado = Resposta.OP_REALIZADA;
						}
						else{
							conta = null;
							resultado = Resposta.SALDO_INSUFICIENTE;
						}

						break;
					case Requisicao.DEPOSITO:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						contas.put(req.getContaOrigem(), conta);
						resultado = Resposta.OP_REALIZADA;

						break;
					case Requisicao.TRANSF_IN:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						resultado = Resposta.OP_REALIZADA;
						break;

					case Requisicao.TRANSF_OUT:
						conta.setSaldo(conta.getSaldo() - req.getValor());
						if(conta.getSaldo() >= 0){
							if(req.getNumeroBanco() == 0){
								contas.put(req.getContaOrigem(), conta);
								Conta contaDestino = contas.get(req.getContaDestino());
								contaDestino.setSaldo(contaDestino.getSaldo() - req.getValor());
								contas.put(contaDestino.getNumero(), contaDestino); 
								resultado = Resposta.OP_REALIZADA;
							}
							else{

								if (socket2 != null){
									RequisicaoStatus reqBC = new RequisicaoStatus(RequisicaoStatus.GET,req.getNumeroBanco(),  0);
									Socket socket3= new Socket("localhost",9999); 
									ObjectOutputStream obOutBC;
									obOutBC = new ObjectOutputStream(socket3.getOutputStream());
									
									obOutBC.writeObject(reqBC);

									ObjectInputStream obInpBC;
									obInpBC = new ObjectInputStream(socket3.getInputStream());

									RespostaStatus repBC = (RespostaStatus)obInpBC.readObject();
									System.out.println("LOG - Porta do banco adquirida - " + repBC.getResultado());
									
									Socket socket4= new Socket("localhost",repBC.getPorta());
									if(socket4!=null){
										ObjectOutputStream obOutReq;
										obOutReq = new ObjectOutputStream(socket4.getOutputStream());
										Requisicao req3 = new Requisicao(Requisicao.TRANSF_IN, req.getContaDestino(), 0, req.getNumeroBanco(), req.getValor());
										obOutReq.writeObject(req3);
										ObjectInputStream obInpReq;
										obInpReq = new ObjectInputStream(socket4.getInputStream());

										Resposta repReq = (Resposta)obInpReq.readObject();
										resultado = repReq.getResultado();
										socket4.close();
									} 
								}
							}
						}
						else{
							conta = null;
							resultado = Resposta.SALDO_INSUFICIENTE;
						}
					default:
						resultado = Resposta.OP_INVALIDA;
					}
				}
				else
					resultado = Resposta.OP_INVALIDA;

				ObjectOutputStream obOutput;
				obOutput = new ObjectOutputStream(client.getOutputStream());
				Resposta rep = new Resposta(resultado, conta); 
				obOutput.writeObject(rep);     			

				obInput.close();
				obOutput.close();

			}				 
		}
		catch(Exception ex){
			System.err.println(" Erro ");
			ex.printStackTrace();
		}
	}
}
