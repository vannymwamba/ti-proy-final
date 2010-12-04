/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

/**
 * Clase para el manejo de los archivos de entrada y salida con los formatos WAVE
 * y KL1. Entrega el archivo por bloques para estudiar la posibilidad de una compresión
 * para streaming.
 * @author emirhg
 */
public class FileManager {

    private File file;
    private String type;
    private int headerSize;
    private int dataSize;
    private long fileSize;
    private long currentPos;
    private byte[] bytes;
    private byte[] currentDataBlock;
    private int currentBlockSize;
    private int blockSize;
    private long polDegree;
    private long compresionFactor;
    private long scaleFactor;
    private boolean write;

    /**
     * Regresa el factor de compresión.
     * @return compresionFactor
     */
    public long getCompresionFactor() {
        return compresionFactor;
    }

    /**
     * Inicializa el factor de compresión.
     * @param compresionFactor
     */
    public void setCompresionFactor(long compresionFactor) {
        this.compresionFactor = compresionFactor;
    }

    /**
     * Regresa el grado del polinomio.
     * @return polDegree
     */
    public long getDegree() {
        return polDegree;
    }

    /**
     * Inicializar el grado del polinomio.
     * @param polDegree
     */
    public void setDegree(long polDegree) {
        this.polDegree = polDegree;
    }

    /**
     * Regresa el factor de escala.
     * @return scaleFactor
     */
    public long getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Inicializa el factor de escala.
     * @param scaleFactor Factor de escala
     */
    public void setScaleFactor(long scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Crea un nuevo objeto de tipo FileManager que manejará el archivo dado una dirección del archivo.
     * @param fName La dirección del archivo en sistema.
     * @param write Tipo de archivo que se utilizará, escritura o lectura.
     */
    public FileManager(String fName, boolean write) {
        file = new File(fName);
        this.write = write;
        if (fName.contains(".")) {
            type = fName.split("\\.")[1];
            try {
                if (!write) {
                    readFile();
                    int i = 0;
                    while (i < bytes.length - 7 && headerSize == 0) {
                        if (bytes[i] == (byte) 'd' && bytes[i + 1] == (byte) 'a' && bytes[i + 2] == (byte) 't' && bytes[i + 3] == (byte) 'a') {
                            headerSize = i + 8;
                        }
                        i++;
                    }
                    currentPos = headerSize;
                    i = 0;
                    if (type.equals("KL1") || type.equals("kl1")) {
                        String compfactor = "", polDe = "", scaleFact = "";
                        while (i < bytes.length && bytes[i] != 13) {
                            compfactor += (char) bytes[i];
                            i++;
                        }
                        i++;
                        compresionFactor = Long.decode(compfactor);

                        while (i < bytes.length && bytes[i] != 13) {
                            polDe += (char) bytes[i];
                            i++;
                        }
                        i++;
                        polDegree = Long.decode(polDe);
                        while (i < bytes.length - 3 && bytes[i] != (byte) 'R') {
                            scaleFact += (char) bytes[i];
                            i++;
                        }
                        i = 0;
                        scaleFactor = Long.decode(scaleFact);
                        blockSize = (int) ((polDegree + 1) * 3);
                    }
                } else {
                    if (file.exists()) {
                        file.delete();
                    }
                }

            } catch (IOException e) {
                System.err.println("Ocurrio un error al recuperar los datos del archivo");
                System.err.println(e.toString());
                bytes = null;
            }
        }

    }
    /*
     * Lee todo el archivo y lo almacena en un arreglo de bytes.
     */

    private void readFile() throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            // File is too large
        }
        // Create the byte array to hold the data
        bytes = new byte[(int) fileSize];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        // Close the input stream and return bytes
        is.close();
    }

    /**
     * Regresa todo el archivo en forma de un arreglo de bytes.
     * @return Un arreglo de bytes que corresponde a todo el archivo.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Regresa el encabezado del archivo para archivos tipo WAVE o KL1.
     * @return El encabezado del archivo.
     */
    public byte[] getHeader() {
        byte header[] = new byte[headerSize];
        System.arraycopy(bytes, 0, header, 0, headerSize);
        return header;
    }

    /**
     * Regresa el encabezado de un archivo tipo WAVE.
     * @return El encabezado WAVE.
     */
    public byte[] getWavHeader() {
        byte header[];
        int i = 0;
        while (i < bytes.length - 3 && bytes[i] != (byte) 'R') {
            i++;
        }
        header = new byte[headerSize - i];
        System.arraycopy(bytes, i, header, 0, headerSize - i);

        return header;
    }

    /**
     * Regresa el siguiente bloque del archivo que sigue al encabezado.
     * @return Un arreglo de bytes con el siguiente bloque de información, si no existe más datos regresa nulo.
     */
    public byte[] getNextDataBlock() {
        if (isNextDataBlock()) {
            //Increments the current position in the data
            if (currentPos < headerSize) {
                currentPos = headerSize;
            } else {
                currentPos += blockSize;
            }
            //Sets the available bytes size to read
            currentBlockSize = blockSize;
            if ((fileSize - currentPos) < blockSize) {
                currentBlockSize = (int) (fileSize - currentPos);
            }
            if (currentDataBlock == null) {
                currentDataBlock = new byte[blockSize];
            }
            System.arraycopy(bytes, (int) currentPos, currentDataBlock, 0, currentBlockSize);
            //Copies an array from the specified source array, beginning at the specified position, to the specified position of the destination array.
        } else {
            currentDataBlock = null;
        }
        return currentDataBlock;
    }

    /**
     * Regresa el siguiente bloque de coeficientes en el archivo.
     * @return El siguiente bloque de coeficientes del archivo, si no existe es nulo.
     */
    public Coeficiente[] getNextCoeficientesBlock() {
        Coeficiente[] result = new Coeficiente[blockSize * 2 / 3];
        int j;
        if (getCurrentDataBlock() != null) {
            byte[] coef = new byte[3];
            j = 0;
            for (int i = 0; i < currentDataBlock.length; i = i + 3) {
                System.arraycopy(currentDataBlock, i, coef, 0, 3);
                result[j] = new Coeficiente(coef);
                j++;
            }
            if (getNextDataBlock() != null) {
                for (int i = 0; i < currentDataBlock.length; i = i + 3) {
                    System.arraycopy(currentDataBlock, i, coef, 0, 3);
                    result[j] = new Coeficiente(coef);
                    j++;
                }
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        getNextDataBlock();
        return result;
    }

    /**
     * Regresa el índice de la posición actual en el arreglo que contiene al archivo.
     * @return un entero con la posición sobre el arreglo de bytes que representa el archivo.
     */
    public long getCurrentPos() {
        return currentPos;
    }

    /**
     * Verifica si hay un siguiente bloque de datos para entregar.
     * @return Cierto si existe un siguiente bloque, falso en caso contrario.
     */
    public boolean isNextDataBlock() {
        return currentPos < fileSize && blockSize != 0;
    }

    /**
     * Obtiene el tamaño actual de los bloques a leer, este parámetro no debe variar
     * mientras haya lectura de bloques.
     * @return El tamaño del bloque actual.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Inicializa el tamaño del bloque a su nuevo valor.
     * @param size Nuevo tamaño del bloque
     */
    public void setBlockSize(int size) {
        blockSize = size;
        currentDataBlock = new byte[blockSize];
    }

    /**
     * Regresa el tamaño total del archivo.
     * @return el tamaño total del archivo.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Escribe el el byte al final del archivo de salida actual.
     * @param data el byte a escribir en el archivo.
     */
    public void appendData(byte data) {
        try {
            OutputStream os = new FileOutputStream(file, true);
            os.write(data);
            os.close();
        } catch (IOException e) {
            System.err.println("Couldn't write the data");
        }
    }

    /**
     * Escribe el arreglo de bytes al final del archivo de salida actual.
     * @param data el arreglo de bytes a escribir en el archivo.
     */
    public void appendData(byte[] data) {
        if (write) {
            try {
                OutputStream os = new FileOutputStream(file, true);
                os.write(data);
                os.close();
            } catch (IOException e) {
                System.err.println("Couldn't write the data");
            }
        } else {
            System.err.println("El archivo se abrio en modo lectura");
        }
    }

    /**
     * Escribe el arreglo de bytes que representa la cadena al final del archivo de salida actual.
     * @param data cadena a escribir en el archivo.
     */
    public void appendData(String data) {
        if (write) {
            try {
                OutputStream os = new FileOutputStream(file, true);
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeBytes(data);
                os.close();
            } catch (IOException e) {
                System.err.println("Couldn't write the data");
            }
        } else {
            System.err.println("El archivo se abrio en modo lectura");
        }
    }

    /**
     * Regresa el bloque actual de datos, es decir, el arreglo de bytes de tamaño del bloque a partir de la posición actual
     * @return El arreglo de bytes del bloque actual.
     */
    public byte[] getCurrentDataBlock() {
        if (currentDataBlock == null) {
            currentDataBlock = new byte[blockSize];
        }
        try {
            System.arraycopy(bytes, (int) currentPos, currentDataBlock, 0, blockSize);
        } catch (Exception e) {
            currentDataBlock = null;
        }
        return currentDataBlock;
    }

    /**
     * Regresa el bloque actual de coeficientes en el archivo.
     * @return arreglo de coeficientes que se encuentran en el archivo.
     */
    public Coeficiente[] getCurrentCoeficienteDataBlock() {
        Coeficiente[] result = new Coeficiente[blockSize * 2 / 3];
        int j;
        if (getCurrentDataBlock() != null) {
            byte[] coef = new byte[3];
            j = 0;
            for (int i = 0; i < currentDataBlock.length; i = i + 3) {
                System.arraycopy(currentDataBlock, i, coef, 0, 3);
                result[j] = new Coeficiente(coef);
                j++;
            }
            if (getNextDataBlock() != null) {
                for (int i = 0; i < currentDataBlock.length; i = i + 3) {
                    System.arraycopy(currentDataBlock, i, coef, 0, 3);
                    result[j] = new Coeficiente(coef);
                    j++;
                }
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Regresa la cantidad de bloques restantes en el archivo.
     * @return entero con la cantidad de bloques restantes en el archivo.
     */
    public int getRemainingBlocks() {
        return (int) ((fileSize - currentPos) / blockSize);
    }
}
