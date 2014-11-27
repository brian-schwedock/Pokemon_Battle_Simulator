package pokemon_simulator;

import java.net.InetAddress;

/*
 * Run this class to find the IP address your system is running on
 */

public class IPAddress {
	public static void main (String [] args) throws Exception{
		System.out.println(InetAddress.getLocalHost());
	}
}
