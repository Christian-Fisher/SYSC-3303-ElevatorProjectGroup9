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
	private Scheduler scheduler;

	public schedulerUDPThread(Scheduler scheduler) {
		this.scheduler = scheduler;
		try {
			elevatorAddress = InetAddress.getLocalHost(); // TODO Change to other comp's IP
			socket = new DatagramSocket(schedulerPort); // Initialize socket to scheduler's port
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
				String message[] = new String(recievedPacket.getData()).trim().split(":");
				int elevatorID = elevatorIDFromPort(recievedPacket.getPort());
				switch (message[0]) {
				case "moveComplete": {
					// scheduler.completeRequest(elevatorIDFromPort(elevatorID),
					// Integer.parseInt(message[1]));
				}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private int elevatorIDFromPort(int port) {
		for (int i = 0; i < elePortArray.length; i++) {
			if (elePortArray[i] == port) {
				return i;
			}
		}
		return -1;
	}

	public void moveElevator(int elevatorID, int moveToFloor) {
		byte[] dataToSend = new String("" + moveToFloor).getBytes();
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
				elePortArray[elevatorID]);
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

	public void toFloor(RequestData data, int elevatorID) {
		byte[] dataToSend = new String("" + data.getTime() + "," + data.getCurrentFloor() + ","
				+ data.getDirection().toString() + "," + data.getRequestedFloor()).getBytes();
		DatagramPacket elevatorMovePacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress,
				elePortArray[elevatorID]);
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
			DatagramPacket elevatorPollPacket = new DatagramPacket(dataToSend, dataToSend.length, elevatorAddress, elePortArray[elevatorID]);
			DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);
			elevatorInfo[elevatorID] = new RequestData();
			try {
				socket.send(elevatorPollPacket);
				socket.receive(recievedPacket);
				String elevatorInfoString[] = new String(recievedPacket.getData()).trim().split(",");
				if (elevatorInfoString.length == 2) {
					if (elevatorInfoString[0].equals("Up")) {
						
					}
				} else {
					throw new IOException("response is not length 2");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
