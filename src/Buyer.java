import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Buyer implements Client {
	    private static String itemName;
    	private static String attribute;
    	private static double minBid;
    	
    public Buyer() {
    	
    }
    	
    public int acknowledge(BufferedReader in, PrintWriter out){
    	int buyerNumber = -1;
    	
    	try{
    		String line = in.readLine();
    		out.println("buyer");
    		buyerNumber = Integer.parseInt(in.readLine());
    	} 
    	catch(Exception e){
    		System.out.println("ERROR: " + e.getMessage());
    	}
    	
		return buyerNumber;
    }
	
    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	int buyerNumber;
        String hostName = "localhost";
        int portNumber = 9001;
        
        // parse arguments
    	if (args.length == 1){
    		portNumber = Integer.parseInt(args[0]);
            System.out.println("The argument is " + portNumber);
    	}
        
        Buyer b = new Buyer();

        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        	OutputStream outStream =kkSocket.getOutputStream();
        	InputStream inStream = kkSocket.getInputStream();
        	ObjectOutputStream objectOut = new ObjectOutputStream(outStream);
            ObjectInputStream objectIn = new ObjectInputStream(inStream);
        ) {
        	System.out.println("hello");
        	objectOut.flush();
        	BufferedReader sn = new BufferedReader(new InputStreamReader(System.in));
        	
        	buyerNumber = b.acknowledge(in, out);
        	
        	while(true){
        		System.out.println("Hello Buyer "+ buyerNumber +"!\nwhat would you like to do?");
        		System.out.println("[1] Search for an item by attribute.\n[2] Check current bids (on items)");

        		String input = sn.readLine();
        		if(input.equals("1")){
        			itemName = "";
        			attribute = "";
        			minBid = 0;
        			
        			System.out.println("What kind of item are you looking for?");
        			itemName = sn.readLine();
        		
        			System.out.println("Give an attribute of the item you wish to add");
        			attribute = sn.readLine();
        		
        			System.out.println("Give the min bid of the item you wish to add");
        			minBid = Double.parseDouble(sn.readLine());
        		
        			// overloaded constructor
        			Item item = new Item (itemName, attribute, minBid);
        			
        			System.out.println("Created an item");
        			
        			out.println("searchItem");
        			objectOut.writeObject(item);
        			
        			System.out.println("Writing matched item to server");
        			
        			ArrayList<Item> matches;
        			
        			matches = (ArrayList<Item>) objectIn.readObject();
        			
        			for(int i = 0; i < matches.size(); i++){
        				Item temp = matches.get(i);
        				System.out.println("[" + (i+1) + "] UniqueID: " + temp.getUniqueID() + " | Item Name: " + temp.getName() + " | Attributes: " + temp.getAttributes() + " | Current bid: " + temp.getMinBid());
        			}
        			
        			System.out.println("Would you like to bid?");
        			
        			String message = in.readLine();
        			System.out.println(message + "\n");
        		}
        		
        		else if(input.equals("2")){
        			
        			// implement bidding for an item
        			
        		}else if(input.equals("0")) {
        			System.exit(0);
        		} else{
        			System.out.println("not a valid response");
        		}
        	}
        	
/*            
            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }*/
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}