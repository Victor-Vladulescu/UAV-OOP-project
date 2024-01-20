import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.regex.Pattern;

class ConnectionHandler implements Runnable {
		
		private Socket client;
		private Server server;
		private PrintWriter out;
		private BufferedReader in;
		private String nickname;
		
		public ConnectionHandler(Socket client, Server server) {
			this.client = client;
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				String clientInput;
				String[] message;
				
				// user tries to enter nickname, make sure it's not already used
				out.println("100>8^(Hello. Please enter a nickname:");
				clientInput = in.readLine();
				message = clientInput.split(Pattern.quote(">8^("), 2);
				
				while (true) {
					if (server.nicknames.contains(nickname)) {
						out.println("99>8^(This name is already in use, try another:");
						clientInput = in.readLine();
						message = clientInput.split(Pattern.quote(">8^("), 2);
					}
					else {
						nickname = message[1];
						server.nicknames.add(nickname);
						break;
					}
				}
				
				
				// get formatted time stamp
				String time = server.timeFormat.format(new Timestamp(System.currentTimeMillis()));
				
				// log and broadcast
				server.broadcast(String.format("5>8^(%s>8^(%s", time, nickname));
				
				
				
				while (true) {
					
					try {
						clientInput= in.readLine();	// will get an exception after being interrupted
					}
					catch (Exception e) { break; }
					
					
					time = server.timeFormat.format(new Timestamp(System.currentTimeMillis()));
					message = clientInput.split(Pattern.quote(">8^("), 3);
					
					
					switch (Integer.parseInt(message[0])) {
					
						// broadcast message
						case 1:	
							server.broadcast(String.format("0>8^(%s>8^(%s>8^(%s", time, nickname, message[1]));
							break;
						
						// user wants to change their nickname
						case 2:
	
							// was a nickname supplied?
							if (message[1].isBlank()) {
								out.println("100>8^(Desired nickname is invalid");
							}
							
							// nickname already in use?
							if (server.nicknames.contains(message[1])) {
								out.println("100>8^(Nickname already in use, try another");
							}
							
							server.broadcast(String.format("3>8^(%s>8^(%s>8^(%s", time, nickname, message[1]));
							server.nicknames.remove(nickname);
							server.nicknames.add(message[1]);
							nickname = message[1];
							break;
							
						// whisper
						case 3:
							
							// TODO implement
							break;
							
						// client quits
						case 4:
							server.broadcast(String.format("6>8^(%s>8^(%s", time, nickname));
							closeConnection();
							break;
							
						default:
							throw new Exception("Operation code unknown [" + message[0] + "]");
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				closeConnection();
			}
		}
		
		public void sendMessage(String message) {
			out.println(message);
		}
		
		public void closeConnection() {
			try {
				
				// socket needs to be closed first, and then readers and writers
				if (!client.isClosed()) {
					client.close();
				}
				
				if (in != null)
					in.close();
				
				if (out != null)
					out.close();
			}
			catch (Exception e) {
				// cannot handle, ignore
			}
		}
	}