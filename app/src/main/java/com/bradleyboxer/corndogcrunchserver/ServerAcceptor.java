package com.bradleyboxer.corndogcrunchserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerAcceptor extends Thread{

	private ArrayList<ServerClientManager> threads = new ArrayList<ServerClientManager>();
	int port;
	
	public ServerAcceptor(int port) {
		this.port = port;
	}
	
	public synchronized void removeClient(ServerClientManager cm) {
		threads.remove(cm);
	}
	
	public synchronized void addClient(ServerClientManager cm) {
		threads.add(cm);
	}
	
	public synchronized ArrayList<ServerClientManager> getClients() {
		return threads;
	}
	
	public void run() {
		Socket socket = null;
		ServerSocket serverSocket = null;
		System.out.println("Server Listening......");

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(!serverSocket.isClosed()){
			try{
				socket = serverSocket.accept();

				MainActivity.message = "Connection established with " + socket.getRemoteSocketAddress() + " . Opening in new thread.";
				MainActivity.newMessage = true;

				System.out.println("Connection established with " + socket.getRemoteSocketAddress() + " . Opening in new thread.");
				ServerClientManager connectionToClient = new ServerClientManager(socket);	
				addClient(connectionToClient);
				connectionToClient.start();
				
				try{Thread.sleep(10);} catch(InterruptedException e) {}
				
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("Connection Error");
			}
		}
	}
}