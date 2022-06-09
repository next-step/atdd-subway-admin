package nextstep.subway.exception;

public class LineNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 8385870385490600730L;

	public LineNotFoundException() {
		super("라인 정보를 찾지 못했습니다.");
	}
}
