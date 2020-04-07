import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class represents the thread that is used to communicate between the Scheduler and the floorSubsystemThread and
 * between the Scheduler and the elevatorThread.
 * 
 * @author Christian Fisher
 * @version 1.0 2020-04-06
 */
public class schedulerUDPThread implements Runnable {

	private final int elePortArray[] = { 90, 91, 92, 93 };	//Array containing all elevator's port number
	private DatagramSocket socket;	//Socket to send and receive from
	private final int schedulerPort = 99;	//Port of this thread
	private DatagramSocket recSocket;
	private InetAddress elevatorAddress;	//Elevators IP address
	private InetAddress floorAddress;		//Floors IP Address
	private final int floorPort = 98;		//Floors Port
	private Scheduler scheduler;	//Reference to scheduler
	private final String COMMA = ",";	//Defines a comma for easier construction of messages


	/**
	 * Constructor for the schedulerUDPThread
	 * @param scheduler - the scheduler that will run in the system
	 */
	public schedulerUDPThread(Scheduler scheduler) {
		this.scheduler = scheduler;	//Set the reference to the scheduler
		try {
			elevatorAddress = InetAddress.getLocalHost();
			floorAddress = InetAddress.getLocalHost();
			socket = new DatagramSocket(); // Initialize socket to scheduler's port
			recSocket = new DatagramSocket(schedulerPort);
		} catch (SocketException | UnknownHostException e) {
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
				recSocket.receive(recievedPacket);	//Receive the incoming packet
				String message[] = new String(recievedPacket.getData()).trim().split(",");	//Split the incoming packet's data into readable words
				switch (message[0]) {	//The first index will hold the type of message
				case "moveComplete": {	//If the message was a moveComplete message from an elevator
					System.out.println("\n");
					System.out.println("moveComplete Recieved");
					scheduler.completeRequest(Integer.parseInt(message[1]), Integer.parseInt(message[2]),message[3]);	//Notify the scheduler that the elevator reached it's destination
					break;
				}
				case "newRequest": {		//The message was a new request from the floor subsystem
					System.out.println("newRequest Received");
					RequestData request = new RequestData();	//Creates a new RequestData to store the new request into
					request.setCurrentFloor(Integer.parseInt(message[1]));	//Sets the currentFloor of request to the floor stored in the message
					request.setMove(Direction.valueOf(message[2]));	//Sets the direction to the direction specified in the message
					request.setRequestFloor(Integer.parseInt(message[3]));	//The requested floor is then added to the request
					request.setErrorMessage(message[4]);
					scheduler.placeRequest(request);	//The request is sent to the scheduler
					break;
				}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Sends A UDP message to a specified elevator, commanding it to move a specified floor
	 * @param elevatorID	ID of the elevator to move
	 * @param moveToFloor	Floor to move the elevator to
	 */
	public void moveElevator(int elevatorID, int moveToFloor, String errorMessage) {
		byte[] dataToSend = new String("move" + COMMA + moveToFloor + COMMA + "error" + COMMA + errorMessage).getBytes();	//Creates a message of the format "move,floorNumber"
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
				elePortArray[elevatorID]);		//Creates the packet to send to the elevator
		try {
			socket.send(elevatorMovePacket);	//Sends the command
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**Sends completed Requests to the floor subsystem
	 * 
	 * @param data The completed request to send to the floor subsystem
	 */
	public void toFloor(RequestData data) {
		byte[] dataToSend = new String("completeRequest" +COMMA+data.getCurrentFloor() + COMMA + data.getRequestedFloor()).getBytes();	//Create the byte array of format "completeRequest,currentFloor,destFloor"
		DatagramPacket toFloorPacket = new DatagramPacket(dataToSend, dataToSend.length, floorAddress, floorPort);	//Creates the packet
		try {
			socket.send(toFloorPacket);	//Sends the packet
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Polls all the elevators for their current floor and the direction if they're moving
	 * @return	Array of RequetData that contains the direction and currentFloor of all elevators
	 */
	public synchronized RequestData[] pollElevators() {
		RequestData[] elevatorInfo = new RequestData[elePortArray.length];	//Creates an array of RequestData with the length = numOfElevators
		try {
			byte[] dataToSend = new String("poll").getBytes(); //Creates the poll message
			
			for (int elevatorID = 0; elevatorID < elePortArray.length; elevatorID++) {	//For each elevator
				DatagramPacket elevatorPollPacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
						elePortArray[elevatorID]);		//Create a new packet to send to the elevator
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Packet to receive into
				socket.send(elevatorPollPacket);	//Sends the poll command
				recSocket.receive(recievedPacket);		//receives the response
				String elevatorInfoString[] = new String(recievedPacket.getData()).trim().split(",");	//Creates array containing the individual data elements of the response
				if (elevatorInfoString.length == 3) {	//If there are 2 parameters in the response
					elevatorInfo[elevatorID] = new RequestData(0, Integer.parseInt(elevatorInfoString[1]), Direction.valueOf(elevatorInfoString[0]), -1, elevatorInfoString[2]); //temporary fix
					elevatorInfo[elevatorID].setElevatorID(elevatorID);
				} else {
					throw new IOException("response is not the correct length (2)");	//Received the wrong packet
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return elevatorInfo;
	}

}
