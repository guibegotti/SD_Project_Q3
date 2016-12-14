import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Terminal{
	public static void main(String args[]){
		Scanner teclado = new Scanner(System.in);
		int opcao;
		Requisicao req=null;
		Resposta   rep=null;
		try{
			System.out.println("Insira a porta do banco");
			int banco = Integer.parseInt(teclado.next());
			Socket socket = new Socket("localhost",banco); 

			if (socket != null){

				System.out.println(">>>>>>>>>>>>> MENU ");
				System.out.println("-1 para sair");
				System.out.println(" 0 para consulta");
				System.out.println(" 1 para saque");
				System.out.println(" 2 para deposito");
				System.out.println(" 3 para transferencia");
				System.out.println(" 4 para criar conta");
				opcao = Integer.parseInt(teclado.next());
				if (opcao != -1){
					if(opcao == 0){
						System.out.println("\nInsira o numero de sua conta:");
						int num = Integer.parseInt(teclado.next());
						req = new Requisicao(Requisicao.CONSULTA, num, 0,0,0);
					}
					else if(opcao == 1){
						System.out.println("\nInsira o numero de sua conta:");
						int num = Integer.parseInt(teclado.next());
						System.out.println("Insira o valor do saque:");
						int valor = Integer.parseInt(teclado.next());
						req = new Requisicao(Requisicao.SAQUE, num, 0,0,valor);
					}		

					else if (opcao == 2){
						System.out.println("\nInsira o numero de sua conta:");
						int num = Integer.parseInt(teclado.next());
						System.out.println("Insira o valor do deposito:");
						int valor = Integer.parseInt(teclado.next());
						req = new Requisicao(Requisicao.DEPOSITO, num, 0,0,valor);    
					}
					else if (opcao == 3){
						System.out.println("\nInsira o numero de sua conta:");
						int numOrigem = Integer.parseInt(teclado.next());
						System.out.println("Insira o numero da conta destino:");
						int numDestino = Integer.parseInt(teclado.next());
						System.out.println("Insira o codigo do banco destino (0 para transferencia interna):");
						int codBanco = Integer.parseInt(teclado.next());
						System.out.println("Insira o valor da transferencia:");
						int valor = Integer.parseInt(teclado.next());
						req = new Requisicao(Requisicao.TRANSF_OUT, numOrigem, numDestino,codBanco,valor); 			     	
					}
					else if(opcao == 4){
						System.out.println("\nInsira o numero da nova conta:");
						int num = Integer.parseInt(teclado.next());
						req = new Requisicao(Requisicao.NOVA_CONTA, num, 0, 0, 0);
					}

					ObjectOutputStream obOut;
					obOut = new ObjectOutputStream(socket.getOutputStream());
					
					obOut.writeObject(req);

					ObjectInputStream obInp;
					obInp = new ObjectInputStream(socket.getInputStream());

					rep = (Resposta)obInp.readObject();
					System.out.println("\nLOG - Resposta recebida - "+rep.getResultado());
					switch(rep.getResultado()){
						case Resposta.OP_REALIZADA:
							System.out.println("Operação realizada com sucesso");
							break;
						case Resposta.OP_INVALIDA:
							System.out.println("Operação inválida");
							break;
						case Resposta.SALDO_INSUFICIENTE:
							System.out.println("Saldo insuficiente");
							break;
						case Resposta.NOT_FOUND:
							System.out.println("Conta não encontrada");
							break;
						case Resposta.OP_INDISPONIVEL:
							System.out.println("Operações entre bancos diferentes não estão disponiveis");
							break;
					}
					if(rep.getConta()!=null){
						System.out.println("Numero da conta: "+rep.getConta().getNumero());
						System.out.println("Saldo          : "+rep.getConta().getSaldo());
					}
				}	 

				socket.close();	
			}
			else{
				System.out.println("LOG - Servidor indisponivel");
			}
		}
		catch(Exception ex){
			System.out.println(" ERRO ");
			ex.printStackTrace();
		}
	}
}
