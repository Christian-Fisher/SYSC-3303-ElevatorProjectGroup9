import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class elevatorUDPThread implements Runnable {
	private final int elePortArray[] = { 90, 91, 92, 93 };
	private final int schedulerPort = 99;	//Scheduler's port
	private DatagramSocket socket;	//Socket to send and recieve
	private InetAddress schedulerAddress;	//IP address of scheduler
	private Elevator elevator;		//Reference to elevator
	private final String COMMA = ",";	//byte array containing "ack" to be used when acknowledging messages
	private final byte[] ackData = "ack".getBytes();	//byte array containing "ack" to be used when acknowledging messages

	/**Constructs the elevatorUDPThread
	 * 
	 * @param portNumber initalizes the socket on the given port
	 * @param elevator	Reference to elevator
	 */
	public elevatorUDPThread(int portNumber, Elevator elevator) {
		this.elevator = elevator;
		try {
			socket = new DatagramSocket(elePortArray[portNumber]);		//Initializes the socket
			schedulerAddress = InetAddress.getLocalHost();	//TODO LOCALHOST
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Create the packet to receive into
				socket.receive(recievedPacket); //Receive the incoming packet
				String message[] = new String(recievedPacket.getData()).trim().split(",");	//Split the incoming packet's data into readable words

				if(message[0].equals("poll")) {	//If the command is a poll command
					String pollResponse = "" + elevator.getDirection() +","+ elevator.getCurrentFloor();	//Create the response
					System.out.println("Poll: Sending: " +pollResponse);
					socket.send(new DatagramPacket(pollResponse.getBytes(), pollResponse.getBytes().length, schedulerAddress, schedulerPort));	//send response
				}else {
					throw new IOException("Unknown command: " + message[0]);	//If the command was not recognized, throw exception
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	/**When the elevator reaches the requested floor, send to scheduler.
	 * 
	 * @param currentFloor	floor the elevator is currently at
	 */
	public void completeMove(int currentFloor) {
		byte[] completedMoveData = ("moveComplete:" + currentFloor).getBytes();	//Creates moveCompleteMessage
		DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
		try {
			socket.send(new DatagramPacket(completedMoveData, completedMoveData.length, schedulerAddress, schedulerPort));	//Sends message
			socket.receive(recievedPacket);	//recieves response
			if(!(new String(recievedPacket.getData()).trim().equals("ack"))) {	//If the response is not an ack
				throw new IOException("No Ack recieved on completeMove "  + currentFloor);	//Throw IOException
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
