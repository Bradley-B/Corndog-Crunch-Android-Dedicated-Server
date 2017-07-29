package com.bradleyboxer.corndogcrunchserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


class ServerClientManager extends Thread{  

	boolean ready = false;
	String threadName = null;
	PrintWriter out = null;
	Socket socket = null;
	ServerClientInputManager cim = null;
	String name = "new.player";
	int score = 0;
	
	public ServerClientManager(Socket socket){
		this.socket = socket;
	}

	public Score getScore() {
		return new Score(name, score);
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setPlayerName(String name) {
		this.name = name;
	}
	
	public boolean getReadyState() {
		return ready;
	}
	
	public void setUnready() {
		ready = false;
		Server.sc.timeRemaining = 10;
	}
	
	public void setReady() {
		ready = true;
	}
	
	public ServerClientInputManager getCim() {
		return cim;
	}
	
	public void sendMessage(String message) {
		out.println(message);
		System.out.println("Responded to client with : " + message);
		out.flush();
	}

	public void closeConnection() {

		Server.sc.sendMessageToClients(name+" has disconnected!");

		try{
			System.out.println("Connection closing for "+name+"...");
			MainActivity.message = "Connection closing for "+name;
			MainActivity.newMessage = true;

			Server.sa.removeClient(this);
			
			if (cim.in!=null){
				cim.in.close(); 
				System.out.println("Socket input stream closed");
			}
			if(out!=null){
				out.close();
				System.out.println("Socket output closed");
			}	
			if (socket!=null){
				socket.close();
				System.out.println("Socket closed");
			}
			
		}
		catch(IOException ie){
			System.out.println("Socket close error!");
		}
	}
	
	public void run() {
		cim = new ServerClientInputManager(socket);
		cim.start();
		
		try{
			out = new PrintWriter(socket.getOutputStream());	
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("IO exception in ServerClientManager thread creating output stream");
		}
	}
	
}//end class