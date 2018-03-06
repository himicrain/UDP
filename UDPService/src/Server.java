import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;


public class Server extends Thread {
	
	public static DatagramSocket server = null;
	
	public Server(int port) throws SocketException  {
		this.server = new DatagramSocket(port);
	}

	
	//检查数据
	public static String[] check(String line){
		
		String[] data = line.split("\\s+");
		int a = 0;
		int b = 0;
		String OC = data[0];
		
		//判断操作符号操作数据是否符合规则
		String OCs = "+-*/";
		if(!OCs.contains(OC)) return null;
		
		try{
			a = Integer.valueOf(data[1].trim());
			b = Integer.valueOf(data[2].trim());
		}catch(Exception e){
			//抛出异常则说明数据不合格
			return null;
		}
		if(OC.equals("/") && b == 0) return null;
		
		return data;
	}
	//通过数据计算数值
	public static float caculate(String[] data){
		float result = 0;
		
		String OC = data[0].trim();
		float a = (float)Integer.valueOf(data[1].trim());
		float b = (float)Integer.valueOf(data[2].trim());
		
		if(OC.equals("+"))
			result = a + b;
		else if (OC.equals("-")) 
			result = a - b;
		else if (OC.equals("*")) 
			result = a * b;
		else if (OC.equals("/")) 
			result =a/b;
		
		return result;
	}
	
	
	
	@Override
	public void run() {
		super.run();
	
		int stateCode = 0;
		float result = 0;
		
		while (true) {
			byte[] buffer = new byte[1024];
			//创建接受数据的数据包
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				//接受数据
				this.server.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//产生一个20以内的随机整数
			int randNum = new Random().nextInt(20);
			//如果整数小于10,那么丢弃
			if(randNum < 10){
				System.out.println("packet loss");
				continue;
			}
			System.out.println("server got data");
			//获取数据包的地址
			InetAddress host = packet.getAddress();
			//获取端口
			int port = packet.getPort();
			//获取数据
			String data = new String(packet.getData());
			String[] datas = check(data);
			//check后如果为空那么返回300
			if(datas == null){
				stateCode = 300;
				result = -1;
			}else {
				//计算结果
				result = caculate(datas);
				stateCode = 200;
			}
			//返回结果
			String respones = stateCode+"  "+String.valueOf(result).trim();
			
			byte[] rs = respones.getBytes();
			try {
				//发送结果数据
				server.send(new DatagramPacket(rs, rs.length,host,port));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public static void main(String[] args) throws SocketException{
		
		Server server = new Server(8000);
		server.start();
		
		
	}
	
}
