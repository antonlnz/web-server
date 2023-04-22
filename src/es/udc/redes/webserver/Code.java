package es.udc.redes.webserver;

public enum Code {

    OK("200 OK"),
    NOT_MODIFIED("304 Not Modified"),
    BAD_REQUEST("400 Bad Request"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found"),
    NOT_IMPLEMENTED("501 Not Implemented");

    private final String estado;

    private Code(String code) {
        this.estado = code;
    }
    
    public String getEstado() {
        return estado;
    }
    
    @Override
    public String toString() {
        String aux = null;
        switch (this) {
            case OK -> {
                aux = "200 OK";
            }
            case NOT_MODIFIED -> {
                aux = "304 Not Modified";
            }
            case BAD_REQUEST -> {
                aux = "400 Bad Request";
            }
            case FORBIDDEN -> {
                aux = "403 Forbidden";
            }
            case NOT_FOUND -> {
                aux = "404 Not Found";
            }
            case NOT_IMPLEMENTED -> {
                aux = "501 Not Implemented";
            }
        }
        return aux;
    }

}
