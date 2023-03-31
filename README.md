#Java Chat System using TCP Protocol

This Java Chat System using TCP Protocol allows users to communicate with each other in a chat room through TCP (Transmission Control Protocol) sockets.

#Features
1.Multiple clients can connect to the server
2.Clients can send messages to each other
3.Server broadcasts messages to all connected clients
4.Clients can disconnect from the server
5.Server can handle multiple clients concurrently using multithreading
6.Client details are logged in *users.txt* file and chats are logged into *chat_log.txt* file

#Requirements
1.Java Development Kit (JDK)
2.Eclipse IDE (optional)

#How to Use
1.Clone the repository to your local machine using the command:
**git clone https://github.com/your-username/java-chat-system.git**
2.Open the project in Eclipse IDE (or any other Java IDE of your choice).
3.Run the Server.java file to start the server.
4.Run the Client.java file to start a client.
5.Enter a username for the client and click the "Connect" button.
6.Repeat steps 4-5 for additional clients.
7.Type a message in the text field at the bottom of the chat window and click "Send" to send the message to all connected clients.
8.Closing window will disconnect client.
