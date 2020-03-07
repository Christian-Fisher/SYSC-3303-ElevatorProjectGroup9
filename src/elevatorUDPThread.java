import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class elevatorUDPThread implements Runnable {

	private final int schedulerPort = 99;
	private DatagramSocket socket;
	private InetAddress schedulerAddress;
	private Elevator elevator;
	private final String COMMA = ",";
	private final byte[] ackData = "ack".getBytes();	//byte array containing "ack" to be used when acknowledging messages

	public elevatorUDPThread(int portNumber, Elevator elevator) {
		this.elevator = elevator;
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
				String message[] = new String(recievedPacket.getData()).trim().split(",");

				if(!message[0].equals("poll")) {
					String pollResponse = "" + elevator.getDirection() +","+ elevator.getCurrentFloor();
					socket.send(new DatagramPacket(pollResponse.getBytes(), pollResponse.getBytes().length, schedulerAddress, schedulerPort));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void completeMove(int currentFloor) {
		byte[] completedMoveData = ("moveComplete:" + currentFloor).getBytes();
		DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
		try {
			socket.send(new DatagramPacket(completedMoveData, completedMoveData.length, schedulerAddress, schedulerPort));
			socket.receive(recievedPacket);
			if(!(new String(recievedPacket.getData()).trim().equals("ack"))) {
				throw new IOException("No Ack recieved on completeMove "  + currentFloor);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
