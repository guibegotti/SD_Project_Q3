import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;


public class Banco{
	
	//Método que percorre a lista de Contas e retorna o indice onde e encontrada determinada conta
	//Na lista, um elemento i (par) indica um numero de conta e o elemento i+1 (impar) o saldo referente a ela
	public static int getConta(List<String> listaContas, int numConta){
		for(int i=0; i<listaContas.size(); i+=2){
			if(Integer.parseInt(listaContas.get(i)) == numConta)
				return i;
		}
		return -1;
	}

	public static void main(String args[]){
		ServerSocket server; 
		Socket       client; 
		Scanner teclado = new Scanner(System.in);
				
		try{		
			System.out.println("Insira o código do banco");
			int banco = Integer.parseInt(teclado.next());
			
			//Recuperar o registro de contas referente ao banco inserido. Se esse registro não existe, cria-lo.
			File registro = new File(Integer.toString(banco)+".txt");
			if(!registro.exists())
				registro.createNewFile();
				
			//Ler o conteudo do registro de contas, colocando os dados em uma lista
			List<String> listaContas = new ArrayList<>(Files.readAllLines(Paths.get(Integer.toString(banco)+".txt"), StandardCharsets.UTF_8));
			
			//Iniciar o servidor do banco
			server = new ServerSocket(0);	
			int porta = server.getLocalPort();	 
			System.out.println("LOG - Servidor no ar na porta "+porta);
			
			//Informar ao banco central que o servidor do banco esta ativo
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
				//Aceitar a conexao de clientes e receber suas respectivas requisicoes
				client = server.accept();
				System.out.println("LOG - Cliente conectado - "+client.getInetAddress().toString());
				ObjectInputStream obInput;
				obInput = new ObjectInputStream(client.getInputStream());
				Requisicao req = (Requisicao) obInput.readObject();
				System.out.println("LOG - Requisicao recebida - "+req.getCodigo());

				int resultado=0;
				int  indexConta = getConta(listaContas, req.getContaOrigem());
				Conta conta = null;
				
				//Se foi feita uma requisicao de abertura de conta, e o numero de conta solicitado esta disponivel, 
				//adicionar esta conta ao registro de contas, com saldo 0. Caso contrario, retornar operacao invalida
				if (req.getCodigo() == Requisicao.NOVA_CONTA){
					if(indexConta != -1){
						resultado = Resposta.OP_INVALIDA;
					}
					else{
						listaContas.add(Integer.toString(req.getContaOrigem()));
						listaContas.add("0");
						Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
						resultado = Resposta.OP_REALIZADA;
					}				
				}
				
				else if(indexConta != -1){
					conta = new Conta(Integer.parseInt(listaContas.get(indexConta)), Integer.parseInt(listaContas.get(indexConta+1)));
					switch(req.getCodigo()){
					//Para a consulta, basta retornar operacao realizada, ja que a consulta foi feita e encontrou-se
					//uma conta com o numero solicitado, ja que indexConta != -1
					case Requisicao.CONSULTA:
						resultado = Resposta.OP_REALIZADA;				 
						break;
					//Para o saque, apenas é realizada a operacao caso o saldo da conta apos a operacao seja positivo
					case Requisicao.SAQUE:
						if(conta.getSaldo() - req.getValor() >= 0){
							resultado = Resposta.OP_REALIZADA;
							conta.setSaldo(conta.getSaldo() - req.getValor());
							listaContas.set(indexConta+1, Integer.toString(conta.getSaldo()));
							Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
						}
						else{
							conta = null;
							resultado = Resposta.SALDO_INSUFICIENTE;
						}

						break;
					//Para o deposito, adiciona-se a quantia requirida ao saldo referente ao numero de conta da 
					//requisicao
					case Requisicao.DEPOSITO:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						listaContas.set(indexConta+1, Integer.toString(conta.getSaldo()));
						Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
						resultado = Resposta.OP_REALIZADA;
						break;
					//Transferencias vindas de outros bancos possuem funcionamento similar ao do deposito
					case Requisicao.TRANSF_IN:
						conta.setSaldo(conta.getSaldo() + req.getValor());
						listaContas.set(indexConta+1, Integer.toString(conta.getSaldo()));
						Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
						resultado = Resposta.OP_REALIZADA;
						break;
					//Transferencias solicitada apenas ocorrem caso o saldo do cliente que a
					//solicitou seja suficiente para a operacao
					case Requisicao.TRANSF_OUT:									
						if(conta.getSaldo()-req.getValor() >= 0){
							//Se a transferencia e entre contas de um mesmo banco, recuperar a conta destino
							//e realizar a operacao
							if(req.getNumeroBanco() == 0){
								int indexDestino = getConta(listaContas, req.getContaDestino());
								if(indexDestino!=-1){
									int saldoDestino = Integer.parseInt(listaContas.get(indexDestino+1));									
									conta.setSaldo(conta.getSaldo() - req.getValor());
									listaContas.set(indexConta+1, Integer.toString(conta.getSaldo()));
									listaContas.set(indexDestino+1, Integer.toString(saldoDestino + req.getValor()));
									Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
									resultado = Resposta.OP_REALIZADA;
								}
								else{
									resultado = Resposta.NOT_FOUND;
								}
								
							}
							//Se a transferencia e entre bancos diferentes
							else{
								try{
									//Abrir uma conexao com o banco central e solicitar a porta para 
									//conectar-se com o banco destino
									RequisicaoStatus reqBC = new RequisicaoStatus(RequisicaoStatus.GET,req.getNumeroBanco(),  0);
									Socket socket3= new Socket("localhost",9999); 
									ObjectOutputStream obOutBC;
									obOutBC = new ObjectOutputStream(socket3.getOutputStream());
									
									obOutBC.writeObject(reqBC);

									ObjectInputStream obInpBC;
									obInpBC = new ObjectInputStream(socket3.getInputStream());

									RespostaStatus repBC = (RespostaStatus)obInpBC.readObject();
									System.out.println("LOG - Porta do banco adquirida - " + repBC.getResultado());
									//Tentar conectar-se com a porta recebida, solicitando uma 
									//transferencia
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
										//Se a resposta do outro banco foi de operacao concluida
										//realizar as alteracoes na conta de origem
										if(resultado == 0){
											conta.setSaldo(conta.getSaldo() - req.getValor());					
											listaContas.set(indexConta+1, Integer.toString(conta.getSaldo()));
											Files.write(Paths.get(Integer.toString(banco)+".txt"), listaContas, StandardCharsets.UTF_8);
										}
										socket4.close();
									} 
								}
								catch(Exception ex){
									resultado = Resposta.NOT_FOUND;
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
					resultado = Resposta.NOT_FOUND;

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
