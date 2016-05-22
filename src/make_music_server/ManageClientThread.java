package make_music_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

class ManageClientThread extends Thread{
	private Socket sock;
	private String id;
	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private HashSet<String> roomList;
	
	public ManageClientThread(Socket sock, HashSet<String> roomList) throws IOException{
		this.sock = sock;
		this.roomList = roomList;
		inputStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outputStream = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		System.out.println("접속 성공!");
		
		id = inputStream.readLine();
		
		System.out.println("Server에 접속한 사용자의 아이디는 "+id+"입니다.");
	}
	
	public void run(){
		try{
			String line = null;
			
			while((line = inputStream.readLine()) != null){
				System.out.println(line);
				if(line.equals("@quit"))
					break;
				else if(line.equals("@addRoom")){
					synchronized(roomList){
						roomList.add(id);
					}
					outputStream.println("@END addRoom");
					outputStream.flush();
				}
				else if(line.equals("@removeRoom")){
					synchronized(roomList){
						roomList.remove(id);
					}
					outputStream.println("@END removeRoom");
					outputStream.flush();
				}
				else if(line.equals("@showRoomList")){
					Iterator<String> it = roomList.iterator();
					while(it.hasNext()){
						String room = (String)it.next();
						outputStream.println(room);
						outputStream.flush();
					}
					outputStream.println("@END showRoomList");
					outputStream.flush();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try{
				if(sock != null)
					sock.close();
			} catch(Exception ex){
			}
		}
	}
}