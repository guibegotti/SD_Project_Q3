import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Server{
	public static void main(String args[]){
		ServerSocket server;
	  	Socket       client;
	  	HashMap<String,Character> character_map;

	  	try{
	    	server = new ServerSocket(10000);		 
		 	System.out.println("LOG - Servidor no ar na porta 10000");
		 	character_map = new HashMap<String, Character>();
         	while(true){

				client = server.accept();
				System.out.println("LOG - Cliente conectado - "+client.getInetAddress().toString());
			
				// TODO: Receber e tratar requisições
				// Precisamos definir um protocolo com cada requisção com um id que modificará o hashmap aqui no servidor
			
        	}		 
	  	}
	  	catch(Exception ex){
	  		System.err.println("*-----------*Erro*-----------*");
			ex.printStackTrace();
	  	}
   	}
}