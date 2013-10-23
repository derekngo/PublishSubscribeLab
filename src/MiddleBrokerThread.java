import java.net.*;
import java.io.*;

public class MiddleBrokerThread extends Thread
{  
	private Socket           socket   = null;
   	//private ChatClient       client   = null;
	private DataInputStream  streamIn = null;
	BufferedReader ssIn = null;
	PrintWriter ssOut = null;
	MiddleBrokerServer b = null;
   

   public MiddleBrokerThread(MiddleBrokerServer a, Socket _socket, BufferedReader _inServer, PrintWriter _outServer)
   {  //client   = _client;
	   b = a;
      socket   = _socket;
      ssIn = _inServer;
      ssOut = _outServer;
      //open();  
      //start();
   }
   /*public void open(){
	   try{  
		   
	   }
	   catch(IOException ioe){
		   System.out.println("Error getting input stream: " + ioe);
		   //client.stop();
	   }
   }
   public void close()
   {  try
      {  if (streamIn != null) streamIn.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing input stream: " + ioe);
      }
   }*/
   public void run(){  
	   
	   try{
		   String line = ssIn.readLine();
		   //b.sendMessageToHead(line);
        	
		   ssOut.println("server");
            
		   System.out.println("Connected!!!");
		   //String clientNumber = inServer.readLine();   
	   }
	   catch(IOException ioe){  
		   System.out.println("Sending error: " + ioe.getMessage());
	   }
       
	   while (true) {
		   
	   }
   }
}