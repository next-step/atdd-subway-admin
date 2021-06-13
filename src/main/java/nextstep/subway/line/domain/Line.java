package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        this.name = name;
        this.color = color;
        this.sections.addSection(new Section(this, upStation, downStation, distance));
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
        if (sections.getSections().isEmpty()) {
            return Arrays.asList();
        }
        return sections.orderSection();
    }

    public void addSection(Section section) {
        validSection(section);
        Boolean isUpStationExist = isUpStationExist(section);
        Boolean isDownStationExist = isDownStationExist(section);

        if (isUpStationExist) {
            sections.updateUpStation(section);
        }

        if (isDownStationExist) {
            sections.updateDownStation(section);
        }
        sections.addSection(section);
    }

    public void validSection(Section section) {
        Boolean isUpStationExist = isUpStationExist(section);
        Boolean isDownStationExist = isDownStationExist(section);

        if (!isUpStationExist && !isDownStationExist && !getStations().isEmpty()) {
            throw new RuntimeException("상/하행선 둘 중 하나는 일치해야 합니다.");
        }
        if (sections.validDuplicationSection(section)) {
            throw new RuntimeException("동일한 구간은 추가할 수 없습니다.");
        }
    }

    public boolean isUpStationExist(Section section) {
        return getStations().stream().anyMatch(it -> it.equals(section.getUpStation()));
    }

    public boolean isDownStationExist(Section section) {
        return getStations().stream().anyMatch(it -> it.equals(section.getDownStation()));
    }
}
