import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class floorSubsystemThread implements Runnable {
	private FloorSubsystem floor;	//Reference to Floor
	private final int floorPort = 98;	//FloorUDP port
	private final int schedulerPort = 99;	//Scheduler's port
	private DatagramSocket socket;	//Socket to send and recieve
	private DatagramSocket recSocket;
	private InetAddress schedulerAddress;	//Address of scheduler
	private final String COMMA = ",";	//byte array containing "ack" to be used when acknowledging messages
	private final byte[] ackData = "ack".getBytes();	//byte array containing "ack" to be used when acknowledging messages

	/**
	 * 
	 * @param floor Reference to floor
	 */
	public floorSubsystemThread(FloorSubsystem floor) {
		this.floor = floor;
		try {
			socket = new DatagramSocket();
			recSocket = new DatagramSocket(floorPort);
			schedulerAddress = InetAddress.getLocalHost(); // TODO LOCALHOST
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		while(true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//create packet to recieve into
				recSocket.receive(recievedPacket);	//Recieve command
				String message[] = new String(recievedPacket.getData()).trim().split(",");		//Convert to readable format
				if(message[0].equals("completeRequest")) {		//If the command is a completedRequest
					socket.send(new DatagramPacket(ackData, ackData.length, schedulerAddress, schedulerPort));//Acknowledge the scheduler
					System.out.println("Completed Request from floor: "+ message[1] + " to floor: " + message[2]);	//Print the request
				}else {
					System.out.println("Unknown Command " + Arrays.toString(message));	//If the command is unknown throw exception
					throw new IOException("Unknown Command ");	//If the command is unknown throw exception
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

/**Sends a request to the scheduler
 * 
 * @param request The request to send to scheduler
 */
	public void sendRequest(RequestData request) {
		try {
			byte[] dataToSend = ("newRequest" + COMMA + request.getCurrentFloor() + COMMA + request.getDirection().toString() + COMMA
					+ request.getRequestedFloor() + COMMA + request.getErrorMessage()).getBytes();	//Creates the message out of the input RequestData
			DatagramPacket requestPacket = new DatagramPacket(dataToSend, dataToSend.length, schedulerAddress,
					schedulerPort);	//Creates packet to send to scheduler
			DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
			socket.send(requestPacket);	//Sends packet
			/*recSocket.receive(recievedPacket);	//Recieves packet
			if(!(new String(recievedPacket.getData()).trim().equals("ack"))) {	//If recieved packet is not an acknowledgement
				throw new IOException("not ack recieved");	//throw Exception
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
