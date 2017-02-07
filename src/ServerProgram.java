/**
 * Created by Zortrox on 9/13/2016.
 */

public class ServerProgram {

	//created to read in IP data and create a new jar file just for the server
	public static void main(String[] args) {
		if (args.length > 0) {
			int locPos = args.length > 1 ? 1 : 0;
			String loc = args[locPos];
			int port = Integer.parseInt(loc.substring(loc.indexOf(':') + 1));
			String IP = loc.substring(0, loc.indexOf(':'));

			System.out.println("Starting Server in TCP\n");

			NetServer server = new NetServer(port, IP);
			server.start();
		}
	}
}
