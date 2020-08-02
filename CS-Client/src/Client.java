/*********************************************
* Concurrent Socket Client
*  
* Client.java
* Main entry and flow logic
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends ClientIO
{
	public static long CompletionTimes[];
	public static ReentrantLock Mutex;
	private static final int TIMEOUT = 10000;
	
	public static void main(String[] args)
	{
		PrintHeader();

		int NumClients, TotalTime;
		long StartLoop, AverageTime;
		Commands Command;
		boolean Timeout;
		
		// Get Connection Info
		String IP = GetIP();
		int Port  = GetPort();
		Mutex = new ReentrantLock();
		
		// Main program loop
		while (true)
		{
			Command    = GetCommand();			
			NumClients = Command == Commands.Stop ? 1 : GetClients();

			System.out.println("");

			CompletionTimes = new long[NumClients];
			Arrays.fill(CompletionTimes, -1); // Initialize timing
			
			for (int iClient = 0; iClient < NumClients; iClient++)
			{
				Thread T = new CClient(IP, Port, iClient, Command); // Create client thread
				T.start(); // Process request
			}
			
			Timeout = false;
			StartLoop = System.currentTimeMillis(); // Timer start
			
			// This thread busy until all client threads report times
			for (int i = 0; i < CompletionTimes.length; i++)
			{
				if (System.currentTimeMillis() - StartLoop > TIMEOUT)
				{
					Timeout = true; // Server taking too long
					SendError(Errors.Timeout);
					break;
				}
				if (CompletionTimes[i] == -1)
				{
					i = -1;// Hold until all threads are finished
					continue;
				}
			}
			
			if (Timeout)
				continue; // Try again with new user input

			TotalTime = 0;
			
			for (long i : CompletionTimes)
				TotalTime += i;
			
			AverageTime = TotalTime / NumClients;			
			ReportTimes(TotalTime, AverageTime);
		}
	}

	public static void ExitClient()
	{
		System.out.println("\nThe client has terminated.\n");
		System.exit(0);
	}
}
