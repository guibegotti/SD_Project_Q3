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
			server = new ServerSocket(0);	
			int porta = server.getLocalPort();	 
			System.out.println("LOG - Servidor no ar na porta "+porta);
			contas = new HashMap<Integer, Conta>(); 

			contas.put(1, new Conta(1, 10));
			contas.put(2, new Conta(2, 20));

			try{
				Socket socket2 = new Socket("localhost",9999); 
			
				RequisicaoStatus r = new RequisicaoStatus(RequisicaoStatus.ONLINE, banco, porta);
				ObjectOutputStream obOutStatus;
				obOutStatus = new ObjectOutputStream(socket2.getOutputStream());
				obOutStatus.writeObject(r);
			}
			catch(Exception ex){
				System.err.println("Banco Central indisponivel. Apenas transações internas disponiveis");
			}
			while(true){
				client = server.accept();
				System.out.println("LOG - Cliente conectado - "+client.getInetAddress().toString());

				ObjectInputStream obInput;
				obInput = new ObjectInputStream(client.getInputStream());
				Requisicao req = (Requisicao) obInput.readObject(); // recebi o objeto requisicao
				System.out.println("LOG - Requisicao recebida - "+req.getCodigo());

				int resultado=0;
				Conta conta = contas.get(req.getContaOrigem());
				
				if (req.getCodigo() == Requisicao.NOVA_CONTA){
					contas.put(req.getContaOrigem(), new Conta(req.getContaOrigem(), 0));
					resultado = Resposta.OP_REALIZADA;				
				}
				
				else if(conta!=null){
					switch(req.getCodigo()){
					case Requisicao.CONSULTA:
						resultado = Resposta.OP_REALIZADA;				 
						break;
					case Requisicao.SAQUE:
						if(conta.getSaldo() - req.getValor() >= 0){
							resultado = Resposta.OP_REALIZADA;
							conta.setSaldo(conta.getSaldo() - req.getValor());
						}
						else{
							conta = null;
							resultado = Resposta.SALDO_INSUFICIENTE;
						}

						break;
					case Requisicao.DEPOSITO:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						resultado = Resposta.OP_REALIZADA;

						break;
					case Requisicao.TRANSF_IN:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						resultado = Resposta.OP_REALIZADA;
						break;

					case Requisicao.TRANSF_OUT:									
						if(conta.getSaldo()-req.getValor() >= 0){
							if(req.getNumeroBanco() == 0){
								Conta contaDestino = contas.get(req.getContaDestino());
								if(contaDestino!=null){
									contaDestino.setSaldo(contaDestino.getSaldo() - req.getValor());
									conta.setSaldo(conta.getSaldo() - req.getValor());			
									resultado = Resposta.OP_REALIZADA;
								}
								else{
									resultado = Resposta.OP_INVALIDA;
								}
								
							}
							else{
								try{
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
										if(resultado == 0)
											conta.setSaldo(conta.getSaldo() - req.getValor());			
										socket4.close();
									} 
								}
								catch(Exception ex){
									resultado = Resposta.OP_INDISPONIVEL;
									System.err.println("Banco Central indisponivel. Apenas transações internas disponiveis");									
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
