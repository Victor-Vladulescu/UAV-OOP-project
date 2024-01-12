import java.io.BufferedReader;
import java.io.InputStreamReader;

class InputHandler implements Runnable {

		private Client client;
		
		public InputHandler(Client client) {
			this.client = client;
		}
	
		@Override
		public void run() {
			try {
				BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
				
				String message;
				while (true) {
					message = inReader.readLine();
				
					// tell server you quit and close client
					if (message.equals("/quit")) {
						client.sendMessage("/quit");
						client.shutdown();
						break;
					}
					else {
						client.sendMessage(message);
					}
				}
			}
			catch (Exception e) {
				client.shutdown();
			}
		}
	}