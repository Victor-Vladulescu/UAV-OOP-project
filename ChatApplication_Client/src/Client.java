import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private Scanner keyIn;
	
	public Client(String address, int port, Scanner keyIn) {
		
		try {
			clientSocket = new Socket(address, port);
			
			this.keyIn = keyIn;
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			InputHandler inHandler = new InputHandler(this);
			Thread t = new Thread(inHandler);
			t.start();
			
			String serverMessage;
			while ((serverMessage = in.readLine()) != null) {
				
				// server shutdown, close client
				if (serverMessage.equals("/shutdown")) {
					System.out.println("Server shut down, closing client...");
					inHandler.closeConnection();
					shutdown();
					break;
				}
				// regular message
				else {
					System.out.println(serverMessage);	
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message) {
		out.println(message);
	}
	
	public void shutdown() {
		
		try {
			if (!clientSocket.isClosed()) {
				clientSocket.close();
			}
			
			if (in != null) {
				in.close();
			}
			
			if (out != null) {
				out.close();
			}
			
			keyIn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Scanner keyIn = new Scanner(System.in);
		
		// ask for server address and port
		String address;
		int port;
		
		try {
			System.out.println("Address for server:");
			address = keyIn.nextLine();
			
			System.out.println("Port number:");
			port = keyIn.nextInt();
		}
		catch (Exception e) {
			System.out.println("Invalid input.");
			System.out.print(e.getMessage());
			keyIn.close();
			return;
		}
		
		// why does Scanner.close() shut off the System.in stream as well?
		//keyIn.close();
		new Client(address, port, keyIn);
	}
}
