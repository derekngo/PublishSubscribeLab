// http://stackoverflow.com/questions/7022063/java-listening-to-a-socket-with-objectinputstream

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class BrokerServer {

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
        private OutputStream outStream;
        private ObjectOutputStream objectOut;

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
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
	        	objectIn = new ObjectInputStream(inStream);
	        	objectOut = new ObjectOutputStream(outStream);
	        	objectOut.flush();
                BufferedReader tempReader = new BufferedReader(new InputStreamReader(inStream));
                
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
            		System.out.println("connected to BUYER" + buyerNumber);
            		out.println(buyerNumber);
            		buyerNumber++;
            		
            		while(true){
            			String action = in.readLine();
	            		if(action.equals("searchItem")){
	            			Item item = (Item) objectIn.readObject();
	            			System.out.println("ITEM FOUND");
	            			ArrayList<Item> matches = match(item);
	            			objectOut.writeObject(matches);
	            		}else if(action.equals("bidItem")){
	            			
	            		}
            		}
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
        
        // dumb solution for now
        public ArrayList<Item> match(Item item){
        	ArrayList<Item> matches = new ArrayList<Item>();
        	
        	for(Item i: itemList){
        		if(i.compareMatch(item)){
        			matches.add(i);
        		}
        	}
        	
        	return matches;
        }
    }
}