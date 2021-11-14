package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import nextstep.subway.common.exception.InvalidDataException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    @Transient
    private Map<Station, Section> upStationToSection;

    @Transient
    private Map<Station, Section> downStationToSection;

    protected Sections() {
    }

    private Sections(Section section) {
        validateNotNull(section);
        this.list.add(section);
    }

    public static Sections from(Section section) {
        return new Sections(section);
    }

    List<Station> stations() {
        Section firstSection = firstSection();
        List<Station> stations = new ArrayList<>(firstSection.stations());
        Station nextStation = firstSection.downStation();
        while (hasSectionByUpStation(nextStation)) {
            nextStation = sectionByUpStation(nextStation)
                .downStation();
            stations.add(nextStation);
        }
        return stations;
    }

    void setLine(Line line) {
        for (Section section : list) {
            section.setLine(line);
        }
    }

    void addSection(Section section) {
        validateAdditionalSection(section);
        addValidatedSection(section);
        deleteSectionsCache();
    }

    private Section firstSection() {
        return list.stream()
            .filter(this::doesNotHavePreviousSection)
            .findFirst()
            .orElseThrow(() -> new InvalidDataException("not exists"));
    }

    private boolean doesNotHavePreviousSection(Section section) {
        return !hasSectionByDownStation(section.upStation());
    }

    private void addValidatedSection(Section section) {
        if (isExist(section.upStation())) {
            addByUpStation(section);
            return;
        }
        addByDownStation(section);
    }

    private void addByUpStation(Section section) {
        if (hasSectionByUpStation(section.upStation())) {
            sectionByUpStation(section.upStation())
                .remove(section);
        }
        list.add(section);
    }

    private void addByDownStation(Section section) {
        if (hasSectionByDownStation(section.downStation())) {
            sectionByDownStation(section.downStation())
                .remove(section);
        }
        list.add(section);
    }

    private void validateAdditionalSection(Section section) {
        validateNotNull(section);
        if (doesNotContainOnlyOneStation(section)) {
            throw new InvalidDataException(
                String.format("stations of section(%s) must be only one overlapping station",
                    section));
        }
    }

    private boolean doesNotContainOnlyOneStation(Section section) {
        return isExist(section.upStation()) == isExist(section.downStation());
    }

    private boolean isExist(Station station) {
        return hasSectionByDownStation(station) || hasSectionByUpStation(station);
    }

    private boolean hasSectionByUpStation(Station station) {
        return sectionByUpStation(station) != null;
    }

    private boolean hasSectionByDownStation(Station station) {
        return sectionByDownStation(station) != null;
    }

    private Section sectionByDownStation(Station station) {
        if (downStationToSection == null) {
            downStationToSection = list.stream()
                .collect(Collectors.toMap(Section::downStation, section -> section));
        }
        return downStationToSection.get(station);
    }


    private Section sectionByUpStation(Station station) {
        if (upStationToSection == null) {
            upStationToSection = list.stream()
                .collect(Collectors.toMap(Section::upStation, section -> section));
        }
        return upStationToSection.get(station);
    }

    private void validateNotNull(Section section) {
        Assert.notNull(section, "section must not be null");
    }

    private void deleteSectionsCache() {
        upStationToSection = null;
        downStationToSection = null;
    }

    @Override
    public String toString() {
        return "Sections{" +
            "list=" + list +
            '}';
    }
}
