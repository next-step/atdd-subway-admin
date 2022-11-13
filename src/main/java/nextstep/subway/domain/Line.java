package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.constant.ErrorCode;
import nextstep.subway.utils.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Name name;
    @Embedded
    private Color color;
    @Embedded
    private Sections sections;
    @Embedded
    private Distance distance;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        validateUpStation(upStation);
        validateDownStation(downStation);

        Section section = Section.of(upStation, downStation, this, distance);
        this.name = Name.from(name);
        this.color = Color.from(color);
        this.sections = Sections.from(Collections.singletonList(section));
        this.distance = Distance.from(distance);
    }

    public void updateLineNameAndColor(String name, String color) {
        if(!StringUtils.isNullOrEmpty(name)) {
            updateLineName(name);
        }
        if(!StringUtils.isNullOrEmpty(color)) {
            updateLineColor(color);
        }
    }

    private void updateLineName(String name) {
        this.name = Name.from(name);
    }

    private void updateLineColor(String color) {
        this.color = Color.from(color);
    }

    private void validateUpStation(Station upStation) {
        if(upStation == null) {
            throw new IllegalArgumentException(ErrorCode.상행종착역은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateDownStation(Station downStation) {
        if(downStation == null) {
            throw new IllegalArgumentException(ErrorCode.하행종착역은_비어있을_수_없음.getErrorMessage());
        }
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void addDistance(Distance distance) {
        this.distance = this.distance.add(distance);
    }

    public void subtractDistance(Distance distance) {
        this.distance = this.distance.subtract(distance);
    }

    public List<Station> findStations() {
        return sections.findStations();
    }

    public void deleteStationInLine(Station station) {
        sections.deleteStationInLine(station);
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Distance getDistance() {
        return distance;
    }
}
