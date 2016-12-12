import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.Random;

public class Terminal{

	public static int randInt(){
	    // NOTE: Como vamos abrir vários "terminais", é legal dar um nome para cada localhost
    	// Se quiserem tirar isso não tem problema...
		Random rand = new Random();
    	int num = rand.nextInt((9999 - 1) + 1) + 1;
    	return num;
	}


    public static void main(String args[]){

    	int local_id = randInt();
    	Scanner keypad = new Scanner(System.in);
    	try{
    		Socket socket = new Socket("localhost_"+local_id,10000);
        
        	if (socket != null){
		 
		 	/*TODO: 
		 	 -  Tratamento de conexão: definir se é o mestre ou um jogador
		 	 -  Entrar como mestre: Manipula dados como hp e lvl de acordo
		 	 	com a resposta do jogador e envia mensagens pros outros jogadores (broadcast).
		 	 -	Entrar como jogador: Entrar na seção, cadastrar personagem (mestre recebe uma 
		 	 	mensagem de entrada e cadastramento) e responde perguntas com sua ações.
		 	 	* Respostas serão aceitas até o mestre fechar a pergunta

		 	 	* Não sei se esseé o melhor jeito, caso tenham outra ideia melhor, vamos discutir no grupo.
		 	 */

            	socket.close();	
			}
			else{
				System.out.println("LOG - Servidor indisponivel");
			}
		}
		catch(Exception ex){
		    System.out.println("*-----------*Erro*-----------*");
			ex.printStackTrace();
		}
  	}
}