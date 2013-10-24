import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

public class MiddleBrokerServer extends Thread{//implements Runnable{

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9002;
    
    private static final int serverPort = 9001;

    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    public static HashSet<String> names = new HashSet<String>();

    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
    private Socket socket              = null;
    private Thread thread              = null;
    private DataInputStream  console   = null;
    private DataOutputStream streamOut = null;
    BufferedReader ssIn = null;
    PrintWriter ssOut = null;

    OutputStream outStream = null;
	ObjectOutputStream objectOut = null;
    

    private static ArrayList<Item> itemList = new ArrayList<Item>();
    
    public HashSet<PrintWriter> getWriters(){
    	return writers;
    }

    /**
     * The application main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        MiddleBrokerServer mBroker = new MiddleBrokerServer();
        //mBroker.start();
        ServerSocket listener = new ServerSocket(PORT);
        
        System.out.println("The Middle Broker server is running at port " + PORT);
        
        try {
        	System.out.println("accepting connections");
            while (true) {
                new Handler(listener.accept(), mBroker).start();
            }
        } finally {
            listener.close();
        }
    }
    
    public MiddleBrokerServer() throws IOException {
    	try {
    		socket = new Socket("localhost", serverPort);
    		
    		System.out.println("Opened socket at port " + serverPort);
            
            // Make connection and initialize streams
 	       	ssIn = new BufferedReader(new InputStreamReader(
 	       									socket.getInputStream()));
 	       	ssOut = new PrintWriter(socket.getOutputStream(), true);
 	       	
 	       	outStream = socket.getOutputStream();
 	       	
 	       	objectOut = new ObjectOutputStream(outStream);
 	       	
 	       	objectOut.flush();
 	       	
 	       	/*System.out.println("created streams");
 	       	String line = ssIn.readLine();
 	       	System.out.println("read in something from parent server");
 	       	System.out.println(line);
        	
 	       	ssOut.println("server");
 	       	
 	       	System.out.println("Connected: " + socket);*/
 	       	
 	       	MiddleBrokerThread a = new MiddleBrokerThread(this, socket, ssIn, ssOut);
 	       	a.start();
    	}
    	finally{
    		
    	}
    	
    }
    
    public BufferedReader getServerReader(){
    	return ssIn;
    }
    
    public PrintWriter getServerWriter(){
    	return ssOut;
    }
    
    public void run(){
    	// do nothing
    }
    
    public void sendMessageToHead(String a){
    	System.out.println("sending stuff to parent server");
    	ssOut.println("Server : " + a);
    }
    
    // ------------------------------ server stuff ----------------------------
    
    
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
        private MiddleBrokerServer s;
        
        private String clientType;
        
        private InputStream inStream;
        private ObjectInputStream objectIn;
        
        private BufferedReader ssIn;
        private PrintWriter ssOut;

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket, MiddleBrokerServer s) {
            this.socket = socket;
            this.s = s;
            this.ssIn = s.getServerReader();
            this.ssOut = s.getServerWriter();
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                inStream = socket.getInputStream();
	        	objectIn = new ObjectInputStream(inStream);
                
                // verifies what type the incoming socket is                
                out.println("What kind of client are you?");
            	String outputLine;
            	clientType = in.readLine();
                
            	if(clientType.equals("seller")){
            		ssOut.println("sellerNumber");
            		String _sellerNumber = ssIn.readLine();
            		System.out.println("connected to SELLER" + _sellerNumber);
            		out.println(_sellerNumber);
            		
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
            		
            	}
            } catch (IOException e) {
                System.out.println(e);
            } catch (Exception e){
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