package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    private static final String ALREADY_CONTAIN_SECTION_MESSAGE = "이미 포함된 Section 입니다. sectionId=%s";
    private static final String ALREADY_CONTAIN_UP_AND_DOWN_STATIONS_MESSAGE = "신규 구간의 상행역, 하행역이 이미 노선에 포함되있습니다. upStationId=%s, downStationId=%s";
    private static final String NO_EXIST_BOTH_UP_AND_DOWN_STATIONS_MESSAGE = "신규 구간의 상행역, 하행역이 둘다 노선에 포함되지 않습니다.";
    private static final String CAN_NOT_DELETE_STATION_WHEN_ONLY_ONE_SECTIONS_MESSAGE = "노선의 구간이 1개인 경우, 지하철 역을 삭제 할 수 없습니다.";
    private static final String CAN_NOT_DELETE_WHEN_NO_EXIST_STATION = "노선에 존재하지 않는 역입니다. stationsId=%s";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections = Sections.createEmpty();

    protected Line() {}

    private Line(String name, String color) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        validateAddableSection(section);
        validateAddableStations(section);

        sections.add(section);
        section.registerLine(this);
    }

    public void remove(Station station) {
        validateHasOnlyOneSectionWhenDeleteStation();
        validateNotIncludeStation(station);

        if (sections.isEndStation(station)) {
            sections.removeEndStation(station);
            return;
        }

        sections.removeMiddleStation(station);
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public List<Station> getStations() {
        return sections.getSortedStations();
    }

    private void validateAddableSection(Section section) {
        if (!sections.isEmpty() && sections.contains(section)) {
            throw new IllegalStateException(String.format(ALREADY_CONTAIN_SECTION_MESSAGE, section.getId()));
        }
    }

    private void validateAddableStations(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        validateAlreadyContainStations(section);
        validateNoRetainStations(section);
    }

    private void validateAlreadyContainStations(Section section) {
        if (sections.containStations(section.getStations())) {
            throw new IllegalArgumentException(String.format(ALREADY_CONTAIN_UP_AND_DOWN_STATIONS_MESSAGE,
                                                             section.getUpStation().getId(),
                                                             section.getDownStation().getId()));
        }
    }

    private void validateNoRetainStations(Section section) {
        if (!sections.retainStations(section.getStations())) {
            throw new IllegalArgumentException(NO_EXIST_BOTH_UP_AND_DOWN_STATIONS_MESSAGE);
        }
    }

    private void validateHasOnlyOneSectionWhenDeleteStation() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(CAN_NOT_DELETE_STATION_WHEN_ONLY_ONE_SECTIONS_MESSAGE);
        }
    }

    private void validateNotIncludeStation(Station station) {
        if (!sections.hasStation(station)) {
            throw new IllegalArgumentException(CAN_NOT_DELETE_WHEN_NO_EXIST_STATION);
        }
    }
}
