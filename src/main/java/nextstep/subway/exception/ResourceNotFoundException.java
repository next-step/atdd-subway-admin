package nextstep.subway.exception;

public class ResourceNotFoundException extends RuntimeException {
    public <T> ResourceNotFoundException(final Class<T> resource, final String message) {
        super(String.format(message, resource.getSimpleName()));
    }
}
