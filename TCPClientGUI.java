import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;


public class TCPClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField messageField;
    private JTextArea chatArea;
    private JLabel onlineUsersLabel;
    private PrintWriter out;
    private ArrayList<String> onlineUsers;

    public static void main(String[] args) {
        TCPClientGUI client = new TCPClientGUI();
        client.setVisible(true);
        client.run();
    }

    public TCPClientGUI() {
        setTitle("Anonymous Chat");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        messageField = new JTextField();
        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);

        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(topRightPanel, BorderLayout.NORTH);

        onlineUsersLabel = new JLabel("Online users: 0");
        topRightPanel.add(onlineUsersLabel);

        onlineUsers = new ArrayList<String>();
    }

    public void run() {
        try {
           String name = null;
    do {
        name = JOptionPane.showInputDialog("Enter your name:");
        if (name == null) {
            // User clicked cancel
            return;
        }
    } while (name.trim().isEmpty());
            Socket socket = new Socket("localhost", 8000);
            chatArea.append("Connected to server\n");
    
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
    
            out.println(name);
            onlineUsers.add(name);
            updateOnlineUsersLabel();
            chatArea.append("You have joined the chat as " + name + "\n");
    
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                if (message.equals("#serverdown")) {
                    chatArea.append("The server is currently down. Please try again later.\n");
                    break;
                }
                if (message.startsWith("#")) {
                    // Update the list of online users
                    onlineUsers.clear();
                    String[] parts = message.split(" ");
                    for (int i = 1; i < parts.length; i++) {
                        onlineUsers.add(parts[i]);
                    }
                    updateOnlineUsersLabel();
                } else {
                    chatArea.append(message + "\n");
                }
            }
    
            socket.close();
        } catch (IOException e) {
            chatArea.append("Failed to connect to server. The server may be down. Please try again later.\n");
        }
    }
    
    

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
        out.println(message);
        messageField.setText("");
        }
    }

    private void updateOnlineUsersLabel() {
        int numOnline = onlineUsers.size();
        String usersString = "Online users: " + numOnline + " - ";
        for (int i = 0; i < numOnline; i++) {
            usersString += onlineUsers.get(i);
            if (i != numOnline - 1) {
                usersString += ", ";
            }
        }
        onlineUsersLabel.setText(usersString);
    }

}