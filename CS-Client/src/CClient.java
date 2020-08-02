/*********************************************
* Concurrent Socket Client
* 
* @file CClient.java
* Multithreaded client class to talk to server
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CClient extends Thread
{	
	String IP;
	int Port;
	int iClient;
	Commands Command;

	public CClient(String IP, int Port, int iClient, Commands Command)
	{
		this.IP = IP;
		this.Port = Port;
		this.iClient = iClient;
		this.Command = Command;
	}

	@Override
	public void run()
	{
		long Start = System.currentTimeMillis();
		long CompletionTime;
		
		try
		{
			// Create socket and initialize IO streams
			Socket         Sock    = new Socket(IP, Port);
			InputStream    iStream = Sock.getInputStream();
			OutputStream   oStream = Sock.getOutputStream();
			BufferedReader In      = new BufferedReader(new InputStreamReader(iStream));
			PrintWriter    Out     = new PrintWriter(oStream, true);;
			
			// Send request
			Out.println(String.format("%02d",  iClient) + Command.toString());
			
			if (Command == Commands.Stop)
				Client.ExitClient();
			
			// Output server response to client console
			ArrayList<String> Output = new ArrayList<String>(); // Buffered Responses
			String Response;
			
			while ((Response = In.readLine()) != null)
				Output.add(Response);
			
			try
			{				
				Client.Mutex.lock(); // Block other threads from printing to console
				CompletionTime = System.currentTimeMillis() - Start;

				System.out.println(
						Colors.Yellow + "Client " + (iClient + 1) 
					    + Colors.Reset + " <" 
					    + Colors.Cyan + Command.toString() 
					    + Colors.Reset 
					    + "> Turnaround Time: "
					    + Colors.Green
					    + CompletionTime + Colors.Reset + " ms"
			    );
				System.out.println("------------------------------------------");
				
				for (String S : Output)
					System.out.println(S); // Output response from server
				
				System.out.println();
				Client.CompletionTimes[iClient] = CompletionTime;
				Client.Mutex.unlock(); // Allow other threads to report
			}
			finally
			{
				Sock.close(); // Cleanup
			}
        } 
		catch (UnknownHostException ex) 
		{
			ClientIO.SendError(Errors.Connect);
        }
		catch (IOException ex)
		{
        	ClientIO.SendError(Errors.Bind);
        }		
	}
}