import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class elevatorUDPThread implements Runnable {

	private int portNumber;
	private DatagramSocket socket;
	private InetAddress schedulerAddress;

	public elevatorUDPThread(int portNumber) {
		this.portNumber = portNumber;
		try {
			socket = new DatagramSocket(portNumber);
			schedulerAddress = InetAddress.getLocalHost();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
				socket.receive(recievedPacket);
				String message[] = new String(recievedPacket.getData()).trim().split(":");

				if(!message[0].equals("test")) {
					//TODO Ask about state diagram change (when in moving, change floor number.)
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void completeMove(int currentFloor) {
		
	}
}
