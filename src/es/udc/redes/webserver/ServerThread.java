package es.udc.redes.webserver;

import java.text.SimpleDateFormat;
import java.lang.String;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Esta clase maneja los hilos del servidor
 * @author antonlopez
 */
public class ServerThread extends Thread {

    private static final int MAX_HEADERS = 20;
    private final Socket socket;
    private Code code;
    private Date date;
    private File file = null;
    // Direccion de los archivos fuente
    private String dir_files = "p1-files";
    // Direccion de los archivos de error
    private String dir_codes = "p1-files";
    private String requestedMethod = "null";

    // Declaramos el formato de fecha que queremos
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MM yyyy HH:mm:ss z", Locale.UK);

    public ServerThread(Socket s) {
        this.socket = s;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(out)) {

            String requestedSource = "null";
            String requestedVersion = "HTTP/1.0 ";

            // GET /index.html HTTP/1.1
            String auxHeader = in.readLine();
            String[] header = auxHeader.split(" ");

            if (header[0] != null && header.length == 3) {
                requestedMethod = header[0];
                requestedSource = header[1];
                requestedVersion = header[2];
            } else {
                code = Code.BAD_REQUEST;
                printHeader(pw, out, "", requestedSource);
                socket.close();
            }

            // Read N heads
            String aux = in.readLine();
            String[] headers = new String[MAX_HEADERS];
            String ifModifiedHeader = null;
            int i = 0;

            if (aux != null) { // Guardamos las cabeceras, el If-Modified-Since por separado
                while (!aux.equals("")) {
                    if (aux.contains("If-Modified-Since")) {
                        ifModifiedHeader = aux;
                        break;
                    } else {
                        aux = in.readLine();
                        headers[i] = aux;
                        i++;
                    }
                }
            }

            if (!requestedMethod.equals("GET") && !requestedMethod.equals("HEAD")) { // Si no es ninguno de estos metodos suponemos un BAD REQUEST (Lo correcto seria un not implemented, en un sistema real)
                code = Code.BAD_REQUEST;
                printHeader(pw, out, "", requestedSource);
            }

            if (requestedMethod.equals("GET")) {
                if (ifModifiedHeader != null && ifModifiedHeader.contains("If-Modified-Since")) {
                    File reload = new File(dir_files + File.separator + requestedSource);
                    String stringIfMod = ifModifiedHeader.substring(19);
                    Date ifModDate = formateDate(stringIfMod);
                    Date lastDate = formateDate(formatDate((int) reload.lastModified()));
                    if (ifModDate.after(lastDate) || ifModDate.equals(lastDate)) { // Si NO se modifico despues de la fecha enviada en la cabecera, 304 Not Modified
                        code = Code.NOT_MODIFIED;
                        printHeader(pw, out, requestedMethod, requestedSource);
                    } else {
                        printHeader(pw, out, requestedMethod, requestedSource);
                    }
                } else {
                    printHeader(pw, out, requestedMethod, requestedSource);
                }
            }

            if (requestedMethod.equals("HEAD")) {
                printHeader(pw, out, requestedMethod, requestedSource);
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printHeader(PrintWriter printWriter, OutputStream outputStream, String method, String content) throws IOException {
        File requestedSource = new File(dir_files + content);

        if (code == Code.NOT_IMPLEMENTED || code == Code.NOT_MODIFIED || code == Code.BAD_REQUEST) {
            switch (code) {
                case NOT_IMPLEMENTED ->  file = new File(dir_codes + "/501.html");
                case NOT_MODIFIED -> file = new File(dir_codes + "/304.html");
                case BAD_REQUEST -> file = new File(dir_codes + "/error400.html");
            }
        } else {
            if (requestedSource.exists()) {
                code = Code.OK;
                file = new File(dir_files + content);
            } else { // If the source file does not exist
                    code = Code.NOT_FOUND;
                    file = new File(dir_codes + "/error404.html");
            }
        }

        int length = (int) file.length();
        byte[] sourceFile = readerData(file, length);

        printWriter.println("HTTP/1.0 " + code.getEstado());
        printWriter.println("Date: " + dateFormat.format(new Date()));
        printWriter.println("Server: udc.redes.webserver/1.0 [Linux]");
        printWriter.println("Last-Modified: " + formatDate(file.lastModified()));
        printWriter.println("Content-Length: " + file.length());
        printWriter.println("Content-Type: " + getType(file));
        printWriter.println("");
        printWriter.flush();

        if(!requestedMethod.equals("HEAD") && code!=Code.NOT_MODIFIED) {
            outputStream.write(sourceFile, 0, length);
        }
        outputStream.flush();
    }

    private byte[] readerData(File file, int lentgh) throws IOException {
        FileInputStream input = null;
        byte[] data = new byte[lentgh];

        try {
            input = new FileInputStream(file);
            input.read(data);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return data;
    }

    private String formatDate(long seconds) {
        return dateFormat.format(seconds);
    }

    private String getType(File fileRequest) {
        String requested = fileRequest.getName();
        String type = "application/octet-stream";

        if (requested.endsWith(".htm") || requested.endsWith(".html")) {
            type = "text/html";
        }
        if (requested.endsWith(".log") || requested.endsWith(".txt")) {
            type = "text/plain";
        }
        if (requested.endsWith(".gif")) {
            type = "image/gif";
        }
        if (requested.endsWith(".png")) {
            type = "image/png";
        }
        return type;
    }

    private Date formateDate(String fecha) {
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(fecha);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return parsedDate;
    }

}
