package nextstep.subway.exception;

public class InvalidStationException extends RuntimeException {

    public InvalidStationException(){
        super();
    }

    public InvalidStationException(String message){
        super(message);
    }
}
