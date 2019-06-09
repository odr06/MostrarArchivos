package mostrararchivos;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteHilos {
    public static void main(String[] args) {
        try {
            Socket conexion = new Socket("localhost", 1234);
            
            PrintWriter salida = new PrintWriter(conexion.getOutputStream( ), true);
            Scanner entrada = new Scanner(conexion.getInputStream( ));
            Scanner teclado = new Scanner(System.in);
            
            System.out.println(entrada.nextLine( ));
            boolean sigue = true;
            while (sigue) {
                String palabra = teclado.nextLine( );
                salida.println(palabra);
                System.out.println(entrada.nextLine( ));
                
                if (palabra.compareTo("ADIOS") == 0) {
                    sigue = false;
                }
            }
            
            entrada.close( );
            salida.close( );
            conexion.close( );
            
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}
