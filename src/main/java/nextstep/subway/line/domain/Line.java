package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.advice.exception.SectionBadRequestException;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Section> getSections() {
        return sections;
    }

    public void sortSections() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        Station upStation = upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst().get();

        List<Section> newSections = addNewSections(upStation);
        updateSections(newSections);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void createSection(Section section) {
        sections.add(section);
    }

    public void addSection(Section newSection) {
        if (isTerminalStation(newSection)) {
            sections.add(newSection);
            return;
        }
        if (isUpStation(newSection)) {
            addMiddleDownStation(newSection);
            sections.add(newSection);
            return;
        }
        if (isDownStation(newSection)) {
            addMiddleUpStation(newSection);
            sections.add(newSection);
            return;
        }
    }

    private boolean isUpStation(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    private boolean isDownStation(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getDownStation()));
    }

    private void addMiddleDownStation(Section newSection) {
        sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .filter(section -> validateDistance(section, newSection.getDistance()))
                .findFirst().ifPresent(section -> {
            section.updateUpStation(newSection.getDownStation());
            section.updateDistance(section.getDistance() - newSection.getDistance());
        });
    }

    private void addMiddleUpStation(Section newSection) {
        sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .filter(section -> validateDistance(section, newSection.getDistance()))
                .findFirst().ifPresent(section -> {
            section.updateDownStation(newSection.getUpStation());
            section.updateDistance(section.getDistance() - newSection.getDistance());
        });
    }

    private boolean isTerminalStation(Section newSection) {
        return sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .anyMatch(section -> section.getUpStation().equals(newSection.getDownStation()) ||
                        section.getDownStation().equals(newSection.getUpStation()));
    }

    private boolean validateDistance(Section section, int distance) {
        if (section.getDistance() > distance) {
            return true;
        }
        throw new SectionBadRequestException(section.getDistance(), distance);
    }

    private boolean isNotDuplicateStations(Section section, Section newSection) {
        if (!(section.getUpStation().equals(newSection.getUpStation())
                && section.getDownStation().equals(newSection.getDownStation()))) {
            return true;
        }
        throw new SectionBadRequestException(section);
    }

    private List<Station> getDownStations() {
        List<Station> downStations = sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
        return downStations;
    }

    private List<Station> getUpStations() {
        List<Station> upStations = sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
        return upStations;
    }

    private List<Section> addNewSections(Station upStation) {
        List<Section> newSections = new ArrayList<>();
        Section firtSection = sections.stream()
                .filter(section -> section.getUpStation() == upStation)
                .findFirst().get();

        newSections.add(firtSection);

        Station downStation = firtSection.getDownStation();

        while (!(sections.size() == newSections.size())) {
            Station finalDownStation = downStation;
            Optional<Section> optionalSection = sections.stream()
                    .filter(section -> section.getUpStation() == finalDownStation)
                    .findFirst();

            if (optionalSection.isPresent()) {
                newSections.add(optionalSection.get());
                downStation = optionalSection.get().getUpStation();
            }
        }
        return newSections;
    }

    private void updateSections(List<Section> newSections) {
        this.sections = newSections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return id != null ? id.equals(line.id) : line.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
