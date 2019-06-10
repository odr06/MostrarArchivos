package mostrararchivos;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteHilos {
    public static void main(String[] args) {
        try {
            Socket conexion = new Socket("localhost", 1234);
            InputStream is;
            FileOutputStream fos;
            BufferedOutputStream bos;
            int bufferSize;
            
            PrintWriter salida = new PrintWriter(conexion.getOutputStream( ), true);
            Scanner entrada = new Scanner(conexion.getInputStream( ));
            Scanner teclado = new Scanner(System.in);
            
            String opcionBuscando = "1";
            do {
                System.out.println(entrada.nextLine( ));
                
                String palabra = teclado.nextLine( );
                while (palabra.isEmpty( )) palabra = teclado.nextLine( );
                
                salida.println(palabra);
                
                String opcion = "1";
                String tamano = entrada.nextLine( );
                do {
                    int tam = toInteger(tamano);
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

                    String opcionAbrir = teclado.nextLine( );
                    while (opcionAbrir.isEmpty( )) opcionAbrir = teclado.nextLine( );
                    salida.println(opcionAbrir);
                    
                    
                    
                    String seguirAbriendo = entrada.nextLine( );
                    String siSeguirAbriendo = entrada.nextLine( );
                    String noSeguirAbriendo = entrada.nextLine( );
                    System.out.println(seguirAbriendo + "\n" + siSeguirAbriendo + "\n" + noSeguirAbriendo);
                    
                    opcion = teclado.nextLine( );
                    while (opcion.isEmpty( )) opcion = teclado.nextLine( );
                    salida.println(opcion);
                } while (opcion.equals("1"));
                
                String seguirBuscando = entrada.nextLine( );
                String siSeguirBuscando = entrada.nextLine( );
                String noSeguirBuscando = entrada.nextLine( );
                System.out.println(seguirBuscando + "\n" + siSeguirBuscando + "\n" + noSeguirBuscando);
                
                opcionBuscando = teclado.nextLine( );
                while (opcionBuscando.isEmpty( )) opcionBuscando = teclado.nextLine( );
                salida.println(opcionBuscando);
            } while (opcionBuscando.equals("1"));
            
            entrada.close( );
            salida.close( );
            conexion.close( );
            
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
    
    public static int toInteger(String num) {
        int res = 0;
        try {
            res = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            res = 0;
        }
        return res;
    }
    
    
}
