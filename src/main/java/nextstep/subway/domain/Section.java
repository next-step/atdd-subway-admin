package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        validateIncludeAnyStation(line, upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateIncludeAnyStation(Line line, Station newUpStation, Station newDownStation) {
        if (line.getUpStation().getId().equals(newUpStation.getId()) || line.getUpStation().getId().equals(newDownStation.getId())) {
            return;
        }

        if (line.getDownStation().getId().equals(newUpStation.getId()) || line.getDownStation().getId().equals(newDownStation.getId())) {
            return;
        }

        throw new InvalidSectionException(line.getUpStation().getId(), line.getDownStation().getId());
    }

    public Long getId() {
        return id;
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

    public Long getDistance() {
        return distance;
    }

    public boolean isExistsSection(Section newSection) {
        if (!upStation.getId().equals(newSection.getUpStation().getId())) {
            return false;
        }

        if (!downStation.getId().equals(newSection.getDownStation().getId())) {
            return false;
        }

        return true;
    }
}
