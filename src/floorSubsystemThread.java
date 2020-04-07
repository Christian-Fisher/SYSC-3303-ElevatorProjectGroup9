import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * This class represents the thread that is used to communicate between the FloorSubsystem and the scheduler thread.
 * 
 * @author Christian Fisher
 * @version 1.0 2020-04-06
 */
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
	 * Constructor for floorSubsystemThread.
	 * 
	 * @param floor Reference to floor
	 */
	public floorSubsystemThread(FloorSubsystem floor) {
		this.floor = floor;
		try {
			socket = new DatagramSocket();
			recSocket = new DatagramSocket(floorPort);
			schedulerAddress = InetAddress.getLocalHost();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method will receive DatagramPackets once a request is completed.
	 */
	public void run() {
		while(true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//create packet to receive into
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
			socket.send(requestPacket);	//Sends packet
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
