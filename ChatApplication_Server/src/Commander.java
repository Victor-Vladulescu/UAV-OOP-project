import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;

class Commander implements Runnable {
		
		private BufferedReader in;
		private Server server;
		
		public Commander(Server server) {
			this.server = server;
		}
		
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(System.in));
				
				String command;
				while (true) {
					
					command = in.readLine();
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					
					// close server
					if (command.equals("/shutdown")) {
						server.broadcast(String.format("4>8^(%s", server.timeFormat.format(timestamp)));
						server.shutdownServer();
						break;
					}
					
					// the administrator has something to say
					else if (command.startsWith("/say")) {
						
						String[] message = command.split(" ", 2);
						
						if (message.length == 2) {
							server.broadcast(String.format("2>8^(%s>8^(%s", server.timeFormat.format(timestamp), message[1]));
						}
						else {
							System.out.println("Your message is empty.");
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}