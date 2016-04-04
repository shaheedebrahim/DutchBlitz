/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.*;
import java.util.*;

public class Server {
    public static int BUFFERSIZE = 32;

    private static DatabaseDriver db;
    //private static HashMap<Integer, InetAddress> ipMap = new HashMap<>();
    private static HashMap<String, Integer> playerIds = new HashMap<>();
    private static HashMap<Integer, String> uNames = new HashMap<>();
    private static HashMap<Integer, SocketChannel> socketMap = new HashMap<>();
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
                            //System.out.println("Mode: " + split[2]);

                            int[] fields = parseHeader(Long.parseLong(split[0]));
                            System.out.printf("%d %d %d\n", fields[0], fields[1], fields[2]);
                            //Kendra
                            //inBuffer.flip();
                            switch (fields[0]) {
                                case 0:
                                    //login
                                    if(db.checkExistingUser(split[1])){
                                        // Check for validity of username/password combo
                                        boolean valid = db.validatePassword(split[1],split[2]);
                                        if (valid && !uNames.containsValue(split[1])) {
                                            Random rand = new Random();
                                            int id = rand.nextInt(65535) + 1;
                                            uNames.put(id, split[1]);
                                            playerIds.put(split[1], id);

                                            bytesSent = send(cchannel, inBuffer, String.valueOf(id));
                                        }
                                        else bytesSent = send(cchannel, inBuffer, "0");
                                    }
                                    else bytesSent = send(cchannel, inBuffer, "0");
                                    break;
                                case 1:
                                    // create/join game
                                    // Create game condition
                                    if (split[2].equals("0")) {
                                        if (!gameList.containsKey(split[1])) {
                                            gameList.put(split[1], new ArrayList<Integer>());
                                            gameList.get(split[1]).add(fields[2]);
                                            activePlayers.put(fields[2], split[1]);
                                            System.out.printf("Mapping socket %s\n", cchannel.getRemoteAddress());
                                            //ipMap.put(fields[2], addr);

                                            bytesSent = send(cchannel, inBuffer, "1");
                                            //(new ServerThread(socket)).start();
                                        } else bytesSent = send(cchannel, inBuffer, "0");
                                    }
                                    // Join game condition
                                    else {
                                        if (gameList.containsKey(split[1])) {
                                            ArrayList<Integer> playerList = gameList.get(split[1]);
                                            broadCast(playerList, inBuffer, uNames.get(fields[2]));

                                            playerList.add(fields[2]);
                                            activePlayers.put(fields[2], split[1]);
                                            //ipMap.put(fields[2], addr);
                                            //(new ServerThread(socket)).start();
                                            bytesSent = send(cchannel, inBuffer, "1");
                                        }
                                        else bytesSent = send(cchannel, inBuffer, "0");
                                    }
                                    break;
                                case 2:
                                    // chat
                                    String message = split[1];
                                    if (message.length() >= 2 && message.startsWith("/w")) {
                                        String[] parts = message.split(" ", 3);
                                        if (playerIds.containsKey(parts[1])) {
                                            SocketChannel chan = socketMap.get(playerIds.get(parts[1]));
                                            send(chan, inBuffer, "chat");
                                            send(chan, inBuffer, message);
                                            send(chan, inBuffer, uNames.get(fields[2]));
                                        }
                                    }
                                    else {
                                        if (activePlayers.containsKey(fields[2])) {
                                            ArrayList<Integer> pList = gameList.get(activePlayers.get(fields[2]));
                                            /*send(socketMap.get(fields[2]), inBuffer, "chat");
                                            send(socketMap.get(fields[2]), inBuffer, message);
                                            send(socketMap.get(fields[2]), inBuffer, uNames.get(fields[2]));*/
                                            broadCast(pList, inBuffer, "chat\n" + message + "\n" + uNames.get(fields[2]));
                                            /*broadCast(pList, inBuffer, "chat");
                                            broadCast(pList, inBuffer, message);
                                            broadCast(pList, inBuffer, uNames.get(fields[2]));*/
                                        }
                                    }
                                    break;
                                case 3:
                                    // create account
                                    System.out.println(db);
                                    if(!db.checkExistingUser(split[1])){
                                        db.addNewUser(split[1],split[2]);

                                        Random rand = new Random();
                                        int id = rand.nextInt(65535) + 1;
                                        uNames.put(id, split[1]);
                                        playerIds.put(split[1], id);

                                        bytesSent = send(cchannel, inBuffer, String.valueOf(id));
                                    }
                                    else bytesSent = send(cchannel, inBuffer, "0");
                                    break;
                                case 4:
                                    // status messages
                                    if (split[1].equals("request_names")) {
                                        ArrayList<Integer> playerList = gameList.get(activePlayers.get(fields[2]));
                                        socketMap.put(fields[2], cchannel);
                                        System.out.printf("Mapping socket %s\n", cchannel.getRemoteAddress());
                                        for (int i = 0; i < playerList.size(); i++) {
                                            if (fields[2] != playerList.get(i)) {
                                                send(cchannel, inBuffer, uNames.get(playerList.get(i)));
                                                System.out.println("Sending " + uNames.get(playerList.get(i)));
                                            }
                                        }
                                        send(cchannel, inBuffer, "0");
                                    }
                                    else if (split[1].equals("leave")) {
                                        String game = activePlayers.remove(fields[2]);
                                        socketMap.get(fields[2]).close();
                                        socketMap.remove(fields[2]);
                                        ArrayList<Integer> pList = gameList.get(game);
                                        if (pList.size() == 1)
                                            gameList.remove(game);
                                        else pList.remove(fields[2]);
                                    }
                                    else if (split[1].equals("start")) {
                                        System.out.println(gameList.get(activePlayers.get(fields[2])));
                                        System.out.printf("Number of players: %d\n", gameList.get(activePlayers.get(fields[2])).size());
                                        if (gameList.get(activePlayers.get(fields[2])).size() > 1)
                                            send(cchannel, inBuffer, "1");
                                        else send(cchannel, inBuffer, "0");
                                    }
                                    else if (split[1].equals("logout")) {
                                        System.out.printf("Logout %s\n", playerIds.get(uNames.get(fields[2])));
                                        playerIds.remove(uNames.get(fields[2]));
                                        uNames.remove(fields[2]);
                                    }
                                    break;
                                case 5:
                                    // game update
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
        /*if (messType == 2) {
            int dest = (int) (h & (Integer.parseInt("111111", 2) << (len - 34))) << (len - 34);
            return new int[] {messType, bodLength, state, dest};
        }*/

        return new int[] {messType, bodLength, state};
    }

    // Sends a message on the given channel
    private static int send(SocketChannel cchannel, ByteBuffer buff, String message) throws IOException {
        buff = ByteBuffer.allocateDirect(BUFFERSIZE);
        buff.put((message + "\n").getBytes());
        //Kendra
        buff.flip();
        return cchannel.write(buff);
    }

    // Sends a message to the given list of players
    private static void broadCast(ArrayList<Integer> players, ByteBuffer buff, String message) throws IOException {
        for (int i = 0; i < players.size(); i++) {
            send(socketMap.get(players.get(i)), buff, message);
            System.out.printf("Send to %s: %s\n", uNames.get(players.get(i)), message);
            System.out.printf("Connected: %s on addr %s\n", socketMap.get(players.get(i)).isConnected(), socketMap.get(players.get(i)).getRemoteAddress());
        }
    }

    // Sends a message on the given socket
    private static void sockSend(Socket sock, String message) {
        try {
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            out.writeBytes(message + "\n");
            sock.close();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Used to handle persistent TCP connections between server and client
    private class ServerThread extends Thread {
        private Socket socket;
        private BufferedReader in;
        private DataOutputStream out;

        public ServerThread(Socket s) {
            socket = s;
        }

        public void listen() {
            try {
                boolean terminated = false;

                while (!terminated) {
                    String header = in.readLine();
                    int[] fields = parseHeader(Long.parseLong(header));


                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                socket.close();
                in.close();
                out.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());
            }
            catch(UnknownHostException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            listen();

            close();
        }
    }
}
