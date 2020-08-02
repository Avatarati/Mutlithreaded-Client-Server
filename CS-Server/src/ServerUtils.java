/*********************************************
* Concurrent Socket Server
* 
* ServerUtils.java
* Logging, Command Processing, and Execution
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.net.Socket;

public class ServerUtils extends ServerMessage
{
	private static void LogException(Exception EX)
	{
		File F = new File("Log.log");
		
		try
		{
			FileWriter W = new FileWriter(F, true); // Default append
			
			if(!F.exists())
				F.createNewFile();
			
			W.write(EX.getMessage()); // Append error message to log
			W.close(); // Unlock
		}
		catch(IOException IO)
		{
			IO.printStackTrace();
		}
	}
	
    public static void HandleRequest(ClientHandler Client)
    {
        switch(Client.Request)
        {
            case Time:    SendDateTime(Client); break;
            case Uptime:  SendUptime(Client); break;
            case Memory:  SendMemoryUsage(Client); break;
            case Netstat: SendNetStat(Client); break;
            case Users:   SendUsers(Client); break;
            case Process: SendProcess(Client); break;
            case Stop:    StopServer(Client.Sock); break;
        }
        ProcessRequest(Client); // Output message to server console
    }
	
	private static void ProcessCommand(PrintWriter Out, String Command)
	{
		try
		{   // Process request in local shell environment
			Process p = Runtime.getRuntime().exec(Command); // Execute
			BufferedReader In = new BufferedReader(new InputStreamReader(p.getInputStream())); // Capture output
			String S = null;

			while ((S = In.readLine()) != null)
				Out.println(S); // Return output result to client
		}
		catch(Exception EX)
		{
			LogException(EX);
		}
	}
	
	private static void SendDateTime(ClientHandler Client)
	{
		DateFormat D = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
		Date date = new Date();
		Client.Out.println(D.format(date));
	}
	
	private static void SendMemoryUsage(ClientHandler Client)
	{        
        long StartMemory = Server.GetMemory();
		long EndMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		float ActualMemory = (float)(EndMemory - StartMemory) / 1000000; // Divide by 10^6 to get MB

		ProcessCommand(Client.Out, "free");
		String S = String.format("Program Memory In Use: %.04f MB", ActualMemory);
		Client.Out.println(S); // Return program memory to client
	}
	
	private static void SendUptime(ClientHandler Client)
	{
        long Start  = Server.GetTime();
		int Uptime  = (int)(System.currentTimeMillis() - Start);
		int Second  = Uptime / 1000;
		int Minute  = Second / 60;
		int Hour    = Minute / 60;
		int Days    = Hour / 24;
		int Hours   = Hour % 24;
		int Minutes = Minute % 60;
		int Seconds = Second % 60;
		
		String S = String.format(
			"Program Uptime: %01d Day%s %02d Hour%s %02d Minute%s %02d Second%s", 
			Days,    (Days    == 1 ? "" : "s"),
			Hours,   (Hours   == 1 ? "" : "s"), 
			Minutes, (Minutes == 1 ? "" : "s"),
			Seconds, (Seconds == 1 ? "" : "s")
		);
		
		// Send hardware and software uptime
		Client.Out.println(S);
		Client.Out.print("Machine Uptime: ");
		ProcessCommand(Client.Out, "uptime");
	}
	
	private static void SendNetStat(ClientHandler Client)
	{
		ProcessCommand(Client.Out, "netstat");
	}
	
	private static void SendUsers(ClientHandler Client)
	{
		ProcessCommand(Client.Out, "users");
	}
	
	private static void SendProcess(ClientHandler Client)
	{
		ProcessCommand(Client.Out, "ps -ef");
	}
	
	public static void StopServer(Socket Sock)
	{
		Server.KillFlag();
	}
}