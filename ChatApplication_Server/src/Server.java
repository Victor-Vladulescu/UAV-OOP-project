import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class Server {
	
	// fields
	public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    public final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	private HashMap<ConnectionHandler, Thread> connections;
	private ServerSocket server;
	private boolean acceptingConnections;
    private static FileWriter logFile;
    private Commander commander;
    private Thread commanderThread;
    
    public ArrayList<String> nicknames;
	
    public Server() {
		
		// make log file
		try {
			String filename = dateTimeFormat.format(new Timestamp(System.currentTimeMillis())) + "_log.txt";
			logFile = new FileWriter(filename);
			log(String.format("[%s] Starting server", timeFormat.format(new Timestamp(System.currentTimeMillis()))));
		}
		catch (Exception e) {
			System.out.println("Cannot create log file.");
			e.printStackTrace();
			return;
		}

		// keep track of all client connections
		connections = new HashMap<>();
		acceptingConnections = true;
		
		// create commander and start in new thread
		commander = new Commander(this);
		commanderThread = new Thread(commander);
		commanderThread.start();
		
		// create list of nicknames
		nicknames = new ArrayList<String>();
		
		// keep accepting connections until commander says otherwise
		try {
			server = new ServerSocket(9999);
			
			while (acceptingConnections) {
				Socket client = server.accept();
				ConnectionHandler handler = new ConnectionHandler(client, this);
				Thread handlerThread = new Thread(handler);
				connections.put(handler, handlerThread);
				handlerThread.start();
			}
			
		} catch (Exception e) {}
	}
	
	public void broadcast(String message) {
		
		for (ConnectionHandler ch : connections.keySet()) {
			ch.sendMessage(message);
		}
	}
	
	public void log(String message) {
		
		// TODO
		// log most commands
		
		System.out.println(message);
		
		try {
			logFile.append(message + "\n");
		}
		catch (Exception e) {
			System.out.println("[WARNING] Cannot write to logfile!");
		}
	}
	
	public void shutdownServer() {
		
		try {
			acceptingConnections = false;
			if (!server.isClosed()) {
				server.close();
			}
			
			// close all connections
			for (ConnectionHandler ch : connections.keySet()) {
				ch.closeConnection();
			}
			
			log(String.format("[%s] Shutting down server", timeFormat.format(new Timestamp(System.currentTimeMillis()))));
			logFile.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new Server();
	}
}
