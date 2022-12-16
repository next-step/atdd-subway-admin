package nextstep.subway.application;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7134445553090479924L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Long id) {
        super(String.format("Not found with given id %d", id));
    }

}
