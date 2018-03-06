import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client extends Thread {
	
	DatagramSocket client = null;
	
	public Client(String host,int port) throws SocketException, UnknownHostException {
		this.client = new DatagramSocket();
	}
	
	
	@Override
	public void run() {
		super.run();
		
		Scanner sc = new Scanner(System.in);
		InetAddress addr = null;
		//创建一个地址对象
		try {
			addr = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//设置初始timeout
		String line = null;
		int timeout = 100;
		
		//循环询问是否要输入
		while(true){
			
			System.out.println("please input the OC num1 num1");
			//获取输入内容
			line = sc.nextLine().trim();
			byte[] buf = line.getBytes();
			//把数据封装成datagrampacket
			DatagramPacket packet = new DatagramPacket(buf, buf.length,addr,8000);
			
			int stateCode = 0;
			float result = 0;
			//计数，用于设置当前数据包丢失次数
			int counter = 0;
			
			while(true){
				
				try {
					//发送数据
					client.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//如果丢失那么timeout乘以2
				timeout = timeout*2;
				if(timeout >= 2000){
					System.out.println("network error , stop");
					return;
				}
				
				
				try {
					//设置超时
					client.setSoTimeout(timeout);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				
				
				byte[] recbuf = new byte[1024];
				//封装数据包
				DatagramPacket recPacket = new DatagramPacket(recbuf, recbuf.length);
				
				try {
					//接受数据
					client.receive(recPacket);
				} catch (IOException e) {
					counter ++ ;
					System.out.println("reSend "+ counter + " times");
					continue;
				}
				//获取数据
				String[] text = new String(recPacket.getData()).trim().split("\\s+");
				
				if(Integer.valueOf(text[0]) == 200){
					System.out.println("successfuly   :"+text[0]);
					System.out.println("the result is :" + text[1]);
				}else {
					System.out.println("failure , error input , try again");
				}
				//超时重置
				timeout = 100;

				break;
				
			}

		}
		
		
		
		
	}

	public static void main(String[] args) throws SocketException, UnknownHostException {
		Client client = new Client("127.0.0.1", 8080);
		client.start();
	}

}
