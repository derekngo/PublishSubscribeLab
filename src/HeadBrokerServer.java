// http://stackoverflow.com/questions/7022063/java-listening-to-a-socket-with-objectinputstream

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class HeadBrokerServer {

	// the port that the server listens on
	// reset in the arguments
    private static int port = 9001;

    // set of all names of clients in the chat room -used to verify unique names
    private static HashSet<String> names = new HashSet<String>();

    // used to broadcast messages to a group
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
    private static PrintWriter seller = null;
    
    private static PrintWriter serverWriter = null;
    
    // new stuff
    private static int sellerNumber = 1;
    private static int buyerNumber = 1;
    private static ArrayList<Item> itemList = new ArrayList<Item>();

    // The application main method; listens on a port and spawns handler threads
    public static void main(String[] args) throws Exception {
    	// parse arguments
    	if (args.length == 1){
    		port = Integer.parseInt(args[0]);
            System.out.println("The argument is " + port);
    	}
    	
        System.out.println("The head server is running on port " + port);
        ServerSocket listener = new ServerSocket(port);
        
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private boolean server;

        private String clientType;
        
        private InputStream inStream;
        private ObjectInputStream objectIn;

        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        // http://stackoverflow.com/questions/9826267/cannot-get-objectinputstream-to-work
        public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Hi1");
                inStream = socket.getInputStream();
                System.out.println("Hi2");
	        	objectIn = new ObjectInputStream(inStream);
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(inStream));
	        	System.out.println("Hi3");
                
                server = false;
                
                // verifies what type the incoming socket is                
                out.println("What kind of client are you?");
            	String outputLine;
            	
            	clientType = in.readLine();
            	
            	if(clientType.equals("seller")){
            		System.out.println("connected to SELLER" + sellerNumber);
            		out.println(sellerNumber);
            		sellerNumber++;
            		
            		while(true){
            			String action = in.readLine();
	            		if(action.equals("marketItem")){
	            			Item item = (Item) objectIn.readObject();
	            			itemList.add(item);
	            			out.println("Item is now on the Market");
	            		}else if(action.equals("sellItems")){
	            			
	            		}
            		}
            	}
            	else if(clientType.equals("buyer")){
            		System.out.println("connected to BUYER");
            		// still needs to be implemented
            		System.out.println("Please implement the buyer");
            	}
            	else if(clientType.equals("server")){
            		System.out.println("connected to SERVER");
            		
            		while(true){
            			String action = in.readLine();
            			
            			synchronized(this){
	            			if(action.equals("buyerNumber")){
	            				out.println(buyerNumber);
	            				buyerNumber++;
	            			}
	            			else if(action.equals("sellerNumber")){
	            				out.println(sellerNumber);
	            				sellerNumber++;
	            			}
            			}
            		}
            	}

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                if(server){
                	serverWriter = out;
                	System.out.println("connected to middle server");
                	
                	// FIX THIS LINE OF CODE HELLOOOOOOOO
                    server = false;
                }
                else{
                	writers.add(out);
                	System.out.println("Connected to a client: " + name);
                }
                
                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    String[] a = input.split(" ");
                    //System.out.println(a[0]);
                    if(a[0].equals("Server")){
                    	if(a.length > 1 && a[2].equals("Seller")){
                    //if(server){
                    	// event from seller
                    	if(input.contains("Seller")){
                    		System.out.println("relaying seller: " + input);
                    		// double sends the message
                    		// need to keep from sending the message back
                    		for (PrintWriter writer : writers) {
    	                        writer.println("HEAD SERVER: MESSAGE " + name + ": " + input);
    	                    }
                    	}
                    	// catch an event
                    	else{
	                    	System.out.print("Message from middle server (events): " + input);
                    	}
                    	
                    }}
                    // event from direct children
                    else{
                    	System.out.println("Message from: " + name + " : " + input);
                    	// ***
                    	serverWriter.println("HEAD Message from: " + name + " : " + input);
                    	System.out.println("writers: " + writers.size());
	                    for (PrintWriter writer : writers) {
	                        writer.println("HEAD SERVER: MESSAGE " + name + ": " + input);
	                    }
                    }
                    
                }
            } catch (IOException e) {
                System.out.println(e);
            } /*catch (ClassNotFoundException e){
            	System.out.println(e);
            }*/ catch(Exception e){
            	System.out.println(e.getMessage());
            } finally {
            
            
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                	
                }
            }
        }
    }
}