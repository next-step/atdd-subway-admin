package nextstep.subway.exception;

import nextstep.subway.domain.Section;

public class DuplicatedSectionException extends IllegalArgumentException {
    private static final String message = "상행역(%d)과 하행역(%d)이 이미 노선에 등록되어 있습니다.";

    public DuplicatedSectionException(Section section) {
        super(String.format(message, section.getUpStation().getId(), section.getDownStation().getId()));
    }
}
