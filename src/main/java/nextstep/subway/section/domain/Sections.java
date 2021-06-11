package nextstep.subway.section.domain;

import nextstep.subway.section.exception.ExistSameStationsException;
import nextstep.subway.section.exception.NotExistAnySameStationException;
import nextstep.subway.section.exception.NotUnderSectionDistanceException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (!CollectionUtils.isEmpty(sections)) {
            validateSection(newSection);
        }

        connectIfExistSameUpStation(newSection);
        connectIfExistSameDownStation(newSection);

        this.sections.add(newSection);
    }

    public List<Station> getStations() {
        return getOrderedSections().stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public static Sections of(List<Section> sections) {
        Sections newSections = new Sections();
        sections.forEach(newSections::add);
        return newSections;
    }

    private void connectIfExistSameDownStation(Section newSection) {
        sections.stream()
                .filter(section -> section.hasSameDownStation(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStationToUpStation(newSection));
    }

    private void connectIfExistSameUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.hasSameUpStation(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStationToDownStation(newSection));
    }

    public List<Section> getOrderedSections() {

        Optional<Section> firstSection = findFirstSection();

        List<Section> result = new ArrayList<>();
        while (firstSection.isPresent()) {
            Section section = firstSection.get();
            result.add(section);
            firstSection = sections.stream()
                    .filter(nextSection -> nextSection.getUpStation().equals(section.getDownStation()))
                    .findFirst();
        }

        return result;
    }

    private Optional<Section> findFirstSection() {
        return this.sections.stream()
                .filter(section -> !getDownStations().contains(section.getUpStation()))
                .findFirst();
    }

    private List<Station> getUpStations() {
        return this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private void validateSection(Section newSection) {
        if (isNotValidDistance(newSection)) {
            throw new NotUnderSectionDistanceException();
        }

        boolean containsUpStation = containsUpStation(newSection.getUpStation());
        boolean containsDownStation = containsDownStation(newSection.getDownStation());

        if (containsUpStation && containsDownStation) {
            throw new ExistSameStationsException();
        }

        if (!containsUpStation && !containsDownStation) {
            throw new NotExistAnySameStationException();
        }
    }

    private boolean containsDownStation(Station station) {
        return getStations().contains(station);
    }

    private boolean containsUpStation(Station station) {
        return getStations().contains(station);
    }

    private boolean isNotValidDistance(Section newSection) {
        return sections.stream()
                .anyMatch(section -> (isSameUpStation(newSection, section) || isSameDownStation(newSection, section))
                        && section.getDistance() == newSection.getDistance());
    }

    private boolean isSameDownStation(Section newSection, Section section) {
        return section.hasSameDownStation(newSection.getDownStation());
    }

    private boolean isSameUpStation(Section newSection, Section section) {
        return section.hasSameUpStation(newSection.getUpStation());
    }
}
