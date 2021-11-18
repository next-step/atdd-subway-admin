package nextstep.subway.common.exception;

/**
 * packageName : nextstep.subway.common.exception
 * fileName : NotFoundException
 * author : haedoang
 * date : 2021/11/18
 * description : NoData 예외 처리 클래스
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
