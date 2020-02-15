# SYSC-3303-ElevatorProjectGroup9

## Set Up
1. Add all the necessary files within the project in a default package.
2. Run project from Scheduler's main
3. To run tests, run `alltests.java` as a JUnit Test 

## Iteration 2 Roles
Dhyan: created test cases

Christian: created the state machine diagram and sequence diagram. Updated the class diagram

Nicholas: worked on implementing Elevator as state machine.

Sonia: worked on implementing Elevator as state machine. Created the state machine diagram and sequence diagram.

Karan: Implemented the Scheduler as a state machine

### Files:
RequestData - a data structure for storing and accessing time, current floor, destination floor and movement of a request

Elevator - consumer class for modelling the elevator subsystem and processing requests given by scheduler

Scheduler - main communication channel processing requests made by Floor subsystem and assigning the request to Elevator 

FloorSubsystem - reading input from file and created requests to be sent to Scheduler, reconfirms the completion of requests

AllTests - contains various test cases for unit testing 

Direction - class containing the enum for a movement of an elevator 

