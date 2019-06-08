package mostrararchivos;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class MostrarArchivos {

    public static void main(String[] args) {  
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese la(s) palabra(s) desea buscar, separadas por un espacio?");
        String linea = sc.nextLine( );
        String[] palabras = linea.split(" ");
        
        String ruta = determinaRuta( );
        String[] lista = listarArchivos(ruta);
        ArrayList<String> listaFiltrada = filtrarArchivos(palabras, lista, ruta);
        if (!listaFiltrada.isEmpty( )) {
            mostrarLista(listaFiltrada);
            int opcion = sc.nextInt( );
            abrirArchivo(listaFiltrada, opcion, ruta);
        } else {
            System.out.println("No se encontraron archivos con la palabra buscada.");
        }
    }
    
    /* Metodo para determinar ruta */
    public static String determinaRuta( ) {
        String ruta = System.getProperty("user.dir");
        ruta += "/src/mostrararchivos/Archivos";
        return ruta;
    }
    
    /* Metodo para listar archivos */
    public static String[] listarArchivos(String ruta) {
        File carpeta = new File(ruta);
        String[] lista = carpeta.list( );
        Arrays.sort(lista);
        return lista;
    }
    
    /* Metodo para filtrar archivos */
    public static ArrayList<String> filtrarArchivos(String[] palabraBuscada, String[] lista, String ruta) {
        ArrayList<String> listaFiltrada = new ArrayList<>(lista.length);
        if (lista.length != 0) {
            for (String archivo : lista) {
                if (analizaArchivos(archivo, ruta, palabraBuscada) == true) {
                    listaFiltrada.add(archivo);
                }
            }
        }
        return listaFiltrada;
    }
    
    /* Metodo para determinar tipo de documento */
    public static int determinaTipo(String nombre, String ruta) {
        String archivo = new File(ruta + "/" + nombre).getName( );
        int punto = archivo.lastIndexOf('.');
        String ext = (punto == -1 ? "" : archivo.substring(punto + 1));
        
        switch(ext) {
        case "pdf":
            return 1;
        case "docx":
            return 2;
        default:
            return 0;
        }
    }
    
    /* Metodo para analizar archivos */
    public static boolean analizaArchivos(String nombre, String ruta, String[] palabraBuscada) {
        int tipo = determinaTipo(nombre, ruta);
        boolean flag = false;
        
        switch(tipo) {
        case 0:
            flag = analizaTexto(nombre, ruta, palabraBuscada);
            break;
        case 1:
            flag = analizaPDF(nombre, ruta, palabraBuscada);
            break;
        case 2:
            flag = analizaDocx(nombre, ruta, palabraBuscada);
            break;
        }
        
        return flag;
    }
    
    /* Metodo para llenar el Map "seen" */
    public static Map<String, Boolean> llenaSeen(String[] palabras) {
        Map<String, Boolean> res = new HashMap<>( );
        for(String palabra : palabras) {
            res.put(palabra, false);
        }
        return res;
    }
    
    /* Metodo para analizar texto */
    public static boolean analizaTexto(String nombre, String ruta, String[] palabraBuscada) {
        int contPalabras = 0;
        try {
            FileReader archivo = new FileReader(ruta + "/" + nombre);
            BufferedReader br = new BufferedReader(archivo);
            Map<String, Boolean> seen = llenaSeen(palabraBuscada);
            
            String linea;
            while ((linea = br.readLine( )) != null) {
                String palabras[] = linea.split("[^a-zA-Z0-9]+");
                for (String palabra : palabras) {
                    palabra = palabra.toLowerCase( );
                    if (seen.get(palabra) != null && seen.get(palabra) == false) {
                        seen.put(palabra, true);
                        contPalabras += 1;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
        
        return (contPalabras == palabraBuscada.length);
    }
    
    /* Metodo para analizar docx */
    public static boolean analizaDocx(String nombre, String ruta, String[] palabraBuscada) {
        int contPalabras = 0;
        try {
            File archivo = new File(ruta + "/" + nombre);
            FileInputStream fis = new FileInputStream(archivo.getAbsolutePath( ));
            XWPFDocument documento = new XWPFDocument(fis);
            Map<String, Boolean> seen = llenaSeen(palabraBuscada);
            List<XWPFParagraph> parrafos = documento.getParagraphs( );
            
            for (XWPFParagraph parrafo : parrafos) {
                String[] palabras = parrafo.getText( ).split("[^a-zA-Z0-9]+");
                for (String palabra : palabras) {
                    palabra = palabra.toLowerCase( );
                    if (seen.get(palabra) != null && seen.get(palabra) == false) {
                        seen.put(palabra, true);
                        contPalabras += 1;
                    }
                }
            }
            
            fis.close( );
        } catch (Exception ex) {
            ex.printStackTrace( );
        }
        
        return (contPalabras == palabraBuscada.length);
    }
    
    /* Metodo para analizar pdf */
    public static boolean analizaPDF(String nombre, String ruta, String[] palabraBuscada) {
        int contPalabras = 0;
        try {
            PDDocument documento = PDDocument.load(new File(ruta + "/" + nombre));
            PDFTextStripper stripper = new PDFTextStripper( );
            String texto = stripper.getText(documento);
            String[] palabras = texto.split("[^a-zA-Z0-9]+");
            Map<String, Boolean> seen = llenaSeen(palabraBuscada);
            
            for (String palabra : palabras) {
                palabra = palabra.toLowerCase( );
                if (seen.get(palabra) != null && seen.get(palabra) == false) {
                    seen.put(palabra, true);
                    contPalabras += 1;
                }
            }
            
            documento.close( );
        } catch (IOException ex) {
            ex.printStackTrace( );
        }
        
        return (contPalabras == palabraBuscada.length);
    }
    
    /* Metodo para mostrar lista filtrada */
    public static void mostrarLista(ArrayList<String> lista) {
        if (!lista.isEmpty( )) {
            for (int i = 0; i < lista.size( ); ++i) {
                System.out.println(i + ") " + lista.get(i));
            }
        }
        System.out.println("\nIngrese el índice del documento que desea abrir:");
    }

    /* Metodo para abrir archivo seleccionado */
    public static void abrirArchivo(ArrayList<String> listaFiltrada, int opcion, String ruta) {
        if (opcion < 0 || opcion > listaFiltrada.size( )) {
            System.out.println("Opcion no valida");
        }
        
        try {
            File archivo = new File(ruta + "/" + listaFiltrada.get(opcion));
            Desktop.getDesktop( ).open(archivo);
        } catch (IOException ex) {
            ex.printStackTrace( );
        }
    }
    
}
