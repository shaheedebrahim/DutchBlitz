/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Server {
    public static int BUFFERSIZE = 32;

    private static DatabaseDriver db;
    private static HashMap<Integer, String> activePlayers = new HashMap<>();
    private static HashMap<String, ArrayList<Integer>> gameList = new HashMap<>();

    public static void main(String args[]) throws Exception
    {
        db = new DatabaseDriver();
        if (args.length != 1)
        {
            System.out.println("Usage: UDPServer <Listening Port>");
            System.exit(1);
        }

        // Initialize buffers and coders for channel receive and send
        String line = "";
        Charset charset = Charset.forName( "us-ascii" );
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer inBuffer = null;
        CharBuffer cBuffer = null;
        int bytesSent, bytesRecv;     // number of bytes sent or received

        // Initialize the selector
        Selector selector = Selector.open();

        // Create a server channel and make it non-blocking
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);

        // Get the port number and bind the socket
        InetSocketAddress isa = new InetSocketAddress(Integer.parseInt(args[0]));
        channel.socket().bind(isa);

        // Register that the server selector is interested in connection requests
        channel.register(selector, SelectionKey.OP_ACCEPT);

        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            while (!terminated)
            {
                if (selector.select(500) < 0)
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }

                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();

                // Walk through the ready set
                while (readyItor.hasNext())
                {
                    // Get key from set
                    SelectionKey key = (SelectionKey)readyItor.next();

                    // Remove current entry
                    readyItor.remove();

                    // Accept new connections, if any
                    if (key.isAcceptable())
                    {

                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accept connection from " + cchannel.socket().toString());

                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    }
                    else
                    {
                        SocketChannel cchannel = (SocketChannel)key.channel();
                        if (key.isReadable())
                        {
                            Socket socket = cchannel.socket();

                            // Open input and output streams
                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
                            cBuffer = CharBuffer.allocate(BUFFERSIZE);

                            // Read from socket
                            bytesRecv = cchannel.read(inBuffer);
                            if (bytesRecv <= 0)
                            {
                                System.out.println("read() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }

                            inBuffer.flip();      // make buffer available  
                            decoder.decode(inBuffer, cBuffer, false);
                            cBuffer.flip();
                            line = cBuffer.toString();
                            String[] split = line.split("\n");
                            System.out.println("Header: " + split[0]);
                            System.out.println("Game name: " + split[1]);
                            System.out.println("Mode: " + split[2]);

                            int[] fields = parseHeader(Long.parseLong(split[0]));
                            System.out.printf("%d %d %d\n", fields[0], fields[1], fields[2]);

                            switch (fields[0]) {
                                case 0:
                                    //login
                                    if(db.checkExistingUser(split[1])){
                                        // Check for validity of username/password combo
                                        boolean valid = db.validatePassword(split[1],split[2]);
                                        if (valid) {
                                            Random rand = new Random();
                                            int id = rand.nextInt(65535) + 1;

                                            bytesSent = send(cchannel, inBuffer, String.valueOf(id));
                                        }
                                        else bytesSent = send(cchannel, inBuffer, "0");
                                    }
                                    else bytesSent = send(cchannel, inBuffer, "0");
                                    break;
                                case 1:
                                    if (split[2].equals("0")) {
                                        System.out.println("test");
                                        if (!gameList.containsKey(split[1])) {
                                            gameList.put(split[1], new ArrayList<Integer>());
                                            gameList.get(split[1]).add(fields[2]);
                                            activePlayers.put(fields[2], split[1]);

                                            bytesSent = send(cchannel, inBuffer, "1");
                                        } else bytesSent = send(cchannel, inBuffer, "0");
                                    }
                                    else {

                                    }
                                    break;
                                case 2:
                                    // chat
                                    break;
                                case 3:
                                    System.out.println(db);
                                    if(!db.checkExistingUser(split[1])){
                                        db.addNewUser(split[1],split[2]);

                                        Random rand = new Random();
                                        int id = rand.nextInt(65535) + 1;

                                        bytesSent = send(cchannel, inBuffer, String.valueOf(id));
                                    }
                                    else bytesSent = send(cchannel, inBuffer, "0");
                                    break;
                                case 4:
                                    // game update
                                    break;
                                case 5:
                                    // status request
                                    break;
                                default:
                                    break;
                            }

                            if (line.equals("terminate\n"))
                                terminated = true;
                        }
                    }
                } // end of while (readyItor.hasNext()) 
            } // end of while (!terminated)
        }
        catch (IOException e) {
            System.out.println(e);
        }

        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext())
        {
            SelectionKey key = (SelectionKey)itr.next();
            //itr.remove();
            if (key.isAcceptable())
                ((ServerSocketChannel)key.channel()).socket().close();
            else if (key.isValid())
                ((SocketChannel)key.channel()).socket().close();
        }
    }

    private static int[] parseHeader(long h) {
        int len = Long.SIZE - Long.numberOfLeadingZeros(h);
        int messType = (int) (h & (7 << (len - 4))) >> (len - 4);
        int bodLength = (int) (h & (Integer.parseInt("11111111", 2) << (len - 12))) >> (len - 12);
        int state = (int) (h & (Integer.parseInt("1111111111111111", 2))) << (len - 28);
        if (messType == 2) {
            int dest = (int) (h & (Integer.parseInt("111111", 2) << (len - 34))) << (len - 34);
            return new int[] {messType, bodLength, state, dest};
        }

        return new int[] {messType, bodLength, state};
    }

    private static int send(SocketChannel cchannel, ByteBuffer buff, String message) throws IOException {
        buff = ByteBuffer.allocateDirect(BUFFERSIZE);
        buff.put((message + "\n").getBytes());
        buff.flip();
        return cchannel.write(buff);
    }
}
