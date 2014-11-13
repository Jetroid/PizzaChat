import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class PizzaServer {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private static PizzaServer myPizzaServer;
	private ArrayList<Socket> clientSocketList;

	public static void main(String[] args) {

		myPizzaServer = new PizzaServer();
	}

	public PizzaServer() {
		clientSocketList = new ArrayList<Socket>();
		System.out.println("At any time, type 'Quit' to exit.");
		setup();
		listen();
	}

	private void setup() {
		startExitThread();
		try {
			getIPAddress();
			serverSocket = new ServerSocket(4178);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Such ugly. Simply makes it so the user can exit whenever they want.
	private void startExitThread() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner input = new Scanner(System.in);
				while (true) {
					if (input.next().toLowerCase().contains("quit")) {
						System.out.println("Exiting.");
						input.close();
						try {
							serverSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					}

				}
			}
		});
		thread.start();
	}

	// Code from user sasidhar at Stack Exchange. With help from Andrew Barrett-Sprot for RegEx.
	private void getIPAddress() throws SocketException {
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				String address = i.getHostAddress();
				if (address.matches("(?!^127)^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
					System.out.println("Your IP address is " + address);
				}
			}
		}
	}

	private void listen() {
		while (true) {
			try {
				System.out.println("Waiting for new Client to connect to port.");
				clientSocket = serverSocket.accept();
				PizzaServerThread newThread = new PizzaServerThread(clientSocket, this);
				clientSocketList.add(clientSocket);
				newThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Socket> getClients() {
		return (ArrayList<Socket>) clientSocketList.clone();
	}

	public class PizzaServerThread extends Thread {
		private PrintWriter out;
		private BufferedReader in;
		private PizzaServer myPizzaServer;

		public PizzaServerThread(Socket socket, PizzaServer myPizzaServer) {
			this.myPizzaServer = myPizzaServer;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("A new client successfully connected!");
			out.println("Hello Client, welcome to jet.net!");
		}

		public void run() {
			try {
				String lineIn;
				while (!((lineIn = in.readLine()).equals(null))) {
					for (Socket clients : myPizzaServer.getClients()) {
						if (clients == null) {
							myPizzaServer.getClients().remove(clients);
							continue;
						}
						out = new PrintWriter(clients.getOutputStream(), true);
						out.println(lineIn);
					}
				}
			} catch (IOException e) {
			}
		}
	}
}
