package com.Server;

import com.Common.ClientInfo;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Server {
	boolean started = false;
	ServerSocket ss = null;
	
	private int port;
	
	ArrayList<MyClient> clients = new ArrayList<MyClient>();

	ArrayList<ClientInfo> clientInfo = new ArrayList<ClientInfo>();
	
	public static void main(String[] args) {
		new Server(8888);
	}
	
	public Server(int port) {
		this.port = port;
		this.start();
	}
	
	public void start() {
		try {
			ss = new ServerSocket(port);
			started = true;
			System.out.println("端口已开启，占用8888端口号");
		} catch (BindException e) {
			System.out.println("端口使用中");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			while (started) {
				Socket s = ss.accept();
				MyClient c = new MyClient(s);
				System.out.println("a client connected!");
				
				new Thread(c).start();
				clients.add(c);
			}		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 	
		}
	}
	
	class MyClient implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		
		private String userName = null;
		private String ip = null;
		

		public MyClient(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println ("对方退出了！我从List里面去掉了！");
			}
		}
		
		public void run () {
			try {
				while (bConnected) {

					String str = dis.readUTF();
					System.out.println (str);
					
					for (int i = 0; i<clients.size();i++) {
						MyClient c = clients.get(i);
						c.send(str);
					}
				}
			} catch (EOFException e) {
				System.out.println("Client closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)	dis.close();
					if (dos != null)	dos.close();
					if (s != null) 		s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}

}
