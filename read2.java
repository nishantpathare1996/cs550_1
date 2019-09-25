import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class read2 {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		try {

			File f = new File("/home/khanderao/Desktop/aos/Data/2/");
			Socket socket = new Socket("127.0.0.1", 5005);

			ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
			System.out.println(names.toString());

			objectOutput.writeObject(names);
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread clientHandler = new Thread() {
			public void run() {
				String toreturn;
				String filename;

				while (true) {
					try {

						System.out.println("Press 1 or 2 to Select \n" + "1. Search a File\n" + "2. Exit");

						// receive the answer from client
						int received = new Scanner(System.in).nextInt();

						if (received == 2) {
							System.exit(0);

						} else if (received == 1) {
							System.out.print("Enter file name: ");
							String fileName = new Scanner(System.in).nextLine();
							Socket socket = new Socket("127.0.0.1", 5005);

							ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());

							dos.writeObject(fileName);
							dos.flush();
							ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());

							ArrayList<String> list = (ArrayList<String>) dis.readObject();
							System.out.println(list);
							//to download
							
							Socket peerSocket = null;
							ObjectOutputStream fileOutput = null;
							FileOutputStream fileOutputStream = null;
							InputStream inputStream = null;
							BufferedOutputStream bufferedOutputStream = null;
							try {
								peerSocket = new Socket("127.0.0.1", 5007);
								fileOutput = new ObjectOutputStream(peerSocket.getOutputStream());
								fileOutput.writeUTF(fileName);
								fileOutput.flush();

								inputStream = peerSocket.getInputStream();
								fileOutputStream = new FileOutputStream("/home/khanderao/Desktop/aos/Data/2/" + fileName);
								bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

								byte[] array = inputStream.readAllBytes();
								bufferedOutputStream.write(array, 0, array.length);
								bufferedOutputStream.flush();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								try {
									fileOutput.close();
									fileOutputStream.close();
									bufferedOutputStream.close();
									inputStream.close();
									fileOutput.close();
									peerSocket.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
							
							/*
							 * String abc="hii"; dos.writeUTF(abc); dos.flush(); socket.close();
							 */

						} else {
							System.out.println("Invalid Input");
						}

					}

					catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		};

		clientHandler.start();

		Thread clientSocketThread = new Thread() {
			public void run() {
				ServerSocket clientPeerSocket = null;
				try {
					clientPeerSocket = new ServerSocket(5007);
					while (true) {
						Socket socket = clientPeerSocket.accept();

						ObjectInputStream fileInput = new ObjectInputStream(socket.getInputStream());
						String fileName = fileInput.readUTF();

						// File fileToShare = new File(FILES_PATH, fileName);
						File f = new File("/home/khanderao/Desktop/aos/Data/1/" + fileName);

//						ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
//						ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
//						ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
//						System.out.println(names.toString());
//
//						objectOutput.writeObject(names);
//						socket.close();

						byte[] byteslen = new byte[(int) f.length()];

						FileInputStream fileInputStream = new FileInputStream(f);
						BufferedInputStream bufInputStream = new BufferedInputStream(fileInputStream);
						bufInputStream.read(byteslen, 0, byteslen.length);

						OutputStream outputStream = socket.getOutputStream();

						outputStream.write(byteslen, 0, byteslen.length);
						outputStream.flush();

						outputStream.close();
						fileInputStream.close();
						bufInputStream.close();
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}

/*
 * class SendFile extends Thread {
 * 
 * public void run() { String filename; System.out.println("Enter File Name: ");
 * Scanner sc=new Scanner(System.in); filename=sc.nextLine(); sc.close();
 * while(true) { //create server socket on port 6124 ServerSocket ss=new
 * ServerSocket(6124); System.out.println ("Waiting for request"); Socket
 * s=ss.accept(); System.out.println
 * ("Connected With "+s.getInetAddress().toString()); DataInputStream din=new
 * DataInputStream(s.getInputStream()); DataOutputStream dout=new
 * DataOutputStream(s.getOutputStream()); try{ String str="";
 * 
 * str=din.readUTF(); System.out.println("SendGet....Ok");
 * 
 * if(!str.equals("stop")){
 * 
 * System.out.println("Sending File: "+filename); dout.writeUTF(filename);
 * dout.flush();
 * 
 * File f=new File(filename); FileInputStream fin=new FileInputStream(f); long
 * sz=(int) f.length();
 * 
 * byte b[]=new byte [1024];
 * 
 * int read;
 * 
 * dout.writeUTF(Long.toString(sz)); dout.flush();
 * 
 * System.out.println ("Size: "+sz); System.out.println
 * ("Buf size: "+ss.getReceiveBufferSize());
 * 
 * while((read = fin.read(b)) != -1){ dout.write(b, 0, read); dout.flush(); }
 * fin.close();
 * 
 * System.out.println("..ok"); dout.flush(); } dout.writeUTF("stop");
 * System.out.println("Send Complete"); dout.flush(); } catch(Exception e) {
 * e.printStackTrace(); System.out.println("An error occured"); } din.close();
 * s.close(); ss.close(); }
 * 
 * } }
 */
