package nextstep.subway.exception;

public class InvalidDistanceException extends RuntimeException {

    public InvalidDistanceException(){
        super();
    }

    public InvalidDistanceException(String message){
        super(message);
    }
}
