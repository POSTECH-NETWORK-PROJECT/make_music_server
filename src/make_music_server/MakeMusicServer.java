package make_music_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

public class MakeMusicServer {
	public static void main(String[] args){
		HashSet<String> roomList = new HashSet<String>();
		try{
			ServerSocket server = new ServerSocket(10001);
			System.out.println("Wait connection...");
			while(true){
				Socket sock = server.accept();
				ManageClientThread t = new ManageClientThread(sock, roomList);
				t.start();
			}
		} catch(Exception e){
			System.out.println(e);
		}
	}
}

class ManageClientThread extends Thread{
	private Socket sock;
	private BufferedReader br;
	private PrintWriter pw;
	private HashSet<String> roomList;
	
	public ManageClientThread(Socket sock, HashSet<String> roomList){
		this.sock = sock;
		this.roomList = roomList;
		try{
			// Set input and output stream.
			pw = new PrintWriter(
					new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			String line = null;
			line = br.readLine();
			System.out.println(line+" is join this server.");
		} catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	public void run(){
		try{
			String line = null;
			
			while((line = br.readLine()) != null){
				// Command createRoom : host make room and server add this room to roomList.
				if(line.equals("/createRoom")){
					synchronized(roomList){
						roomList.add(sock.getInetAddress().getHostAddress());
					}
				}
				// Command deleteRoom : host exit its room and server erase this room to roomList.
				else if(line.equals("/deleteRoom")){
					synchronized(roomList){
						roomList.remove(sock.getInetAddress().getHostAddress());
					}
				}
				// Command showRoomList : client is in the LIST state and request the roomList, so server provide the roomList.
				else if(line.equals("/showRoomList")){
					Iterator<String> it = roomList.iterator();
					while(it.hasNext()){
						String address = it.next();
						pw.println(address);
						pw.flush();
					}
				}
				// Command quit : host or client leave this server.
				else if(line.equals("/quit"))
					break;
			}
		} catch(Exception e){
			System.out.println(e);
		} finally{
			try{
				if(sock != null)
					sock.close();
			} catch(Exception ex){
			}
		}
	}
}