package nextstep.subway.domain.factory;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineFactory {
    public static Line createNewLine(String name, String color, Station upStation, Station downStation, Long distance) {
        Section initSection = SectionFactory.createSectionAtMiddleOfLine(upStation,downStation,distance);
        Section lastSection = SectionFactory.createSectionAtLastOfLine(downStation);
        return new Line(name,color, initSection,lastSection);
    }
}
