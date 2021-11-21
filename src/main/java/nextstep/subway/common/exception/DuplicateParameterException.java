package nextstep.subway.common.exception;

/**
 * packageName : nextstep.subway.common.exception
 * fileName : NotFoundException
 * author : haedoang
 * date : 2021/11/18
 * description : 중복 데이터 관련 예외처리
 */
public class DuplicateParameterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateParameterException() {
    }

    public DuplicateParameterException(String message) {
        super(message);
    }
}
