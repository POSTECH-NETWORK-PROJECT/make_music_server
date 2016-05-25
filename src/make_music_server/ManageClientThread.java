package make_music_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;

class ManageClientThread extends Thread{
	private Socket sock;
	private String id;
	private String ipAddress;
	private BufferedReader inputStream;
	private PrintWriter outputStream;
	private HashMap<String, String> roomList;
	private HashMap<String, String> userList;
	
	public ManageClientThread(Socket sock, HashMap<String, String> roomList, HashMap<String, String> userList) throws IOException{
		this.sock = sock;
		this.roomList = roomList;
		this.userList = userList;
		inputStream = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outputStream = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		// Socket setting.
		System.out.println("접속 성공!");
		
		// Client send ID to server.
		id = inputStream.readLine();
		ipAddress = sock.getInetAddress().getHostAddress();
		// ID and address setting.
		synchronized(userList){
			userList.put(ipAddress, id);
		}
		System.out.println(sock.toString());
		System.out.println("Server에 접속한 사용자의 ID는 "+id+"입니다.");
	}
	
	public void run(){
		try{
			String line = null;
			
			while((line = inputStream.readLine()) != null){
				System.out.println(ipAddress+"에서 보낸 명령어: "+line);
				// @quit is end signal from client.
				if(line.equals("@quit"))
					break;
				// @addRoom is to add room to roomList signal from client.
				else if(line.indexOf("@addRoom") == 0){
					int roomNameIndex = line.indexOf(" ")+1;
					synchronized(roomList){
						roomList.put(ipAddress, line.substring(roomNameIndex));
					}
					outputStream.println("@END addRoom");
					outputStream.flush();
				}
				// @removeRoom is to remove room to roomList signal from client.
				else if(line.equals("@removeRoom")){
					synchronized(roomList){
						roomList.remove(ipAddress);
					}
					outputStream.println("@END removeRoom");
					outputStream.flush();
				}
				// @showRoomList is the signal from client that request room list.
				else if(line.equals("@showRoomList")){
					Iterator<HashMap.Entry<String, String>> it = roomList.entrySet().iterator();
					
					while(it.hasNext()){
						HashMap.Entry<String, String> room = it.next();
						// message format: (room name):(room ip address) 
						outputStream.println(room.getValue() + ":" + room.getKey());
						outputStream.flush();
					}
					outputStream.println("@END showRoomList");
					outputStream.flush();
				}
				// @showUserList is the signal form client that request user list.
				else if(line.equals("@showUserList")){
					Iterator<String> it = userList.values().iterator();
					while(it.hasNext()){
						String user = it.next();
						outputStream.println(user);
						outputStream.flush();
					}
					outputStream.println("@END showUserList");
					outputStream.flush();
				}
			}
		} catch(SocketException se){
			// Client exit using X button in right-top side.
			try{
				if(outputStream != null)
					outputStream.close();
				if(inputStream != null)
					inputStream.close();
				if(sock != null)
					sock.close();
			} catch(IOException e){
				e.printStackTrace();
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			System.out.println(ipAddress+"와 Connection이 끊겼습니다.");
			synchronized(userList){
				userList.remove(ipAddress);
			}
			synchronized(roomList){
				roomList.remove(ipAddress);
			}
			try{
				if(sock != null)
					sock.close();
			} catch(Exception ex){
			}
		}
	}
}