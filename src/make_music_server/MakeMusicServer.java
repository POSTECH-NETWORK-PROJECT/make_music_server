package make_music_server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class MakeMusicServer {
	public static void main(String[] args){
		ServerSocket server = null;
		try{
			server = new ServerSocket(10002);
			System.out.println("[MAIN SERVER] ������ ��ٸ��ϴ�.");
			HashSet<String> roomList = new HashSet<String>();
			while(true){
				Socket sock = server.accept();
				ManageClientThread t = new ManageClientThread(sock,roomList);
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

