package nextstep.subway.common.exception;

/**
 * packageName : nextstep.subway.common.exception
 * fileName : SectionNotDeleteException
 * author : haedoang
 * date : 2021/11/23
 * description :
 */
public class SectionNotDeleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SectionNotDeleteException(String message) {
        super(message);
    }
}
