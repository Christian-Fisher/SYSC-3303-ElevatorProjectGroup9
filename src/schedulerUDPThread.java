import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class schedulerUDPThread implements Runnable {

	private final int elePortArray[] = { 90, 91, 92, 93 };	//Array containing all elevator's port number
	private DatagramSocket socket;	//Socket to send and receive from
	private final int schedulerPort = 99;	//Port of this thread
	private InetAddress elevatorAddress;	//Elevators IP address
	private InetAddress floorAddress;		//Floors IP Address
	private final int floorPort = 98;		//Floors Port
	private Scheduler scheduler;	//Reference to scheduler
	private final byte[] ackData = "ack".getBytes();	//byte array containing "ack" to be used when acknowledging messages
	private final String COMMA = ",";	//Defines a comma for easier construction of messages


	public schedulerUDPThread(Scheduler scheduler) {
		this.scheduler = scheduler;	//Set the reference to the scheduler
		try {
			elevatorAddress = InetAddress.getLocalHost(); // TODO LOCALHOST
			floorAddress = InetAddress.getLocalHost();
			socket = new DatagramSocket(schedulerPort); // Initialize socket to scheduler's port
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {

		while (true) {
			try {
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Create the packet to receive into
				socket.receive(recievedPacket);	//Recieve the incoming packet
				String message[] = new String(recievedPacket.getData()).trim().split(",");	//Split the incoming packet's data into readable words
				int elevatorID = elevatorIDFromPort(recievedPacket.getPort());	//Saves the ID of the elevator that sent the request (-1 if an elevator did not send the request)
				switch (message[0]) {	//The first index will hold the type of message
				case "moveComplete": {	//If the message was a moveComplete message from an elevator
					socket.send(new DatagramPacket(ackData, ackData.length, recievedPacket.getAddress(), recievedPacket.getPort()));	//Send an ack message back
					scheduler.completeRequest(elevatorIDFromPort(elevatorID), Integer.parseInt(message[1]));	//Notify the scheduler that the elevator reached it's destination
				}
				case "newRequest": {		//The message was a new request from the floor subsystem
					socket.send(new DatagramPacket(ackData, ackData.length, recievedPacket.getAddress(), recievedPacket.getPort()));	//Sends an ack message
					RequestData request = new RequestData();	//Creates a new RequestData to store the new request into
					request.setCurrentFloor(Integer.parseInt(message[1]));	//Sets the currentFloor of request to the floor stored in the message
					request.setDirection(Direction.valueOf(message[2]));	//Sets the direction to the direction specified in the message
					request.setRequestedFloor(Integer.parseInt(message[3]));	//The requested floor is then added to the request
					scheduler.placeRequest(request);	//The request is sent to the scheduler
				}
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 *  Private method which will be used to determine the elevator ID based on the port number of the message receievd
	 * @param port contains the port number
	 * @return integer containing the ID of the elevator (OR -1 to indicate it was not an elevator port)
	 */
	private int elevatorIDFromPort(int port) {
		for (int i = 0; i < elePortArray.length; i++) {	//Loops through the elevatorPort array
			if (elePortArray[i] == port) {	//If the port is in the elevator array
				return i;		//return it's index
			}
		}
		return -1;		//Else, it was not an elevator that sent the message
	}
/**
 * Sends A UDP message to a specified elevator, commanding it to move a specified floor
 * @param elevatorID	ID of the elevator to move
 * @param moveToFloor	Floor to move the elevator to
 */
	public void moveElevator(int elevatorID, int moveToFloor) {
		byte[] dataToSend = new String("move" + COMMA + moveToFloor).getBytes();	//Creates a message of the format "move,floorNumber"
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
				elePortArray[elevatorID]);		//Creates the packet to send to the elevator
		DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Creates a packet to recieve the response
		try {
			socket.send(elevatorMovePacket);	//Sends the command
			socket.receive(recievedPacket);		//Recieves the response of the elevator
			if (new String(recievedPacket.getData()).trim().equals("ack")) {	//If the response was not an acknowledgement, throw exception
				return;
			} else {
				throw new IOException("not ack recieved");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/**
 * 
 * @param data	RequestData to send to floorSubsystem
 * @param elevatorID	Elevator ID 
 */
	public void toFloor(RequestData data) {
		byte[] dataToSend = new String("" + data.getTime() + "," + data.getCurrentFloor() + ","
				+ data.getDirection().toString() + "," + data.getRequestedFloor()).getBytes();
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, floorAddress, floorPort);
		DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
		try {
			socket.send(elevatorMovePacket);
			socket.receive(recievedPacket);
			if (new String(recievedPacket.getData()).trim().equals("ack")) {
				return;
			} else {
				// TODO Throw exception or return a bad value?
				return;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public RequestData[] pollElevators() {
		byte[] dataToSend = new String("poll").getBytes();
		RequestData[] elevatorInfo = new RequestData[elePortArray.length];
		for (int elevatorID = 0; elevatorID < elePortArray.length; elevatorID++) {
			DatagramPacket elevatorPollPacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
					elePortArray[elevatorID]);
			DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
			elevatorInfo[elevatorID] = new RequestData();
			try {
				socket.send(elevatorPollPacket);
				socket.receive(recievedPacket);
				String elevatorInfoString[] = new String(recievedPacket.getData()).trim().split(",");
				if (elevatorInfoString.length == 2) {
					elevatorInfo[elevatorID].setDirection(Direction.valueOf(elevatorInfoString[0]));
					elevatorInfo[elevatorID].setCurrentFloor(Integer.parseInt(elevatorInfoString[1]));
				} else {
					throw new IOException("response is not the correct length (2)");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return elevatorInfo;
	}

}
