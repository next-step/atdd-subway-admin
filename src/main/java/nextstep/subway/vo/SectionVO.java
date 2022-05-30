package nextstep.subway.vo;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class SectionVO {

    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final long distance;

    public SectionVO(Line line, Station upStation, Station downStation, long distance) {
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
}
