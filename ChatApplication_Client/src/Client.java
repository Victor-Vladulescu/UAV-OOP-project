import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private InputHandler inHandler;
	
	public Client(String address, int port, BufferedReader keyIn) {
		
		try {
			clientSocket = new Socket(address, port);
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			inHandler = new InputHandler(this, keyIn);
			Thread t = new Thread(inHandler);
			t.start();
			
			String serverMessage;
			while ((serverMessage = in.readLine()) != null) {
				
				// server shutdown, close client
				if (serverMessage.equals("/shutdown")) {
					System.out.println("Server shut down, closing client...");
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
			
			inHandler.stopReading();
			
			if (in != null) {
				in.close();
			}
			
			if (out != null) {
				out.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));
		
		// ask for server address and port
		String address;
		int port;
		
		try {
			System.out.println("Address for server:");
			address = keyIn.readLine();
			
			System.out.println("Port number:");
			port = Integer.parseInt(keyIn.readLine());
		}
		catch (Exception e) {
			System.out.println("Invalid input.");
			System.out.print(e.getMessage());
			return;
		}
		
		new Client(address, port, keyIn);
		
		try {
			keyIn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
