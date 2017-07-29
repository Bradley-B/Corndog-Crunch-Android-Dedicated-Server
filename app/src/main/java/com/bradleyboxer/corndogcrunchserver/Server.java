package com.bradleyboxer.corndogcrunchserver;

public class Server {

	public static ServerAcceptor sa = null;
	public static ServerController sc = null;
	public static boolean serverStarted = false;

	public static void start(String port) {
		if(!serverStarted) {
            sa = new ServerAcceptor(Integer.valueOf(port));
            sa.start();

            sc = new ServerController();
            sc.start();
        }
        serverStarted = true;
	}
}
