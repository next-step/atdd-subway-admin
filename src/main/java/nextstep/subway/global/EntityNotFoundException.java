package nextstep.subway.global;

public class EntityNotFoundException extends RuntimeException {

    public static final String MESSAGE = "엔티티가 존재하지 않습니다.";

    public EntityNotFoundException(Class<?> entityClass) {
        super(String.format("%s %s", entityClass.getSimpleName(), MESSAGE));
    }
}
