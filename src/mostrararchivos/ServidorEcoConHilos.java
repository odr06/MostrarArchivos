package mostrararchivos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorEcoConHilos {
    public static void main(String[] args) {
        try {
            int i = 1;
            ServerSocket s = new ServerSocket(1234);
            
            while (true) {
                Socket entrante = s.accept( );
                System.out.println("Generando hilo " + i + ".");
                Runnable r = new ManejadorHilos(entrante, i);
                Thread t = new Thread(r);
                t.start( );
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}
