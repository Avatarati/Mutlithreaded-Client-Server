/*********************************************
* Concurrent Socket Server
* 
* Server.java
* Entry point for IS Server, flow logic, and 
* command processing
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends ServerUtils
{
    private static final long StartTime = System.currentTimeMillis(); // Uptime reporting
	private static final long StartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); // Memory reporting
    private static boolean RunFlag = true; // Main loop condition
    

    public static long GetMemory()
    {
        return StartMemory;
    }

    public static long GetTime()
    {
        return StartTime;
    }

    public static void KillFlag()
    {
        RunFlag = false;
    }

	public static void main(String[] Args)
	{	
		int Port = 2500;
		
		try
		{
			if (Args.length == 0)
			{
				SendError(Errors.Port); // Must supply port #
				return;
			}

			Port = Integer.parseInt(Args[0]);
			
			if (Port < 1024 || Port > 65535)
				throw new NumberFormatException();
		}
		catch(NumberFormatException e)
		{
			SendError(Errors.Range); // Must supply port #
			return;
		}	
		
		ServerSocket ServerSock = null;
		
		try
		{
			ServerSock = new ServerSocket(Port);
			PrintHeader(Port);

			while (RunFlag)
			{
				Socket Sock = ServerSock.accept(); // Accept new connections
                new ClientHandler(Sock).start(); // Multi-threaded request response		
			}
		}
		catch(IOException e)
		{
			SendError(Errors.Bind);
		}
		finally 
		{
			if(ServerSock != null) 
			{
				try 
				{   // Cleanup
					ServerSock.close();
				}
				catch(IOException e) 
				{
					SendError(Errors.Bind);
				}
			}
		}
		System.out.println("\nThe server has terminated.\n");
	}
}