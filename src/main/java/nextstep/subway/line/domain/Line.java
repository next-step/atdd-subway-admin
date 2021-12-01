package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

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

    public Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        this(name, color);
        this.sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public void addSection(Section section) {
        this.sections.updateSection(section);
        section.addLine(this);
    }

    public void removeStation(Station station) {
        checkValidateStation(station);
        this.sections.removeStation(station);
    }

   private void checkValidateStation(Station station) {
        hasStationInLine(station);
        isLastOneSection();
    }

    private void isLastOneSection() {
        if (this.sections.hasLastOneSection()) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_ONLY_ONE_SECTION_IN_LINE);
        }
    }

    private void hasStationInLine(Station station) {
        List<Station> registeredStations = getStations();
        if (!registeredStations.contains(station)) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_STATION_IN_LINE);
        }
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
        return sections.getStations();
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public Sections sections() {
        return sections;
    }

    public List<Station> getOrderedSections() {
        return sections.getOrderedStation();
    }

    public Station getFirstStation() {
        return this.sections().findFirstStation();
    }

    public Station getLastStation() {
        return this.sections().findLastStation();
    }

    public Section findSection(Station upStation, Station downStation) {
        return this.getSections().stream()
                .filter(it -> isSameSection(upStation, downStation, it))
                .findFirst()
                .orElseThrow(
                        () -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_SECTION)
                );
    }

    private boolean isSameSection(Station upStation, Station downStation, Section it) {
        return it.getUpStation() == upStation && it.getDownStation() == downStation;
    }
}
