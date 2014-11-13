import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PizzaClient {
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private String username;
	private String usertext;
	private Scanner input;

	public static void main(String[] args) {
		new PizzaClient();
	}

	public PizzaClient() {
		try {
			clientSocket = new Socket("152.78.71.9", 4178);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			input = new Scanner(System.in);
			System.out.print("Please enter your username:\n> ");
			username = input.nextLine();
			System.out.println("Hello " + username + "! Type 'Quit' at any time to exit.");
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						usertext = input.nextLine();
						if (usertext.toLowerCase().equals("quit")) {
							input.close();
							System.exit(0);
						}
						out.println("[ " + username + " ]: " + usertext);
					}
				}
			});
			thread.start();
			String inputLine;
			while (true) {
				while (!((inputLine = in.readLine()) == (null))) {
					if (inputLine.startsWith("[ " + username + " ]: ")) {
						continue;
					}
					System.out.println(inputLine);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("WOW SHIT GON DOWN");
		}

	}
}
