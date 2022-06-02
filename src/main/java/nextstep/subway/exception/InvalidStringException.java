package nextstep.subway.exception;

public class InvalidStringException extends RuntimeException {

    public InvalidStringException(){
        super();
    }

    public InvalidStringException(String message){
        super(message);
    }
}
