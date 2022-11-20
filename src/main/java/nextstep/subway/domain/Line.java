package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

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

    @Transient
    private boolean isFindSameStation = false;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void change(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

    public Sections getSections() {
        return sections;
    }

    public List<Section> getSectionList() {
        return sections.getSections();
    }

    public void addSection(int distance, Station upStation, Station downStation) {
        sections.addSection(new Section(this, distance, upStation, downStation));
    }

    public void validateAlreadyAndNotExistsStations(Station upStation, Station downStation) {
        sections.validateAlreadyExistsStation(upStation, downStation);
        sections.validateNotExistsStation(upStation, downStation);
    }

    public void isFindSameUpStationThenCreateNewSection(Station upStation, Station downStation, int distance) {
        addBetweenByUpStation(upStation, downStation, distance);
        prependUpStation(upStation, downStation, distance);
    }

    public void isFindSameDownStationThenCreateNewSection(Station upStation, Station downStation, int distance) {
        addBetweenByDownStation(upStation, downStation, distance);
        appendDownStation(upStation, downStation, distance);
    }

    private void prependUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        sections.findSameUpStation(downStation).ifPresent(section -> {
            sections.addSection(section.createNewSection(distance, upStation, downStation));
            isFindSameStation = true;
        });
    }

    private void addBetweenByUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        sections.findSameUpStation(upStation).ifPresent(section -> {
            section.validateLength(distance);
            sections.addSection(section.createNewSection(distance, upStation, downStation));
            sections.addSection(section.createNewDownSection(distance, downStation));
            sections.removeSection(section);
            isFindSameStation = true;
        });
    }

    private void appendDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        sections.findSameDownStation(upStation).ifPresent(section -> {
            sections.addSection(section.createNewSection(distance, upStation, downStation));
            isFindSameStation = true;
        });
    }

    private void addBetweenByDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        sections.findSameDownStation(downStation).ifPresent(section -> {
            section.validateLength(distance);
            sections.addSection(section.createNewSection(distance, upStation, downStation));
            sections.addSection(section.createNewUpSection(distance, upStation));
            sections.removeSection(section);
            isFindSameStation = true;
        });
    }
}
