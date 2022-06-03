package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class CreateSectionDTO {
    private Station upStation;
    private Station downStation;
    private Section targetSection;
    private Station targetStation;
    private Line line;

    public CreateSectionDTO(Station upStation, Station downStation, Section targetSection,
        Station targetStation, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.targetSection = targetSection;
        this.targetStation = targetStation;
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Section getTargetSection() {
        return targetSection;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    public Line getLine() {
        return line;
    }
}
