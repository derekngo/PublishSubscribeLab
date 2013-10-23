import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public interface Client {
	public int acknowledge(BufferedReader in, PrintWriter out) throws IOException;
	
	// the client's run method must call acknowledge() first to tell the server who they are
}
