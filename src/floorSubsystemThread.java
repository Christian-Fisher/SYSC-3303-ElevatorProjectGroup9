import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class floorSubsystemThread implements Runnable {
	private FloorSubsystem floor;
	private final int floorPort = 98;
	private final int schedulerPort = 99;
	private DatagramSocket socket;
	private InetAddress schedulerAddress;
	private final String COMMA = ",";
	private final byte[] ackData = "ack".getBytes();	//byte array containing "ack" to be used when acknowledging messages

	public floorSubsystemThread(FloorSubsystem floor) {
		this.floor = floor;
		try {
			socket = new DatagramSocket(floorPort);
			schedulerAddress = InetAddress.getLocalHost(); // TODO LOCALHOST
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		while(true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
				socket.receive(recievedPacket);
				String message[] = new String(recievedPacket.getData()).trim().split(",");
				if(!message[0].equals("completedRequest")) {
					socket.send(new DatagramPacket(ackData, ackData.length, recievedPacket.getAddress(), recievedPacket.getPort()));
					
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void sendRequest(RequestData request) {
		try {
			byte[] dataToSend = ("newRequest" + COMMA + request.getCurrentFloor() + COMMA + request.getDirection().toString() + COMMA
					+ request.getRequestedFloor()).getBytes();
			DatagramPacket requestPacket = new DatagramPacket(dataToSend, dataToSend.length, schedulerAddress,
					schedulerPort);
			DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
			socket.send(requestPacket);
			socket.receive(recievedPacket);
			if(!(new String(recievedPacket.getData()).trim().equals("ack"))) {
				throw new IOException("not ack recieved");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
