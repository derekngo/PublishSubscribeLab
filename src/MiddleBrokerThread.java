import java.net.*;
import java.io.*;

public class MiddleBrokerThread extends Thread
{  
	private Socket           socket   = null;
   	//private ChatClient       client   = null;
	private DataInputStream  streamIn = null;
	BufferedReader inServer = null;
	PrintWriter outServer = null;
	MiddleBrokerServer b = null;
   

   public MiddleBrokerThread(MiddleBrokerServer a, Socket _socket, BufferedReader _inServer, PrintWriter _outServer)
   {  //client   = _client;
	   b = a;
      socket   = _socket;
      inServer = _inServer;
      outServer = _outServer;
      //open();  
      start();
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
	   while (true) {
		   try{
	        	String line = inServer.readLine();
	        	System.out.println(line);
	        	
	            outServer.println("Server");
	            
	            for (PrintWriter writer : b.getWriters()) {
                    writer.println("HEAD SERVER: MESSAGE " + line);
                }
	            //break;
       		}
       		catch(IOException ioe)
       		{  
       			System.out.println("Sending error: " + ioe.getMessage());
       		}
       	}
   }
}