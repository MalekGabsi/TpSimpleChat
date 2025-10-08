package simplechat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleChatClientWithNotifications {
    private JTextArea incoming;
    private JTextField outgoing;
    private PrintWriter writer;
    private SocketChannel socketChannel;

    public void go() {
        setUpNetworking();
        JScrollPane scroller = createScrollableTextArea();
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel mainPanel = new JPanel();
        mainPanel.add(scroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        JFrame frame = new JFrame("Simple Chat Client");
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(400, 350);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Création d'un thread pour lire les messages entrants
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(new IncomingReader());
    }

    private JScrollPane createScrollableTextArea() {
        incoming = new JTextArea(15, 30);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane scroller = new JScrollPane(incoming);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroller;
    }

    private void setUpNetworking() {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 5000);
            socketChannel = SocketChannel.open(serverAddress);
            writer = new PrintWriter(Channels.newWriter(socketChannel, UTF_8), true);
            System.out.println("Networking established.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        writer.println(outgoing.getText());
        writer.flush();
        outgoing.setText("");
        outgoing.requestFocus();
    }

    // Thread de lecture des messages entrants
    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(512);
            try {
                while (true) {
                    buffer.clear();
                    int bytesRead = socketChannel.read(buffer);
                    if (bytesRead == -1) break; // serveur fermé
                    buffer.flip();
                    String message = UTF_8.decode(buffer).toString();
                    // Mise à jour de l'UI sur le thread Swing
                    SwingUtilities.invokeLater(() -> incoming.append(message + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new SimpleChatClientWithNotifications().go();
    }
}
