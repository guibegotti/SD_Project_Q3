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
			
				ObjectInputStream obInput;
				
				obInput = new ObjectInputStream(client.getInputStream());
				Request req = (Request) obInput.readObject();
				System.out.println("LOG - Requisicao recebida - "+req.getCode());
				
				Character character = null;
				Boss boss = null;
				String message="";
				float damage=0;
				
				switch(req.getCode()){
					case Request.FIGHT:
						boss = new Boss("a", req.getCharacter().getLevel()*50);
						message = "O chefe se aproxima!";
						damage = 0;
						break;
					case Request.ATTACK:
						boss.setHp(boss.getHp() - req.getCharacter().getLevel()*10);
						damage =  boss.getHp()/10;
						message = ""+req.getCharacter().getLevel()*10+" de dano em "+boss.getName()
							  +"\n"+boss.getName()+" te ataca, causando" + damage + "de dano";
						boss.setHp(boss.getHp() - req.getCharacter().getLevel()*10);
					default:
						System.err.println("*-----------*Erro*-----------*");
				}
				ObjectOutputStream obOutput;
				obOutput = new ObjectOutputStream(client.getOutputStream());
				Response rep = new Response(message, boss, damage); 
				obOutput.writeObject(rep);     
				
				obInput.close();
				obOutput.close();
        	}		 
	  	}
	  	catch(Exception ex){
	  		System.err.println("*-----------*Erro*-----------*");
			ex.printStackTrace();
	  	}
   	}
}
