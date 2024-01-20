import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;

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
				out.println("Hello. Please enter a nickname: ");
				nickname = in.readLine();
				
				// get formatted time stamp
				String time = server.timeFormat.format(new Timestamp(System.currentTimeMillis()));
				
				// log and broadcast
				server.broadcast(String.format("[%s] (%s) joined the chat!", time, nickname));
				
				String message;
				while (true) {
					
					try {
						message = in.readLine();	// will get an exception after being interrupted
					}
					catch (Exception e) { break; }
						
					time = server.timeFormat.format(new Timestamp(System.currentTimeMillis()));
					
					// change nickname
					if (message.startsWith("/nick ")) {
						String[] messageSplit = message.split(" ", 2);
						
						// TODO make sure no two users can have the same nickname at the same time!
						
						if (messageSplit.length == 2) {
							server.broadcast(String.format("[%s] (%s) renamed themselves to (%s)", time, nickname, messageSplit[1]));
							nickname = messageSplit[1];
							out.println("Successfully changed nickname to " + nickname);
						}
						else {
							out.println("No nickname provided");
						}
					}
					// user quit
					else if (message.equals("/quit")) {
						server.broadcast(String.format("[%s] (%s) left the chat", time, nickname));
						closeConnection();
					}
					// user sent a message
					else {
						server.broadcast(String.format("[%s] (%s) %s", time, nickname, message));
					}
				}
			} catch (Exception e) {
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