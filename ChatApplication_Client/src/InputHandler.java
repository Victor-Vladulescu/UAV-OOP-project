import java.io.BufferedReader;
import java.io.InputStreamReader;

class InputHandler implements Runnable {

		private Client client;
		private BufferedReader inReader;
		
		public InputHandler(Client client) {
			this.client = client;
		}
	
		@Override
		public void run() {
			try {
				inReader = new BufferedReader(new InputStreamReader(System.in));
				
				String message;
				while (true) {
					
					try {
						message = inReader.readLine();
					}
					catch (Exception e) {
						break;
					}
					
					// tell server you quit and close client
					if (message.equals("/quit")) {
						client.sendMessage("/quit");
						break;
					}
					else {
						client.sendMessage(message);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void closeConnection() {
			try {
				
				if (inReader != null) {
					inReader.close();
				}
				
				
			}
			catch (Exception e) {
				// cannot handle, ignore
			}
		}
	}