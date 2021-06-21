package nextstep.subway.line.domain;

public final class LineNameDuplicatedException extends Exception {

	public LineNameDuplicatedException(String name) {
		super("Line name duplicated. " + name);
	}
}
