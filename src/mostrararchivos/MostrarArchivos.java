package mostrararchivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MostrarArchivos {

    public static void main(String[] args) {        
        Scanner sc = new Scanner(System.in);
        System.out.println("Que palabra desea buscar?");
        String palabra = sc.next( );
        
        String ruta = determinaRuta( );
        String[] lista = listarArchivos(ruta);
        ArrayList<String> listaFiltrada = filtrarArchivos(palabra, lista, ruta);
        if (!listaFiltrada.isEmpty( )) {
            mostrarLista(listaFiltrada);
        } else {
            System.out.println("No se encontraron archivos con la palabra buscada.");
        }
    }
    
    public static String determinaRuta( ) {
        String ruta = System.getProperty("user.dir");
        ruta += "/src/mostrararchivos/Archivos";
        return ruta;
    }
    
    public static String[] listarArchivos(String ruta) {
        File carpeta = new File(ruta);
        String[] lista = carpeta.list( );
        
        return lista;
    }
    
    public static ArrayList<String> filtrarArchivos(String palabraBuscada, String[] lista, String ruta) {
        ArrayList<String> listaFiltrada = new ArrayList<>(lista.length);
        if (lista.length != 0) {
            for (String archivo : lista) {
                if (analizarArchivo(archivo, ruta, palabraBuscada) == true) {
                    listaFiltrada.add(archivo);
                }
            }
        }
        return listaFiltrada;
    }
    
    public static boolean analizarArchivo(String nombre, String ruta, String palabraBuscada) {
        try {
            FileReader archivo = new FileReader(ruta + "/" + nombre);
            BufferedReader br = new BufferedReader(archivo);
            
            String linea;
            while ((linea = br.readLine( )) != null) {
                String palabras[] = linea.split("[, .;:¿?!¡/]+");
                for (String palabra : palabras) {
                    if (palabra.equals(palabraBuscada)) {
                        return true;
                    }
                }
            }
            System.out.println("");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
        
        return false;
    }
    
    public static void mostrarLista(ArrayList<String> lista) {
        if (!lista.isEmpty( )) {
            for (int i = 0; i < lista.size( ); ++i) {
                System.out.println("Archivo: " + lista.get(i));
            }
        }
    }
    
}
