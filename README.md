# SYSC-3303-ElevatorProjectGroup9

## Set Up
1. Add all the necessary files within the project in a default package.
2. Run project from Scheduler's main
3. To run tests, run `alltests.java` as a JUnit Test 

## Iteration 1 Roles
Dhyan: created Scheduler class and resolve bugs
Christian: created FloorSubsystem class and reading input file
Nicholas: created a custom data structure `RequestData` to store the necessary information of input
Sonia: created Elevator subsystem and created a movement impersonation 
Karan: created UML diagrams and test cases

### Files:
RequestData - a data structure for storing and accessing time, current floor, destination floor and movement of a request
Elevator - consumer class for modelling the elevator subsystem and processing requests given by scheduler
Scheduler - main communication channel processing requests made by Floor subsystem and assigning the request to Elevator 
FloorSubsystem - reading input from file and created requests to be sent to Scheduler, reconfirms the completion of requests
AllTests - contains various test cases for unit testing 
Direction - class containing the enum for a movement of an elevator 

