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
            //while (sigue) {
                String palabra = teclado.nextLine( );
                salida.println(palabra);
                
                int opcion = 1;
                do {
                    String tamano = entrada.nextLine( );
                    int tam = Integer.parseInt(tamano);
                    for (int k = 0; k < tam; ++k) {
                        String lineaMenu = entrada.nextLine( );
                        System.out.println(lineaMenu);
                    }
                    
                    String mensajeOpcion = entrada.nextLine( );
                    System.out.println(mensajeOpcion);

                    int opcionAbrir = teclado.nextInt( );
                    salida.println(opcionAbrir);
                    
                    String seguirAbriendo = entrada.nextLine( );
                    String siSeguirAbriendo = entrada.nextLine( );
                    String noSeguirAbriendo = entrada.nextLine( );
                    System.out.println(seguirAbriendo + "\n" + siSeguirAbriendo + "\n" + noSeguirAbriendo);
                    opcion = teclado.nextInt( );
                    salida.println(opcion);
                } while (opcion == 1);
            //}
            
            entrada.close( );
            salida.close( );
            conexion.close( );
            
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}
