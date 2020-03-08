# SYSC-3303-ElevatorProjectGroup9

## Set Up
1. Add all the necessary files within the project in a default package.
2. Run project from Scheduler's main
3. To run tests, run `alltests.java` as a JUnit Test 

## Iteration 3 Roles
Dhyan: created Scheduler class and resolve bugs

Christian: created the UDP threads for the elevator, floorSubsytem and the scheduler classes. 

Nicholas: Modified the states for the elevator's state machine and updated the FloorSubsystem to read the new input file.

Sonia: created Elevator subsystem and created a movement impersonation 

Karan: Updated the input file for the test cases for iteration #3, wrote the reflection and updated the diagrams. 

### Files:
RequestData - a data structure for storing and accessing time, current floor, destination floor and movement of a request

Elevator - consumer class for modelling the elevator subsystem and processing requests given by scheduler

Scheduler - main communication channel processing requests made by Floor subsystem and assigning the request to Elevator 

FloorSubsystem - reading input from file and created requests to be sent to Scheduler, reconfirms the completion of requests

AllTests - contains various test cases for unit testing 

Direction - class containing the enum for a movement of an elevator 

elevatorUDPThread - class used to send and receive UDP for the elevator. Used for communication amongst the elevator, scheduler and the floor subsystem. 

schedulerUDPThread - class used to send and receive UDP for the scheduler. Used for communication amongst the elevator, scheduler and the floor subsystem. 

floorSubsystemThread - class used to send and receive UDP for the floor.  Used for communication amongst the elevator, scheduler and the floor subsystem. 
