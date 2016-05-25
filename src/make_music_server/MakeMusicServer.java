package make_music_server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MakeMusicServer {
	public static void main(String[] args){
		ServerSocket server = null;
		try{
			server = new ServerSocket(10002);
			System.out.println("[MAIN SERVER] 접속을 기다립니다.");
			// Save IP address and room name and id using hash map.
			HashMap<String, String> roomList = new HashMap<String, String>();
			HashMap<String, String> userList = new HashMap<String, String>();
			// Server doesn't end unless there are exception.
			while(true){
				Socket sock = server.accept();
				// Using thread to handle each client.
				ManageClientThread t = new ManageClientThread(sock,roomList, userList);
				t.start();
			}
		} catch(Exception e){
			e.printStackTrace();
			try{
				if(server != null)
					server.close();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}

