import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TCPServerGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextArea chatArea;
    private ArrayList<ClientHandler> clients;

    public static void main(String[] args) {
        TCPServerGUI server = new TCPServerGUI();
        server.setVisible(true);
        server.run();
    }

    public TCPServerGUI() {
        setTitle("Admin Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        clients = new ArrayList<ClientHandler>();

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chatArea.setText("");
            }
        });
        add(clearButton, BorderLayout.SOUTH);
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            chatArea.append("Server started\n");

            while (true) {
                Socket socket = serverSocket.accept();
                chatArea.append("Client connected: " + socket + "\n");

                ClientHandler client = new ClientHandler(socket);
                clients.add(client);

                Thread t = new Thread(client);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Get the IP address and port of the client
                String ipAddress = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();

                // Get the name of the client
                name = in.readLine();

                // Log the IP address, port, and name of the client
                chatArea.append("Client connected: " + name + " (" + ipAddress + ":" + port + ")\n");

                File file = new File("users.txt");
                FileWriter writer = new FileWriter(file, true);
                writer.write("Client connected: " + name + " (" + ipAddress + ":" + port + ")\n");
                writer.close();


                // Send a welcome message to the client
                out.println("Welcome to the chat, " + name + "!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message =in.readLine()) != null) {
                    // Log the message received from the client
                    chatArea.append(name + ": " + message + "\n");
                                    // Broadcast the message to all clients
                broadcast(name + ": " + message);

                // Write the message to a file
                File file = new File("chat_log.txt");
                FileWriter writer = new FileWriter(file, true);
                writer.write(name + ": " + message + "\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Remove the client from the list of clients
                clients.remove(this);

                // Close the input and output streams and the socket
                in.close();
                out.close();
                socket.close();

                // Log that the client has disconnected
                chatArea.append("Client disconnected: " + name + "\n");

                // Broadcast that the client has disconnected
                broadcast(name + " has disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
}