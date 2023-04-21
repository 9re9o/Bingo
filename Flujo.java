
import java.net.*;
import java.io.*;
import java.util.*;

public class Flujo extends Thread {

    Socket nsfd;
    String usu;
    DataInputStream FlujoLectura;
    DataOutputStream FlujoEscritura;
    private String linea = "";

    public Flujo(Socket sfd) {
        nsfd = sfd;
        try {
            FlujoLectura = new DataInputStream(new BufferedInputStream(sfd.getInputStream()));
            FlujoEscritura = new DataOutputStream(new BufferedOutputStream(sfd.getOutputStream()));
            //FlujoEscritura.writeUTF("Diga su usuario");
            usu = FlujoLectura.readUTF();
            System.out.println(nsfd.getInetAddress() + " se registro como: " + usu);

        } catch (IOException ioe) {
            System.out.println("IOException(Flujo): " + ioe);
        }

    }

    @Override
    public void run() {
        broadcast(usu + "> se ha conectado");
        Server.usuarios.add((Object) this);
        while (true) {
            try {
                 linea = FlujoLectura.readUTF();

                if (!linea.equals("")) {
                    linea = usu + "> " + linea;
                    broadcast(linea);
                    
                    System.out.println("Linea de prueba desde flujo:" + linea);

                    
                }
            } catch (IOException ioe) {
                Server.usuarios.removeElement(this);
                broadcast(usu + "> se ha desconectado");
                break;
            }
        }
    }

    public String getLinea(){
        return linea;
    }
    
    public void broadcast(String mensaje) {
        synchronized (Server.usuarios) {
            Enumeration e = Server.usuarios.elements();
            while (e.hasMoreElements()) {
                Flujo f = (Flujo) e.nextElement();
                try {
                    synchronized (f.FlujoEscritura) {
                        f.FlujoEscritura.writeUTF(mensaje);
                        f.FlujoEscritura.flush();
                    }
                } catch (IOException ioe) {
                    System.out.println("Error: " + ioe);
                }
            }
        }
    }

    public void mandarNumerosTodos() throws InterruptedException {
        
        broadcast(Integer.toString(genNums()));
        
    }

    public static int genNums() throws InterruptedException {
        while (true) {
            Random random = new Random();
            int num = random.nextInt(100);
            sleep(1000);
            return num;
        }

    }

}
