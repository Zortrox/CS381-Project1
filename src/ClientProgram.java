/**
 * Created by Zortrox on 9/12/2016.
 */

public class ClientProgram {

	//created to read in IP data and create a new jar file just for the client
	public static void main(String[] args) {
		if (args.length > 0) {

			int locPos = args.length > 1 ? 1 : 0;
			String loc = args[locPos];
			int port = Integer.parseInt(loc.substring(loc.indexOf(':') + 1));
			String IP = loc.substring(0, loc.indexOf(':'));

			System.out.println("Starting Client in TCP\n");

			NetClient client = new NetClient(port, IP);
			client.start();
		}
	}
}
