import java.io.*;
import java.net.*;


public class Seller implements Client {
	    private static String itemName;
    	private static String attribute;
    	private static double minBid;
    	
    public int acknowledge(BufferedReader in, PrintWriter out){
    	int sellerNumber = -1;
    	
    	try{
    		String line = in.readLine();
    		out.println("seller");
    		sellerNumber = Integer.parseInt(in.readLine());
    	} 
    	catch(Exception e){
    		System.out.println("ERROR: " + e.getMessage());
    	}
    	
		return sellerNumber;
    }
	
    public static void main(String[] args) throws IOException {
    	int sellerNumber;
        String hostName = "localhost";
        int portNumber = 9001;
        
        // parse arguments
    	if (args.length == 1){
    		portNumber = Integer.parseInt(args[0]);
            System.out.println("The argument is " + portNumber);
    	}
        
        Seller s = new Seller();

        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        	OutputStream outStream =kkSocket.getOutputStream();
        	ObjectOutputStream objectOut = new ObjectOutputStream(outStream);
        ) {

        	BufferedReader sn = new BufferedReader(new InputStreamReader(System.in));
        	
        	sellerNumber = s.acknowledge(in, out);
    		//out.println("seller");
    		//sellerNumber = Integer.parseInt(in.readLine());
        	while(true){

        		System.out.println("Hello Seller "+ sellerNumber +"!\nwhat would you like to do?");
        		System.out.println("[1] Put an Item on the market.\n[2] Check Items you have on the market");

        		String input = sn.readLine();
        		if(input.equals("1")){    
        	/*		out.println("marketItem");
        			in.readLine();
        			out.println(Integer.toString(sellerNumber));
        			in.readLine();*/
        			itemName = "";
        			attribute = "";
        			minBid = 0;
        			
        			System.out.println("What is the name of the item you wish to add?");
        			itemName = sn.readLine();
        		
        			System.out.println("Give an attribute of the item you wish to add");
        			attribute = sn.readLine();
        		
        			System.out.println("Give the min bid of the item you wish to add");
        			minBid = Double.parseDouble(sn.readLine());
        		
        			Item item = new Item (sellerNumber, itemName, attribute, minBid);
        			
        			out.println("marketItem");
        			objectOut.writeObject(item);
        			String message = in.readLine();
        			System.out.println(message + "\n");
        		}
        		
        		else if(input.equals("2")){
        			out.println("sellItems");
        			out.println(Integer.toString(sellerNumber));
        			String outputLine = in.readLine();
        			while(!outputLine.equals("") ){
        				System.out.println(outputLine);
        				System.out.println("\n");
        				if (outputLine.equals("You have no Items on the Market\n")){
        					break;
        				}
        				outputLine = in.readLine();
        			}
        			
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