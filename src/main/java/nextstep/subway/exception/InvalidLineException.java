package nextstep.subway.exception;

public class InvalidLineException extends RuntimeException {

    public InvalidLineException(){
        super();
    }

    public InvalidLineException(String message){
        super(message);
    }
}
