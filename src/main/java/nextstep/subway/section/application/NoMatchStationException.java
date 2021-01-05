package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;

public class NoMatchStationException extends RuntimeException {
    public NoMatchStationException(Section newSection) {
        super(newSection.toString());
    }
}
