import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileClient_TCP {
	static String hostName = "127.0.0.1";
	static int portNumber = 1024;

	public static void main(String[] args) {
		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.println("Enter File name to upload to the Server: ");
			String fileName = stdIn.readLine();
			out.println("SEND-FILE " + fileName);
			try {
				for (String line : Files.readAllLines(Paths.get(fileName))) {
					out.println(line);
					System.out.println("Line Sent: " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				e.printStackTrace(out);
			} finally {
				out.println("<EOF>");
			}
			System.out.println("exited loop");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
		}
	}
}