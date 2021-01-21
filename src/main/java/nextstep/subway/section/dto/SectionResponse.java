package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class SectionResponse {
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Line line, Station upStation, Station downStation, int distance) {
        return new SectionResponse(line, upStation, downStation, distance);
    }
}
