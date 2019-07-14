package mostrararchivos;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
                
                String fileName = teclado.nextLine( );
                while (fileName.isEmpty( )) fileName = teclado.nextLine( );
                
                salida.println(fileName);
                
                File sendFile = new File("Cliente//" + fileName);
                enviarArchivo(sendFile, conexion);
                
                String fileResults = "result_" + fileName;
                recibeArchivo(fileResults, conexion);

                String opcion = "1";
                do {
                    imprimeResults(fileResults);

                    System.out.println(entrada.nextLine( ));
                    salida.println(teclado.nextLine( ));

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
    
    public static void enviarArchivo(File archivo, Socket socket) {
        FileInputStream fis;
        DataOutputStream out;
        try {
            fis = new FileInputStream(archivo);
            out = new DataOutputStream(socket.getOutputStream());
            int bytesLenght = (int) archivo.length();
            byte[] buffer = new byte[bytesLenght];
            fis.read(buffer);
            out.writeInt(bytesLenght);
            out.write(buffer, 0, bytesLenght);
            fis.close( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
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

    public static void imprimeResults(String fileName){
        Scanner fis;
        try{
            File archivo = new File(fileName);
            fis = new Scanner(archivo);
            while(fis.hasNextLine()){
                System.out.println(fis.nextLine());
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
}

