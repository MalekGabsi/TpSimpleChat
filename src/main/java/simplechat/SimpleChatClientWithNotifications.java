package simplechat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleChatClientWithNotifications {
    private JTextArea incoming;
    private JTextField outgoing;
    private PrintWriter writer;

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

        // TODO : Créer un ExecutorService pour gérer le thread de lecture des messages
        // Choisir parmis les implémentations d'ExecutorService, celle qui vous semble
        // la plus adaptée : newFixedThreadPool, newSingleThreadExecutor,
        // newCachedThreadPool...

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
            // serverAddress represente l'adresse complete de la machine serveur
            InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 5000);
            // On ouvre la connexion
            SocketChannel socketChannel = SocketChannel.open(serverAddress);
            // On crée un "Writer" pour envoyer des donnees a travers le SocketChannel
            writer = new PrintWriter(Channels.newWriter(socketChannel, UTF_8));
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

    public static void main(String[] args) {
        new SimpleChatClientWithNotifications().go();
    }
}