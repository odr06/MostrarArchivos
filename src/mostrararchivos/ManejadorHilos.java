package mostrararchivos;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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

public class ManejadorHilos implements Runnable {
    
    private Socket entrante;
    private int contador;
    private int consulta = 0;
    
    public ManejadorHilos(Socket i, int c) {
        entrante = i;
        contador = c;
    }

    public void run( ) {
        try {
            try {
                InputStream secuenciaEntrada = entrante.getInputStream( );
                OutputStream secuenciaSalida = entrante.getOutputStream( );
                
                String hacerOtraBusqueda = "1";
                do {
                    Scanner in = new Scanner(secuenciaEntrada);
                    PrintWriter out = new PrintWriter(secuenciaSalida, true);
                    
                    out.println("Ingrese el nombre del archivo que contiene la(s) consulta(s))");
                    
                    String fileName = in.nextLine( ) + "_c" + Integer.toString(contador) + "_q" + Integer.toString(consulta++);
                    
                    recibeArchivo(fileName, entrante);
                    
                    sendQuery(fileName);
                    
                    String deseaAbrirOtro = "1";
                    do {
                        out.println("Ingrese el nombre del archivo que desee abrir");
                        String recFile = in.nextLine();
                        File file = new File(determinaRuta() + "\\" + recFile);
                        out.println(file.getName( ));
                        enviarArchivo(file, entrante);
                        out.println("Desea abrir otro archivo? (Solo valor numerico)");
                        out.println("1) SI");
                        out.println("2) NO");
                        deseaAbrirOtro = in.nextLine( );
                    } while (deseaAbrirOtro.equals("1"));
                    
                    out.println("Desea hacer otra busqueda? (Solo valor numerico)");
                    out.println("1) SI");
                    out.println("2) NO");
                    hacerOtraBusqueda = in.nextLine( );
                } while (hacerOtraBusqueda.equals("1"));
                
            } finally {
                entrante.close( );
                System.out.println("Hilo " + contador + " finalizado.");
            }
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
    
    /* Metodo para determinar ruta */
    public static String determinaRuta( ) {
        String ruta = System.getProperty("user.dir");
        ruta += "\\src\\mostrararchivos\\Archivos";
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
        String archivo = new File(ruta + "\\" + nombre).getName( );
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
            FileReader archivo = new FileReader(ruta + "\\" + nombre);
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
            File archivo = new File(ruta + "\\" + nombre);
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
            PDDocument documento = PDDocument.load(new File(ruta + "\\" + nombre));
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
    public static void mostrarLista(ArrayList<String> lista, PrintWriter out) {
        for (int i = 0; i < lista.size( ); ++i) {
            out.println(i + ") " + lista.get(i));
        }
        out.println("Ingrese el indice del documento que desea abrir:");
    }
    

    /* Metodo para abrir archivo seleccionado */
    public static void abrirArchivo(ArrayList<String> listaFiltrada, String opcion, String ruta) {
        int op = Integer.parseInt(opcion);
        if (op < 0 || op > listaFiltrada.size( )) {
            System.out.println("Opcion no valida");
        }
        
        try {
            File archivo = new File(ruta + "\\" + listaFiltrada.get(op));
            Desktop.getDesktop( ).open(archivo);
        } catch (IOException ex) {
            ex.printStackTrace( );
        }
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
        File archivo = new File("Servidor//" + nombreArchivo);
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
    
    public ArrayList<String> searchQuery(String query) {
        System.out.println("C" + this.contador + "Palabra(s) a buscar: " + query);
        String[] palabras = query.split(" ");
        String ruta = determinaRuta( );
        String[] lista = listarArchivos(ruta);
        return filtrarArchivos(palabras, lista, ruta);
    }

    public void sendQuery(String fileName) {
        Scanner fis;
        PrintWriter fos;

        try{
            String file_res = fileName + "_results.txt";
            File archivo = new File("Servidor//" + fileName);
            File archivow = new File("Servidor//" + file_res);
            fis = new Scanner(archivo);
            fos = new PrintWriter(new FileWriter(archivow));
            int nline = 1;
            while(fis.hasNextLine()){
                String q = fis.nextLine();
                fos.println("Consulta " + nline++ + ": " + q);
                ArrayList<String> results = searchQuery(q);
                if (!results.isEmpty( )){
                    for(String r : results){
                        fos.println("\t" + r);
                    }
                }else{
                    fos.println("\tNo se encontraron archivos con la palabra buscada.");
                }
            }
            fis.close();
            fos.close();     
            enviarArchivo(new File("Servidor//" + file_res), this.entrante);
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
    
}

