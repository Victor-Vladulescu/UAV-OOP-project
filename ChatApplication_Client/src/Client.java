import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

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
			
			String serverInput;
			while ((serverInput = in.readLine()) != null) {
				
				
				String[] message = serverInput.split(Pattern.quote(">8^("), 4);
				
				
				switch (Integer.parseInt(message[0])) {
					
					// broadcast message
					case 0:
						System.out.println(String.format("[%s] (%s) %s", message[1], message[2], message[3]));
						break;
						
					// whisper received
					case 1:
						// TODO implement
						break;
	
					// the administrator sent a message to everyone
					case 2:
						System.out.println(String.format("[%s - Admin] %s", message[1], message[2]));
						break;
					
					
					// user changed their nickname
					case 3:
						System.out.println(String.format("[%s] (%s) renamed themselves to (%s)", message[1], message[2], message[3]));
						break;
					
					// server shut down
					case 4:
						System.out.println(String.format("[%s] Server shut down, closing client...", message[1]));
						shutdown();
						break;
						
					// new user joined
					case 5:
						System.out.println(String.format("[%s] (%s) joined the chat!", message[1], message[2]));
						break;
						
					// user just quit
					case 6:
						System.out.println(String.format("[%s] (%s) just quit.", message[1], message[2]));
						break;
						
					// error
					case 99:
						System.out.println(String.format("ERROR: %s", message[1]));
						break;
						
					// general message from server
					case 100:
						System.out.println(message[1]);
						break;
						
					default:
						throw new Exception("Unknown operation code. [" + message[0] + "]");
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
