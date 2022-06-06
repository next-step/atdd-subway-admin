package nextstep.subway.exception;

public class InvalidSectionException extends RuntimeException {

    public InvalidSectionException(){
        super();
    }

    public InvalidSectionException(String message){
        super(message);
    }
}
