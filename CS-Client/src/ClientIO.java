/*********************************************
* Concurrent Socket Client
* 
* ClientIO.java
* Methods for input/output, error control, and 
* command processing
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.util.InputMismatchException;
import java.util.Scanner;

enum Errors 
{
	IP,
	Port,
	Bind,
	Client,
	Command,
	Connect,
	Timeout
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

class Colors
{
	public static final String Red    = "\u001B[31m";
	public static final String Cyan   = "\u001B[36m";
	public static final String Green  = "\u001B[32m";
	public static final String Yellow = "\u001B[33m";
	public static final String Reset  = "\u001B[0m";
}

interface Error 
{
	void Show(String S);
}

public class ClientIO
{
	private static Scanner Input = new Scanner(System.in);
	private static boolean FirstPass = true;

	/*****************************************
	* Message Output Methods
	*****************************************/
	
	public static void SendError(Errors e)
	{
		Error E = (String S) -> System.out.println(Colors.Red + "Error" + Colors.Reset + ": " + S);
		
		switch (e)
		{
			case IP:       E.Show("Invalid IP!"); break;
			case Port:     E.Show("Valid Port Range: 1024 to 65535"); break;
			case Bind:     E.Show("Failed to bind socket!"); break;
			case Client:   E.Show("Clients must number <= 100"); break;
			case Command:  E.Show("Unrecognized command!"); break;
			case Connect:  E.Show("Failed to connect to server!"); break;
			case Timeout:  E.Show("No response from server!"); break;
			default:       E.Show("An unknown error occurred..."); break;
		}
	}
	
	public static void PrintHeader()
	{
		System.out.print("\033[H\033[2J"); // Clear console
		System.out.flush();
		System.out.println("-------------------------");
		System.out.println("Concurrent Socket Client\n");
		System.out.println("-------------------------\n");
	}
	
	public static void PrintLegend()
	{
		System.out.printf(
			"\n%s\n\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n\n",
			"The server is able to process the following requests:",
			Colors.Yellow + "  Date"    + Colors.Reset + "    " + "- Get Date",
			Colors.Yellow + "  Uptime"  + Colors.Reset + "  "   + "- Get Uptime",
			Colors.Yellow + "  Memory"  + Colors.Reset + "  "   + "- Get Memory Usage",
			Colors.Yellow + "  Status"  + Colors.Reset + "  "   + "- Get Network Status",
			Colors.Yellow + "  Users"   + Colors.Reset + "   "  + "- Get List of Users",
			Colors.Yellow + "  Process" + Colors.Reset + " "    + "- Get Running Processes",
			Colors.Yellow + "  Stop"    + Colors.Reset + "    " + "- Terminate Client & Server"
		);
	}
	
	public static void ReportTimes(long T, long A)
	{
		System.out.println("Total Turnaround Time (All Clients): " + Colors.Green + T + Colors.Reset + " ms");
		System.out.println("Average Turnaround Time (Per Client): " + Colors.Green + A + Colors.Reset + " ms\n");
	}
	
	/*****************************************
	* User Input Methods
	*****************************************/
	
	private static boolean isValidIP(String IP)
	{
		int Start = 0;
		int Count = 0;
		
		int Dot[] = new int[4];
		Dot[3] = IP.length();
		
		for (int i = 0; i < IP.length(); i++)
			if (IP.charAt(i) == '.')
				Dot[Count++] = i;

		if (Count != 3 || Dot[0] == 0)
			return false;
		
		for (int i = 0; i < 4; i++)
		{
			try 
			{
				int j = Integer.parseInt(IP.substring(Start, Dot[i]));
				if (j < 0 || j > 0xFF)
					throw new NumberFormatException();
				Start = Dot[i] + 1;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
		return true;
	}
	
	public static String GetIP()
	{
		String IP = "";
		boolean Valid = false;
		
		System.out.println("Enter connection details:");

		while(!Valid)
		{
			System.out.print("IP Address: ");
			IP = Input.next();
			Valid = isValidIP(IP);
			
			if (!Valid)
				SendError(Errors.IP);
		}
		return IP;
	}
	
	public static int GetPort()
	{
		int Port = 0;

		for(;;)
		{
			System.out.print("Port Number: ");
			try 
			{
				Port = Input.nextInt();
				if (Port > 0xFFFF || Port < 1024) // Well-known or out-of-range
					throw new NumberFormatException();
				return Port;
			}
			catch(NumberFormatException e) 
			{
				// Fall through
			}
			catch(InputMismatchException  e)
			{
				Input.nextLine();
			}
			SendError(Errors.Port);
		}
	}
	
	public static int GetClients()
	{
		int Num = 1;
		
		for(;;)
		{
			System.out.print("Clients: ");
			try 
			{
				Num = Input.nextInt();
				if (Num <= 100)
					return Num;
			}
			catch (NumberFormatException e) 
			{
				// Fall through
			}
			catch(InputMismatchException  e) 
			{
				Input.nextLine();
			}
			SendError(Errors.Client);
		}
	}
	
	public static Commands GetCommand()
	{
		// Show help on first use only
		if (FirstPass)
		{
			PrintLegend();
			FirstPass = !FirstPass;
		}
		
		while(true)
		{
			System.out.print("Request: ");
			String S = Input.next().toLowerCase();
			
			// Partial-match for typo flexibility
			if (S.contains("up"))
				return Commands.Uptime;
			if (S.contains("time") || S.contains("date"))
				return Commands.Time;
			if (S.contains("mem") || S.contains("usag"))
				return Commands.Memory;
			if (S.contains("stat"))
				return Commands.Netstat;
			if (S.contains("user"))
				return Commands.Users;
			if (S.contains("proc"))
				return Commands.Process;
			if (S.contains("stop"))
				return Commands.Stop;
			if (S.contains("help"))
			{
				PrintLegend();
				continue;
			}
			
			SendError(Errors.Command);
			System.out.println("Type 'help' to list options.");
		}
	}
}