package es.udc.redes.tutorial.copy;

import java.io.*;

import java.io.IOException;


public class Copy {

    public static void main(String[] args) throws IOException {

        String fichOrigen = args[0];
        String fichDestino = args[1];

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fichOrigen));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fichDestino));

            in = inputStream;
            out = outputStream;
            int c;

            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
    
}
