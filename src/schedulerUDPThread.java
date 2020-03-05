import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class schedulerUDPThread implements Runnable {

	private final int elePortArray[] = { 90, 91, 92, 93 };
	private DatagramSocket socket;
	private final int schedulerPort = 99;
	private InetAddress elevatorAddress;

	public schedulerUDPThread() {
		try {
			elevatorAddress = InetAddress.getLocalHost(); // TODO Change to other comp's IP
			socket = new DatagramSocket(schedulerPort);	//Initialize socket to scheduler's port
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {

		while (true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
				socket.receive(recievedPacket);
				String message = new String(recievedPacket.getData()).trim();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public void moveElevator(int elevatorID, int moveToFloor) {
		byte[] dataToSend = new String(""+moveToFloor).getBytes();
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress, elePortArray[elevatorID]);
		DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
		try {
			socket.send(elevatorMovePacket);
			socket.receive(recievedPacket);
			if(new String(recievedPacket.getData()).trim().equals("ack")) {
				return;
			}else {
				//TODO Throw exception or return a bad value?
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
