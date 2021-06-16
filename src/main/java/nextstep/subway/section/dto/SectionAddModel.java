package nextstep.subway.section.dto;

import nextstep.subway.common.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionAddModel {
    private final Station upStation;
    private final Station downStation;
    private final Section upSection;
    private final Section downSection;
    private final Line line;
    private final Distance distance;

    public SectionAddModel(Station upStation, Station downStation, Section upSection, Section downSection, Line line, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.upSection = upSection;
        this.downSection = downSection;
        this.line = line;
        this.distance = distance;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Section upSection() {
        return upSection;
    }

    public Section downSection() {
        return downSection;
    }

    public Line line() {
        return line;
    }

    public Distance distance() {
        return distance;
    }
}
