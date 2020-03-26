import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileServer_TCP {
	private final static int PORT = 1024;
	//REQUIERED TO WORK- folder: C:\Files\ADMIN 
	private static final String ROOT_DIR = "C:\\Files\\";
	private static String activeUser = "ADMIN";

	public static void main(String[] args) {
		boolean flag = true;
		while (flag) {
			try (ServerSocket serverSocket = new ServerSocket(PORT);
					Socket clientSocket = serverSocket.accept();
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
				String inputLine;
				String[] bl;
				while ((inputLine = in.readLine()) != null) {
					bl = inputLine.split(" ");
					switch (bl[0]) {
					case "SEND-FILE":
						getFile(bl[1], in);
						break;
					default:
						clientSocket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.out.println(
						"Exception caught when trying to listen on port " + PORT + " or listening for a connection");
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
	}

	private static void getFile(String fileName, BufferedReader in) {
		String output, toWrite = "";
		try {
			while (!(output = in.readLine()).equals("<EOF>")) {
				if (output == null)
					break;// eclipse says it's dead code, i don't understand why.
				toWrite += output + "\n";
				System.out.println("Line Sent: " + output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		toWrite = toWrite.substring(0, toWrite.length() - 1).trim();
		// Convert the string to a ByteBuffer.
		ByteBuffer bb = ByteBuffer.wrap(toWrite.getBytes());
		// writing the encrypted data
		try (SeekableByteChannel sbc = Files.newByteChannel(Paths.get(ROOT_DIR + activeUser + "\\" + fileName), CREATE,
				WRITE)) {
			sbc.write(bb);
			System.out.println("File Written.");
		} catch (IOException x) {
			System.out.println("Exception thrown: " + x);
		}
	}
}