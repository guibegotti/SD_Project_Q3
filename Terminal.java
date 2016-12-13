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
    	int op1, op2=0;
    	Request req = null;
    	Response rep = null;
    	Character character = null;
    	try{
    		Socket socket = new Socket("localhost",10000);
        
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
		
			 
			 character = new Character("id", "nome", "raça", "classe", 1, 100);
			 do{
			  	System.out.println(">>>>>>>>>>>>> MENU ");
				System.out.println(" 1 lutar");
				System.out.println("-1 para sair");
				 
				op1 = Integer.parseInt(keypad.next());
			 	if(op1 == 1){
			 		req = new Request(Request.FIGHT, character);
			 	}
			 
				ObjectOutputStream obOut;
	  			obOut = new ObjectOutputStream(socket.getOutputStream());
	  			 
				obOut.writeObject(req);
		
				ObjectInputStream obInp;
				obInp = new ObjectInputStream(socket.getInputStream());
			
				rep = (Response)obInp.readObject();
				System.out.println(rep.getMessage());
				
			 }while(op1!=-1);
			 
		 	 
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
