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
						server.shutdownServer();
						break;
					}
					// admin has something to say
					else if (command.startsWith("/say ")) {
						String[] messageSplit = command.split(" ", 2);
						
						if (messageSplit.length == 2) {
							server.log(String.format("[%s - Admin] %s", server.timeFormat.format(timestamp), messageSplit[1]), true);
							server.broadcast(String.format("[%s - Admin] %s", server.timeFormat.format(timestamp), messageSplit[1]));
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