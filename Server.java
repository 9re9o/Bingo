
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;

public class Server extends Thread {

    public static Vector usuarios = new Vector();

    public static void main(String args[]) throws InterruptedException {

        ServerSocket servSocket = null;
        DataOutputStream FlujoEscritura;

        int clientes = 0;
        try {
            servSocket = new ServerSocket(8000);
            System.out.println("Servidor iniciado, esperando jugadores...");

            //retorna el # de puerto por el que el server escucha
            System.out.println(" El servidor se inicializo en el puerto : " + servSocket.getLocalPort());
        } catch (IOException ioe) {
            System.out.println("Error al iniciar el servidor.");
            System.out.println("Comunicacion rechazada." + ioe);
            System.exit(1);
        }

        while (true) {
            try {
                //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                Socket clienteSocket = servSocket.accept();

                FlujoEscritura = new DataOutputStream(new BufferedOutputStream(clienteSocket.getOutputStream()));
                FlujoEscritura.writeUTF("Diga su usuario");

                //cada vez que un cliente se conecta aumenta
                clientes++;
                System.out.println("\n Cliente # " + clientes + " conexion aceptada de: " + clienteSocket.getInetAddress());

                Flujo flujo = new Flujo(clienteSocket);
                Thread t = new Thread((Runnable) flujo);
                t.start();

                enviarNumeros(clienteSocket); //generamos los cartones y los enviamos 

                if (clientes == 2) {
                    flujo.broadcast("Rellene su bingo");
                    boolean detener = true;
                    
                    while (detener) {
                        
                        
                        flujo.mandarNumerosTodos();
                        
                        sleep(2000);
                        String nomLinea = flujo.getLinea();
                        
                        String bingo = "BINGO";
                        
                        if (nomLinea.contains(bingo) ) {
                            System.out.println("NOS FUIMOS");
                            detener = false;
                            break;
                        }
                        
                    }

                }

            } catch (IOException ioe) {
                System.out.println("Error al aceptar la conexion.");
                System.out.println("Error: " + ioe);
            }

        }

    }

    //genera los numeros aleatorios para los jugadores
    public static int[] genNums(int varTam) {
        int[] arrayNumeros = new int[varTam];

        ArrayList<Integer> listaNumeros = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < varTam; i++) {
            int num = random.nextInt(100);
            listaNumeros.add(num);
        }

        for (int i = 0; i < varTam; i++) {
            arrayNumeros[i] = listaNumeros.get(i);
        }

        return arrayNumeros;
    }

    public static void enviarNumeros(Socket socketCliente) {
        try {
            int[] nums = genNums(10); //genera solamente 10 numeros
            DataOutputStream out = new DataOutputStream(socketCliente.getOutputStream());

            out.writeUTF("Este es tu carton de juego \n");

            for (int num : nums) {
                out.writeUTF(Integer.toString(num)); //conversion
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
