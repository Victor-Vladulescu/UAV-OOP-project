import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


// reads keyboard input from the user after a connection to the server is established
class InputHandler implements Runnable {

		private Client client;
		private BufferedReader keyIn;
		
		public InputHandler(Client client, BufferedReader keyIn) {
			this.client = client;
			this.keyIn = keyIn;
		}
	
		@Override
		public void run() {
			try {
				String message;
				
				while ((message = keyIn.readLine()) != null) {
					
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
		
		public void stopReading() {
			try {
				// TODO
				// tell the stupid BufferedReader that it's supposed to STOP reading the line
				// keyIn.close();
				
				// just kill the entire program then
				System.exit(0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}