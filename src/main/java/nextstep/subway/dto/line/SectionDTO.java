package nextstep.subway.dto.line;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.station.Station;

public class SectionDTO {

    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final long distance;

    public SectionDTO(Line line, Station upStation, Station downStation, long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public long getDistance() {
        return distance;
    }

    public LineStation toLineStation() {
        return new LineStation(line,upStation,downStation,distance);
    }

    public LineStation toLineStationLinkByDownStation(Station addDownStation, long distance) {
        return new LineStation(line,downStation,addDownStation,distance);
    }
}
