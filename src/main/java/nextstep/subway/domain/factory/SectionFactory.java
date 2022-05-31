package nextstep.subway.domain.factory;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionFactory {
    public static Section createSectionAtMiddleOfLine(Station upStation, Station downStation, Long distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section createSectionAtLastOfLine(Station lastStation) {
        return new Section(lastStation, null, 0L);
    }
}
