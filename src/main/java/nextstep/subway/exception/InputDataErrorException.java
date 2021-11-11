package nextstep.subway.exception;

public class InputDataErrorException extends RuntimeException {

    private InputDataErrorCode inputDataErrorCode;
    private String errorMessage;

    public InputDataErrorException(InputDataErrorCode inputDataErrorCode) {
        this(inputDataErrorCode, inputDataErrorCode.errorMessage());
        this.inputDataErrorCode = inputDataErrorCode;
    }

    public InputDataErrorException(InputDataErrorCode inputDataErrorCode, String errorMessage) {
        super(errorMessage);
        this.inputDataErrorCode = inputDataErrorCode;
        this.errorMessage = errorMessage;
    }
}
