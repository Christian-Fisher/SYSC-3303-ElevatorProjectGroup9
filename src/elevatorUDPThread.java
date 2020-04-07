import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class represents the thread that is used to communicate between the elevator and the scheduler thread.
 * 
 * @author Christian Fisher
 * @version 1.0 2020-04-06
 */
public class elevatorUDPThread implements Runnable {
	private final int elePortArray[] = { 90, 91, 92, 93 };
	private final int schedulerPort = 99;	//Scheduler's port
	private DatagramSocket socket;	//Socket to send and receive
	private DatagramSocket recSocket;
	private InetAddress schedulerAddress;	//IP address of scheduler
	private Elevator elevator;		//Reference to elevator
	private final String COMMA = ",";	//byte array containing "ack" to be used when acknowledging messages

	/**
	 * Constructs the elevatorUDPThread
	 * 
	 * @param portNumber initializes the socket on the given port
	 * @param elevator	Reference to elevator
	 */
	public elevatorUDPThread(int portNumber, Elevator elevator) {
		this.elevator = elevator;
		try {
			recSocket = new DatagramSocket(elePortArray[portNumber]);
			socket = new DatagramSocket();		//Initializes the socket
			schedulerAddress = InetAddress.getLocalHost();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will receive DatagramPackets and choose the appropriate action depending on what the message says.
	 */
	public void run() {
		while (true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Create the packet to receive into
				recSocket.receive(recievedPacket); //Receive the incoming packet
				String message[] = new String(recievedPacket.getData()).trim().split(",");	//Split the incoming packet's data into readable words
				if(message[0].equals("poll")) {	//If the command is a poll command
					String pollResponse = "" + elevator.getDirection() +COMMA+ elevator.getCurrentFloor()+COMMA+elevator.getError();	//Create the response
					socket.send(new DatagramPacket(pollResponse.getBytes(), pollResponse.getBytes().length, schedulerAddress, schedulerPort));	//send response
				}else if(message[0].equals("move")) {
					elevator.setRequestedFloor(Integer.parseInt(message[1]));
					elevator.setError(message[3]);
					System.out.println("The chosen elevator will now move to floor # "+ Integer.parseInt(message[1]));
					System.out.println("\n");
				}
				else {
					throw new IOException("Elevator: Unknown command: " + message[0]);	//If the command was not recognized, throw exception
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * When the elevator reaches the requested floor, send message to scheduler.
	 * 
	 * @param currentFloor	floor the elevator is currently at
	 */
	public void completeMove(int elID, int currentFloor, String errorMessage) {
		byte[] completedMoveData = ("moveComplete" +COMMA+elID+COMMA+ currentFloor+COMMA+errorMessage).getBytes();	//Creates moveCompleteMessage
		//Sends message
		try {
			socket.send(new DatagramPacket(completedMoveData, completedMoveData.length, schedulerAddress, schedulerPort));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
