/*********************************************
* Concurrent Socket Server
* 
* ClientHandler.java
* Multi-threaded class to handle client
* packet interpretation and requests
* @author Ethan Hannen
* @author Douglas McBride 
**********************************************/

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientHandler extends Thread
{
	public Socket Sock;
	public int iClient;
	public PrintWriter Out;
	public Commands Request;
	
	public ClientHandler(Socket Sock)
	{
		this.Sock = Sock;
	}
	
	@Override
	public void run()
	{
        try 
        {
            InputStream    iStream = Sock.getInputStream();
            OutputStream   oStream = Sock.getOutputStream();
            BufferedReader In      = new BufferedReader(new InputStreamReader(iStream));
            Out = new PrintWriter(oStream, true);
            
            try
            {   // Process Request
            	String S = In.readLine(); // Extract contents of packet
            	iClient = Integer.parseInt(S.substring(0,2)) + 1;
                Request = Commands.valueOf(S.substring(2)); // Translate to Enum
                ServerUtils.HandleRequest(this); // Handle
            }
            catch(IllegalArgumentException e)
            {
            	ServerUtils.SendError(Errors.Command);
            }
            finally
            {   // Cleanup
            	Sock.close();
                iStream.close();
                oStream.close();
                In.close();
                Out.close();
            }
        }
        catch(IOException e)
        {
        	ServerUtils.SendError(Errors.Bind);
        }
	}
}