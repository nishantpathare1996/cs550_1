import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.net.*;

public class get {
	static TreeMap<String, String> sortedKey = new TreeMap<>();
	public static HashMap<String, ArrayList<String>> filemap = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) throws IOException {
		ServerSocket myServerSocket = new ServerSocket(5005);
		ThreadPoolExecutor workerpool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		while (true) {
			Socket skt = null;
			try {

				skt = myServerSocket.accept();
				System.out.println("A new client is connected : " + skt);

				ObjectInputStream objectInput = new ObjectInputStream(skt.getInputStream());
				ObjectOutputStream objectOutput = new ObjectOutputStream(skt.getOutputStream());

				System.out.println("\nAssigning new thread for this client");

				PeerHandler clientrequest = new PeerHandler(skt, objectInput, objectOutput);
				workerpool.execute(clientrequest);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class PeerHandler implements Runnable {
	final ObjectInputStream ois1;
	final ObjectOutputStream oos1;
	final Socket s1;

	public PeerHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos) {
		this.s1 = s;
		this.ois1 = ois;
		this.oos1 = oos;
		// totalfiles.putAll(map);
	}

	public void run() {
		try {
			ArrayList<String> titleList = new ArrayList<String>();
			Object object = ois1.readObject();
			boolean search;
			if(object instanceof String) {
				search = true;
			} else {
				search = false;
			}
//			try {
//				
//				
//			} catch (Exception e) {
//				
//			}

			if (search == false) {
				titleList = (ArrayList<String>) object;
				for (int i = 0; i < titleList.size(); i++) {
					ArrayList<String> titles = get.filemap.get(titleList.get(i));
					if (titles != null) {
						titles.add(s1.getInetAddress().toString().substring(1));
					} else {
						ArrayList<String> ipaddresses = new ArrayList<String>();
						ipaddresses.add(s1.getInetAddress().toString().substring(1));
						get.filemap.put(titleList.get(i), ipaddresses);
					}

				}
				System.out.print(get.filemap);

			} else {
				System.out.println("A new client is connected : " + s1);
				System.out.println("Searching file in get");
				String fileName = (String) object;
				ArrayList<String> ipaddresses = get.filemap.get(fileName);
				this.oos1.writeObject(ipaddresses);
				System.out.println("output>>>>>>>> " + String.valueOf(ipaddresses));
				this.oos1.flush();
			}
//			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
