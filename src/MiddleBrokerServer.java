import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.JScrollPane;

/**
 * A multithreaded chat room server.  When a client connects the
 * server requests a screen name by sending the client the
 * text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received.  After a client submits a unique
 * name, the server acknowledges with "NAMEACCEPTED".  Then
 * all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name.  The
 * broadcast messages are prefixed with "MESSAGE ".
 *
 * Because this is just a teaching example to illustrate a simple
 * chat server, there are a few features that have been left out.
 * Two are very useful and belong in production code:
 *
 *     1. The protocol should be enhanced so that the client can
 *        send clean disconnect messages to the server.
 *
 *     2. The server should do some logging.
 */
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
    //private ChatClientThread client    = null;
    BufferedReader inServer = null;
    PrintWriter outServer = null;
    
    public HashSet<PrintWriter> getWriters(){
    	return writers;
    }

    /**
     * The application main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        MiddleBrokerServer mBroker = new MiddleBrokerServer();
        mBroker.start();
        System.out.println("The Middle Broker server is running.");
        ServerSocket listener = new ServerSocket(PORT);
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
            System.out.println("Connected: " + socket);
            
            // Make connection and initialize streams
 	       	inServer = new BufferedReader(new InputStreamReader(
 	       			socket.getInputStream()));
 	       	outServer = new PrintWriter(socket.getOutputStream(), true);
 	       	
 	       	MiddleBrokerThread a = new MiddleBrokerThread(this, socket, inServer, outServer);
    	}
    	finally{
    		
    	}
    	
    }
    
    public void run() {
        // Process all messages from server, according to the protocol.
        
    }
    
    public void sendMessageToHead(String a){
    	System.out.println("sending stuff to head");
    	outServer.println("Server : " + a);
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

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket, MiddleBrokerServer s) {
            this.socket = socket;
            this.s = s;
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

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    System.out.println("Connected with : " + name);
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    System.out.println("RECEIVED AND PRINTING FROM MIDDLE: " + input);
                    if (input == null) {
                        return;
                    }
                    String[] a = input.split(" ");
                    
                    // message from head server
                    // distribute to listeners
                    if(a[0].equals("HEAD")){
                    	System.out.println("Relay message to middle broker sellers");
                    	System.out.println("Should relay to " + writers.size() + " writers");
                    	/*for (PrintWriter writer : writers) {
	                        writer.println("MESSAGE " + name + " : " + input);
	                    }*/
                    }
                    // message from a client
                    else{
	                    s.sendMessageToHead("Seller : " + name + " : " + input);
	                    for (PrintWriter writer : writers) {
	                        writer.println("MESSAGE " + name + " : " + input);
	                    }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
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