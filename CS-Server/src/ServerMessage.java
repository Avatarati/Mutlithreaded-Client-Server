/*********************************************
* Concurrent Socket Server
* 
* ServerMessage.java
* Connection messages and server errors
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.util.concurrent.locks.ReentrantLock;

class Colors
{
	public static final String Red    = "\u001B[31m";
	public static final String Cyan   = "\u001B[36m";
	public static final String Green  = "\u001B[32m";
	public static final String Yellow = "\u001B[33m";
	public static final String Reset  = "\u001B[0m";
}

enum Commands
{
	Time,
	Uptime,
	Memory,
	Netstat,
	Users,
	Process,
	Stop
}

enum Errors
{
	Command,
	Range,
	Port,
	Bind
}

interface Error 
{
	void Show(String S);
}

public class ServerMessage
{
	private static ReentrantLock MessageControl = new ReentrantLock();

	public static void SendError(Errors e)
	{
		Error E = (String S) -> 
			System.out.println(
				"\n" + Colors.Red + e.toString()
				+ " Error" + Colors.Reset 
				+ ": " + S
		);
		switch (e)
		{
			case Command: E.Show("Unable to process command. See log for details.\n"); break;
			case Port:    E.Show("A port must be supplied as an argument.\n"); break;
			case Range:   E.Show("The port supplied is invalid. (Valid range: 1024 - 65535)\n"); break;
			case Bind:    E.Show("Failed to bind socket."); break;
		}
	}
	
	public static void PrintHeader(int Port)
	{
		System.out.print("\033[H\033[2J"); // Clear console window
		System.out.flush();
		System.out.println("-------------------------");
		System.out.println("Concurrent Socket Server\n");
		System.out.println("Service is " + Colors.Green + "Online" + Colors.Reset);
		System.out.println("Listening on Port " + Colors.Green + Port + Colors.Reset);
		System.out.println("-------------------------\n");
	}
	
	public static void ProcessRequest(ClientHandler Client)
	{
		MessageControl.lock(); // Wait until able to gain control
		System.out.print(
			"Processing " 
			+ Colors.Cyan 
			+ String.format("%-7s", Client.Request.toString())
			+ Colors.Reset 
			+ " Request from Client "
            + Colors.Green 
            + String.format("%2s", Integer.toString(Client.iClient))
            + Colors.Reset
            + " @ <"
            + Colors.Yellow 
			//+ Client.Sock.getRemoteSocketAddress().toString().substring(1)
            + "13X.XXX.XXX.XXX:XXXXX"
			+ Colors.Reset + ">\n"
		);
		MessageControl.unlock(); // Release control
	}
}