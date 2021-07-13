package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.DuplicateSectionException;
import nextstep.subway.common.exception.NotMatchStationException;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public Sections getSections() {
        return this.sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        List<Station> stations = this.getStations();

        boolean upStationMatched = stations.stream().anyMatch(station -> station == upStation);
        boolean downStationMatched = stations.stream().anyMatch(station -> station == downStation);

        if(upStationMatched && downStationMatched) {
            throw new DuplicateSectionException();
        }

        if(!upStationMatched && !downStationMatched) {
            throw new NotMatchStationException();
        }
        // 추가 상행역이 역들 중에 있는 경우
        if(upStationMatched) {
            // 역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
            this.getSections().findSectionByUpStation(upStation)
                .ifPresent(section -> section.updateUpStation(downStation, distance));
        }

        // 추가 하행역이 역들 중에 있는 경우
        if(downStationMatched) {
            //역들 사이에 들어가는 경우, 기존 구간 정보 업데이트
            this.getSections().findSectionByDownStation(downStation)
                .ifPresent(section -> section.updateDownStation(upStation, distance));
        }

        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public void removeStation(Station station) {
        sections.removeStation(this, station);
    }
}
