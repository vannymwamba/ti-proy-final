/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

/**
 *
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

    /**
     * Creates  new Object FileManager that will operate with the file given in fName
     * @param fName
     * The file path
     */
    public FileManager(String fName) {
        file = new File(fName);
        System.out.println("File Name: " + fName);
        type = fName.split("\\.")[1];

        System.out.println("Archivo tipo " + type);

        try {
            readFile();
            currentPos = 0;

            if (type.equals("wav")) {
                headerSize = 44;
                currentPos = headerSize;
            }

        } catch (IOException e) {
            System.err.println("Ocurrio un error al recuperar los datos del archivo");
            System.err.println(e.toString());
            bytes = null;
        }
    }


    /*
     * Reads the whole file and stores it information in a byte array structure
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
     * Gets the whole file in a byte array representation
     * @return
     * An array of bytes that correspond to the file
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Gets the file header
     * @return
     * The file header
     */
    public byte[] getHeader() {
        byte header[] = new byte[headerSize];
        System.arraycopy(bytes, 0, header, 0, headerSize);
        return header;
    }

    /**
     * Gets the next block of non header data
     * @return
     * A byte array with the next block data, if there is no more blocks available it returns null
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
            if ((fileSize - currentPos) < blockSize) {
                currentBlockSize = (int) (fileSize - currentPos);
            }
            System.arraycopy(bytes, (int) currentPos, currentDataBlock, 0, currentBlockSize);
        } else {
            return null;
        }
        return currentDataBlock;
    }

    /**
     * Verify if there are any available blocks to read
     * @return
     * True if there are available blocks, false otherwise
     */
    public boolean isNextDataBlock() {
        return currentPos < fileSize;
    }

    /**
     * Gets the current size of the blocks to read, this parameter should be the
     * the same during all the lecture per block
     * @return
     * The lecture block size in bytes
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Sets the block size to it's new value
     * @param size
     * New block size
     */
    public void setBlockSize(int size) {
        blockSize = size;
    }

    public void writeByteArray(String fName, byte[] data) throws IOException {
        OutputStream os = new FileOutputStream(new File(fName));
        os.write(data);
        os.close();
    }

    public long getFileSize() {
        return fileSize;
    }
}
