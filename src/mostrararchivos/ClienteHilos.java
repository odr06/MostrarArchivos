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
            
            int opcionBuscando = 1;
            do {
                System.out.println(entrada.nextLine( ));
                
                String palabra = teclado.nextLine( );
                while (palabra.isEmpty( )) palabra = teclado.nextLine( );
                
                salida.println(palabra);
                
                int opcion = 1;
                do {
                    String tamano = entrada.nextLine( );                        
                    int tam = Integer.parseInt(tamano);
                    if (tam == 0) {
                        String mensajeListaVacia = entrada.nextLine( );
                        System.out.println(mensajeListaVacia);
                        break;
                    }
                    
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
                
                String seguirBuscando = entrada.nextLine( );
                String siSeguirBuscando = entrada.nextLine( );
                String noSeguirBuscando = entrada.nextLine( );
                System.out.println(seguirBuscando + "\n" + siSeguirBuscando + "\n" + noSeguirBuscando);
                opcionBuscando = teclado.nextInt( );
                salida.println(opcionBuscando);
            } while (opcionBuscando == 1);
            
            entrada.close( );
            salida.close( );
            conexion.close( );
            
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}
