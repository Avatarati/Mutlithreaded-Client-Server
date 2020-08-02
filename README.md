# Multi-Threaded Client and Server
Client and server demonstrating concurrency in the transmission and handling of requests.

## Screens
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Demo/Client.PNG)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Demo/Server.PNG)

## Build Strings
* javac Client.java CClient.java ClientIO.java
* javac Server.java ServerUtils.java ServerMessage.java ClientHandler.java

## Runnables
* Runnables are Client.java and Server.java.
* When launching the server, a port must be supplied as an argument.

## Client
The client will ask for an IP address and port when first launched. It will then ask for the command to be processed and the number of clients which to generate. The number of clients will determine how many times the command is sent to the server.

## Available Commands:
* Date    - Get Date
* Uptime  - Get Uptime
* Memory  - Get Memory Usage
* Status  - Get Network Status
* Users   - Get List of Users
* Process - Get Running Processes
* Stop    - Terminate Client & Server

## Server
The server listens for traffic on a supplied port. Upon receive, it will accept the connection and create a new socket. From there, it will extract the Client Number from the first two bytes and the enumerated command from the last remaining bytes of the payload. If valid, it will execute the command in the linux shell and return the result to the client. Each request received will initialize a new ClientHandler object to process the request on a new thread.

## Testing
The server is able to process several concurrent requests at once. Testing on a community university server revealed a considerable breakdown in server response after 25 concurrent connections. The goal of the project was to explore how well the server was able to handle multiple requests given 1, 5, 10, 15, 20, 25, and 100 concurrent connections.
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/DateTime.png)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/Uptime.png)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/Memory.png)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/Users.png)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/Processes.png)
![alt text](https://raw.githubusercontent.com/Avatarati/Mutlithreaded-Client-Server/master/Testing/NetStat.png)
