package nextstep.subway.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, Long id) {
        super(String.format("해당 엔티티를 찾을수 없습니다. 엔티티명[%s] 아이디[%s]", entity, id));
    }
}
