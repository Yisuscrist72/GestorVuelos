package com.mycompany.aeropuerto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorVuelos {
    
    // APARTADO 1: Obtener aeropuertos de un país
    public static List<String> obtenerAeropuertos(String nombreFichero, String pais) {
        // Lista para guardar los aeropuertos que cumplan la condición
        List<String> aeropuertos = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(nombreFichero))) {
            String linea;
            
            // Leer el fichero línea por línea
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                
                // Saltar líneas vacías
                if (linea.isEmpty()) {
                    continue;
                }
                
                // Separar por comas: código, país, latitud, longitud
                String[] campos = linea.split(",");
                
                if (campos.length >= 4) {
                    String paisAeropuerto = campos[1].trim();
                    
                    // Añadir si el país coincide o si es "cualquiera"
                    if (pais.equalsIgnoreCase("cualquiera") || paisAeropuerto.equalsIgnoreCase(pais)) {
                        aeropuertos.add(linea);
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al leer el fichero: " + e.getMessage());
        }
        
        return aeropuertos;
    }
    
    // APARTADO 2: Obtener rutas entre aeropuertos de la lista
    public static List<String> obtenerRutas(String nombreFichero, List<String> aeropuertos) {
        // Lista para guardar las rutas válidas
        List<String> rutas = new ArrayList<>();
        
        // Extraer solo los códigos de aeropuertos (primera columna)
        List<String> codigosAeropuertos = new ArrayList<>();
        for (String aeropuerto : aeropuertos) {
            String[] campos = aeropuerto.split(",");
            if (campos.length > 0) {
                codigosAeropuertos.add(campos[0].trim());
            }
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(nombreFichero))) {
            String linea;
            
            // Leer el fichero de precios línea por línea
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                
                if (linea.isEmpty()) {
                    continue;
                }
                
                // Separar: origen, destino, precio, duración, aerolínea
                String[] campos = linea.split(",");
                
                if (campos.length >= 5) {
                    String origen = campos[0].trim();
                    String destino = campos[1].trim();
                    
                    // Solo añadir si origen Y destino están en nuestra lista
                    if (codigosAeropuertos.contains(origen) && codigosAeropuertos.contains(destino)) {
                        rutas.add(linea);
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al leer el fichero: " + e.getMessage());
        }
        
        return rutas;
    }
    
    // APARTADO 3: Filtrar rutas por origen y destino
    public static List<String> filtrarRutas(List<String> rutas, String origen, String destino) {
        // Lista para las rutas filtradas
        List<String> rutasFiltradas = new ArrayList<>();
        
        // Recorrer todas las rutas
        for (String ruta : rutas) {
            String[] campos = ruta.split(",");
            
            if (campos.length >= 5) {
                String origenRuta = campos[0].trim();
                String destinoRuta = campos[1].trim();
                
                // Comprobar si coincide el origen
                boolean coincideOrigen = origenRuta.equalsIgnoreCase(origen);
                // Comprobar si coincide el destino (o si es "cualquiera")
                boolean coincideDestino = destino.equalsIgnoreCase("cualquiera") || destinoRuta.equalsIgnoreCase(destino);
                
                // Añadir si coinciden ambos
                if (coincideOrigen && coincideDestino) {
                    rutasFiltradas.add(ruta);
                }
            }
        }
        
        return rutasFiltradas;
    }
    
    // APARTADO 4: Guardar rutas en fichero
    public static void guardarRutas(String nombreFichero, List<String> rutas, int posicion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero))) {
            
            // Si posición == número de rutas, guardar todas
            if (posicion == rutas.size()) {
                for (String ruta : rutas) {
                    escribirRuta(bw, ruta);
                }
            } 
            // Si posición válida, guardar solo esa ruta
            else if (posicion >= 0 && posicion < rutas.size()) {
                escribirRuta(bw, rutas.get(posicion));
            } 
            else {
                System.err.println("Posición inválida: " + posicion);
            }
            
            System.out.println("Rutas guardadas en: " + nombreFichero);
            
        } catch (IOException e) {
            System.err.println("Error al escribir el fichero: " + e.getMessage());
        }
    }
    
    // Método auxiliar para escribir una ruta con el formato requerido
    private static void escribirRuta(BufferedWriter bw, String ruta) throws IOException {
        // Separar los campos de la ruta
        String[] campos = ruta.split(",");
        
        if (campos.length >= 5) {
            String origen = campos[0].trim();
            String destino = campos[1].trim();
            String precio = campos[2].trim();
            String duracion = campos[3].trim();
            String lineaAerea = campos[4].trim();
            
            // Escribir en el formato especificado
            bw.write("---\n");
            bw.write("Flight: " + origen + " to " + destino + "\n");
            bw.write("Carrier: " + lineaAerea + "\n");
            bw.write("Duration: " + duracion + " minutes\n");
            bw.write("Total Cost: " + precio + " euros\n");
            bw.write("---\n");
        }
    }
    
    // Programa principal para probar
    public static void main(String[] args) {
        System.out.println("=== APARTADO 1 ===");
        List<String> aeropuertosEspana = obtenerAeropuertos("localizacion.txt", "Spain");
        System.out.println("Total aeropuertos españoles: " + aeropuertosEspana.size());
        System.out.println("Primeros 5:");
        for (int i = 0; i < Math.min(5, aeropuertosEspana.size()); i++) {
            System.out.println(aeropuertosEspana.get(i));
        }
        
        System.out.println("\n=== APARTADO 2 ===");
        List<String> rutasEspana = obtenerRutas("precios.txt", aeropuertosEspana);
        System.out.println("Total rutas españolas: " + rutasEspana.size());
        System.out.println("Primeras 5:");
        for (int i = 0; i < Math.min(5, rutasEspana.size()); i++) {
            System.out.println(rutasEspana.get(i));
        }
        
        System.out.println("\n=== APARTADO 3 ===");
        List<String> rutasAGP = filtrarRutas(rutasEspana, "AGP", "cualquiera");
        System.out.println("Rutas desde AGP: " + rutasAGP.size());
        System.out.println("Primeras 5:");
        for (int i = 0; i < Math.min(5, rutasAGP.size()); i++) {
            System.out.println(rutasAGP.get(i));
        }
        
        List<String> rutasAGPtoMAD = filtrarRutas(rutasEspana, "AGP", "MAD");
        System.out.println("\nRutas de AGP a MAD:");
        for (String ruta : rutasAGPtoMAD) {
            System.out.println(ruta);
        }
        
        System.out.println("\n=== APARTADO 4 ===");
        if (!rutasAGPtoMAD.isEmpty()) {
            guardarRutas("ruta_0.txt", rutasAGPtoMAD, 0);
            guardarRutas("todas_rutas.txt", rutasAGPtoMAD, rutasAGPtoMAD.size());
        }
    }
}