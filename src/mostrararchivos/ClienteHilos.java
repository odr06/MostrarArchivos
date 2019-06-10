package mostrararchivos;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteHilos {
    
    public static void main(String[] args) throws IOException {
        try {
            Socket conexion = new Socket("localhost", 1234);
            
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
                    
                    String nombreArchivo = entrada.nextLine( );
                    recibeArchivo(nombreArchivo, conexion);
                    if (new File(nombreArchivo).isFile( )) {
                        Desktop.getDesktop( ).open(new File(nombreArchivo));
                    } else {
                        System.out.println("No Existe!");
                    }
                    String seguirAbriendo = null, siSeguirAbriendo = null, noSeguirAbriendo = null;
                    seguirAbriendo = entrada.nextLine( );
                    siSeguirAbriendo = entrada.nextLine( ); //while (siSeguirAbriendo.isEmpty( )) siSeguirAbriendo = entrada.nextLine( );
                    noSeguirAbriendo = entrada.nextLine( ); //while (noSeguirAbriendo.isEmpty( )) noSeguirAbriendo = entrada.nextLine( );
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
    
    public static void recibeArchivo(String nombreArchivo, Socket socket) throws FileNotFoundException, IOException {
        File archivo = new File(nombreArchivo);
        FileOutputStream fos = new FileOutputStream(archivo);
        DataInputStream is = new DataInputStream(socket.getInputStream());
        try {
            int bytesLength = is.readInt();
            byte[] buffer = new byte[bytesLength];
            is.read(buffer, 0, bytesLength);
            fos.write(buffer);
            fos.close( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}

