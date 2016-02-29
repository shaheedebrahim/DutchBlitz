import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;

public class Server {
    final static int PACKET_SIZE = 64;
    
    private static DatagramChannel channel = null;
    private static Charset charset = Charset.forName( "us-ascii" );  
    private static CharsetDecoder decoder = charset.newDecoder();  
    private static ByteBuffer inBuffer;
    private static CharBuffer cBuffer;
    
    public static void main(String[] args) throws IOException {
        channel = DatagramChannel.open();
        InetSocketAddress isa = new InetSocketAddress(9876);
        channel.socket().bind(isa);
        
        byte[] inBuff = new byte[PACKET_SIZE];
        DatagramPacket pack = new DatagramPacket(inBuff, PACKET_SIZE);
        
        String in = "";
        while (true) {
            inBuffer = ByteBuffer.allocateDirect(PACKET_SIZE);
			cBuffer = CharBuffer.allocate(PACKET_SIZE);
            
            SocketAddress client = channel.receive(inBuffer);
            
            inBuffer.flip();
            decoder.decode(inBuffer, cBuffer, false);
            cBuffer.flip();
            
            in = cBuffer.toString();
            String[] fields = in.split(" ");
            
            System.out.println("Client: " + in);
            
            String resp = "";
            if (fields.length == 2 && fields[0].equals("admin") && fields[1].equals("admin"))
                resp = "success";
            else resp = "failure";
            
            byte[] rBytes = resp.getBytes();
            inBuffer = ByteBuffer.allocateDirect(rBytes.length);
            inBuffer.put(rBytes);
            inBuffer.flip();
            
            channel.send(inBuffer, client);
        }
    }
}