package bingo;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author gregory
 */
public class Jugador extends Frame implements ActionListener {
    static Socket sfd = null;
    static DataInputStream EntradaSocket;
    static DataOutputStream SalidaSocket;
    static TextField salida;
    static TextArea entrada;
    String texto;

    public Jugador() {
        setTitle("BINGO GAME");
        setSize(550, 400);
        salida = new TextField(30);
        salida.addActionListener(this);

        entrada = new TextArea();
        entrada.setEditable(false);

        add("South", salida);
        add("Center", entrada);
        setVisible(true);
    }

    public static void main(String[] args) {
        Jugador cliente = new Jugador();
        try {
            sfd = new Socket("localhost", 8000);
            EntradaSocket = new DataInputStream(new BufferedInputStream(sfd.getInputStream()));
            SalidaSocket = new DataOutputStream(new BufferedOutputStream(sfd.getOutputStream()));
        } catch (UnknownHostException uhe) {
            System.out.println("No se puede acceder al servidor.");
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("Comunicacion rechazada.");
            System.exit(1);
        }
        while (true) {
            try {
                String linea = EntradaSocket.readUTF();
                entrada.append(linea + "\n");
            } catch (IOException ioe) {
                System.exit(1);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        texto = salida.getText();
        salida.setText("");
        try {
            SalidaSocket.writeUTF(texto);
            SalidaSocket.flush();
        } catch (IOException ioe) {
            System.out.println("Error mensaje ");
            System.out.println("Error: " + ioe);
        }
    }

    public boolean handleEvent(Event e) {
        if ((e.target == this) && (e.id == Event.WINDOW_DESTROY)) {
            if (sfd != null) {
                try {
                    sfd.close();
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe);
                }
                this.dispose();
            }
        }
        return true;
    }
    
}
