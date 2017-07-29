package com.bradleyboxer.corndogcrunchserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerClientInputManager extends Thread {

	BufferedReader in = null;
	String input = null;
	Socket socket = null;
	
	public ServerClientInputManager(Socket socket) {
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception in ServerClientManager thread creating bufferedreader");
		} 
	}
	
	public ServerClientManager findMaster() {
		
		for(ServerClientManager cm : Server.sa.getClients()) {
			if(socket.getRemoteSocketAddress().toString().equals(cm.socket.getRemoteSocketAddress().toString())) {
				return cm;
			}
		}
		return null;
	}
	
	public void processCommand(String command) {
		ServerClientManager cm = findMaster();
		
		if(command==null) {
			cm.closeConnection();
		}
		
		if(command.startsWith("/")) {
			String basecommand = Misc.getCommand(command);
			String subcommand = Misc.getSubcommand(command);
			
			//System.out.println("basecommand = "+basecommand);
			
			if(basecommand.equals("ready")) { //add commands here
				cm.setReady();
				Server.sc.sendMessageToClients(cm.name+" readied!");
			} else if(basecommand.equals("unready")) {
				cm.setUnready();
				Server.sc.sendMessageToClients(cm.name+" unreadied!");
			} else if(basecommand.contains("scoreReport")) {
				String sscore = subcommand.trim();
				cm.setScore(Integer.valueOf(sscore));
			} else if(basecommand.equals("nameReport")) {
				cm.setPlayerName(subcommand);
			} else if(basecommand.equals("disconnect")) {
				cm.closeConnection();
			} else if(basecommand.equals("start")) {
				//Runnable newSc = () -> {Server.sc.runGame();};
				new Thread(new Runnable() {
					@Override
					public void run() {
						Server.sc.runGame();
					}
				}).start();
			} else {
				cm.sendMessage("command not found");
			}
			
		} else {
			Server.sc.sendMessageToClients(cm.name+": "+command);
		}
		
	}
	
	public void run() {
		
		try {
			while(!socket.isClosed()) {
				
				input = in.readLine(); //thread hangs on this line until new line is found on stream

				MainActivity.message = "Command recieved from " + findMaster().name + " : " + input;
				MainActivity.newMessage = true;

				System.out.println("Command recieved from " + findMaster().name + " : " + input);

				processCommand(input);
				
				try{Thread.sleep(1);} catch(InterruptedException e) {}
			}
		} catch(IOException e) {
			System.out.println("error in reading CIM... closing respective socket");
			ServerClientManager thisCm = findMaster();
			thisCm.closeConnection();
		} catch(NullPointerException e) {

		}
	}
	
}
