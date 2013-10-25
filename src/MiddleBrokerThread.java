import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class MiddleBrokerThread extends Thread
{  
	private Socket           socket   = null;
	private DataInputStream  streamIn = null;
	BufferedReader ssIn = null;
	PrintWriter ssOut = null;
	MiddleBrokerServer b = null;
	
	ObjectInputStream ssObjectIn = null;
	ObjectOutputStream ssObjectOut = null;
	
	int portNumber;
   

   public MiddleBrokerThread(MiddleBrokerServer a, Socket _socket, BufferedReader _inServer, PrintWriter _outServer, ObjectInputStream _ssObjectIn, ObjectOutputStream _ssObjectOut, int portNumber){
	   b = a;
	   socket   = _socket;
	   ssIn = _inServer;
	   ssOut = _outServer;
	   ssObjectIn = _ssObjectIn;
	   ssObjectOut = _ssObjectOut;
	   this.portNumber = portNumber;
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
		   
		   ssOut.println(portNumber);
            
		   System.out.println("Connected!!!");
		   //String clientNumber = inServer.readLine(); 	
		   
		   while(true){
			   //String asdf = ssIn.readLine();
			   //System.out.println("what" + asdf);
			   //Message message = (Message) ssObjectIn.readObject();
			   //ArrayList<Item> i	temList = b.getItemList();
			   //b.match(item);
		   }
	   }
	   catch(IOException ioe){  
		   	System.out.println("Sending error: " + ioe.getMessage());
	   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   }
   }
}