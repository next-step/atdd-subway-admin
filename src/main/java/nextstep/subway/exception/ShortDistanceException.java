package nextstep.subway.exception;

public class ShortDistanceException extends BadRequestException {
    public ShortDistanceException(Integer nowDistance, Integer newDistance) {
        super(String.format("신규 역사이 길이[%d]가 기존 역사이 길이[%d]보다 크거나 같습니다.", newDistance, nowDistance));
    }
}
