package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.RESOURCE_NOT_FOUND;

public class ResourceNotFoundException extends RuntimeException {
    public <T> ResourceNotFoundException(final Class<T> resource) {
        super(String.format(RESOURCE_NOT_FOUND, resource.getSimpleName()));
    }
}
