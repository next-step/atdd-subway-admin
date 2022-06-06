package nextstep.subway.domain;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@DynamicUpdate
@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Integer distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
        this.sections.add(Section.ascendEndPoint(this, upStation));
        this.sections.add(Section.of(this, downStation, upStation, distance));
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

    public List<Section> getAllSections() {
        return this.sections.getAll();
    }

    public Section getAscendEndpoint() {
        return getAllSections().stream().filter(it -> it.getPreStation() == null).findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Section getDescendEndpoint() {
        List<Section> sections = getAllSections();
        return sections.get(sections.size() - 1);
    }

    public void update(String name, String color) {
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
        return getId().equals(line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        checkSame(preStation, station);
        checkAlreadyAdded(preStation, station);

        if (isAscendEndpoint(preStation)) {
            changeAscendEndpoint(station, distance);
            return;
        }

        if (isDescendEndpoint(preStation)) {
            changeDescendEndpoint(station, distance);
            return;
        }

        addBetween(preStation, station, distance);
    }

    private void checkSame(Station preStation, Station station) {
        if (Objects.equals(preStation, station)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkAlreadyAdded(Station preStation, Station station) {
        if (getAllSections().stream().anyMatch(section -> Objects.equals(section.getStation(), station) && Objects.equals(section.getPreStation(), preStation))) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private boolean isAscendEndpoint(Station station) {
        return Objects.equals(station, null);
    }

    private boolean isDescendEndpoint(Station station) {
        return Objects.equals(getDescendEndpoint().getStation(), station);
    }

    private void addBetween(Station preStation, Station station, Integer distance) {
        Section startSection = getAllSections().stream().filter(it -> Objects.equals(preStation, it.getStation())).findFirst()
                .orElseThrow(IllegalAccessError::new);
        Section endSection = getAllSections().get(getAllSections().indexOf(startSection) + 1);

        if (endSection.getDistance() - startSection.getDistance() <= distance) {
            throw new IllegalArgumentException("distance 는 기존 역 구간 내에 속할 수 있는 값이어야 합니다.");
        }

        endSection.updatePreStation(station);
        this.sections.add(Section.of(this, station, preStation, startSection.getDistance() + distance));
    }

    private void changeAscendEndpoint(Station station, Integer distance) {
        for (Section section : getAllSections()) {
            section.updateDuration(section.getDistance() + distance);
        }

        Section section = getAscendEndpoint();
        section.updatePreStation(station);

        this.sections.add(Section.ascendEndPoint(this, station));
    }

    private void changeDescendEndpoint(Station station, Integer distance) {
        Section section = getDescendEndpoint();
        this.sections.add(Section.of(this, station, section.getStation(),
                section.getDistance() + distance));
    }
}
