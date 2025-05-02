package inversionColores;

import java.io.*;
import java.util.Scanner;

public class InversionColores {
    public static void invertirColores(String fileName) throws IOException{
        InputStream inputStream = InversionColores.class.getResourceAsStream(fileName);

        if (inputStream == null) {
            System.err.println("No se encontró el archivo .pgm");
            return;
        }

        Scanner scan = new Scanner(inputStream);
        String formatPGM = scan.nextLine().trim(); // P2 o P5
        int headerLinesToSkip = 1;

        // Saltar comentarios 
        while (scan.hasNext("#")) {
            scan.nextLine();
            headerLinesToSkip++;
        }

        int picWidth = scan.nextInt();
        int picHeight = scan.nextInt();
        headerLinesToSkip++;

        int maxValue = scan.nextInt();
        headerLinesToSkip++;

        scan.close();
        inputStream.close();

        int[][] data2D = new int[picHeight][picWidth];

        if (formatPGM.equals("P2")) {
            // Leer como texto
            inputStream = InversionColores.class.getResourceAsStream(fileName);
            scan = new Scanner(inputStream);

            // Saltar encabezado
            for (int i = 0; i < headerLinesToSkip; i++) {
                scan.nextLine();
            }

            for (int row = 0; row < picHeight; row++) {
                for (int col = 0; col < picWidth; col++) {
                    data2D[row][col] = scan.nextInt();
                }
            }

            scan.close();
        } else if (formatPGM.equals("P5")) {
            // Leer como binario
            inputStream = InversionColores.class.getResourceAsStream(fileName);
            DataInputStream dis = new DataInputStream(inputStream);

            // Saltar encabezado como binario
            int newlinesToSkip = headerLinesToSkip;
            while (newlinesToSkip > 0) {
                char c;
                do {
                    c = (char) dis.readUnsignedByte();
                } while (c != '\n');
                newlinesToSkip--;
            }

            for (int row = 0; row < picHeight; row++) {
                for (int col = 0; col < picWidth; col++) {
                    data2D[row][col] = dis.readUnsignedByte();
                }
            }

            dis.close();
        } else {
            System.err.println("Formato PGM no soportado: " + formatPGM);
            return;
        }

        // Invertir colores
        for (int row = 0; row < picHeight; row++) {
            for (int col = 0; col < picWidth; col++) {
                data2D[row][col] = maxValue - data2D[row][col];
            }
        }

        // Guardar imagen invertida
        String outputFilePath = fileName+"_invertida.pgm";
        File outputFile = new File(outputFilePath);
        System.out.println("Guardando en: " + outputFile.getAbsolutePath());

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            String header = formatPGM + "\n" + picWidth + " " + picHeight + "\n" + maxValue + "\n";
            bos.write(header.getBytes());

            if (formatPGM.equals("P2")) {
                // Guardar como texto
                for (int row = 0; row < picHeight; row++) {
                    for (int col = 0; col < picWidth; col++) {
                        bos.write((data2D[row][col] + " ").getBytes());
                    }
                    bos.write("\n".getBytes());
                }
            } else {
                // Guardar como binario
                for (int row = 0; row < picHeight; row++) {
                    for (int col = 0; col < picWidth; col++) {
                        bos.write((byte) data2D[row][col]);
                    }
                }
            }

            System.out.println("Imagen invertida guardada como: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
	}	
	
    public static void main(String[] args){
    	try {
    		invertirColores("feep.ascii.pgm");
    	}catch(IOException e)
    	{
    		System.out.println(e.getMessage());
    	}       	
    }
}
