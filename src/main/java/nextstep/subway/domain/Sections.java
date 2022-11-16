package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.SectionsErrorCode;

@Embeddable
public class Sections {

    private static final int SECTION_ONE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        validateDuplicate(section);
        validateNotExist(section);
        sections.forEach(s -> s.updateStation(section));
        sections.add(section);
    }

    private void validateDuplicate(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException(SectionsErrorCode.SECTION_DUPLICATION_ERROR.getMessage());
        }
    }

    private void validateNotExist(Section section) {
        if (isNotExist(section)) {
            throw new IllegalArgumentException(SectionsErrorCode.SECTION_NOT_EXIST_ERROR.getMessage());
        }
    }

    private boolean isNotExist(Section section) {
        List<Station> stations = getStations();
        return section.getStations()
            .stream()
            .noneMatch(stations::contains);
    }

    public void remove(Station station) {
        validateOneSection();
        validateNotExistStation(station);
        Optional<Section> upStationSection = findUpStationSection(station);
        Optional<Section> downStationSection = findDownStationSection(station);

        if(upStationSection.isPresent() && downStationSection.isPresent()) {
            addConnectSection(upStationSection.get(), downStationSection.get());
        }
        upStationSection.ifPresent(sections::remove);
        downStationSection.ifPresent(sections::remove);
    }

    private void validateOneSection() {
        if (sections.size() == SECTION_ONE) {
            throw new IllegalArgumentException(SectionsErrorCode.SECTION_ONE_ERROR.getMessage());
        }
    }

    private void validateNotExistStation(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException(SectionsErrorCode.SECTION_NOT_EXIST_STATION_ERROR.getMessage());
        }
    }

    private void addConnectSection(Section upSection, Section downSection) {
        sections.add(Section.of(
            upSection.getDownStation(),
            downSection.getUpStation(),
            upSection.getLine(),
            upSection.addDistance(downSection).value()
        ));
    }

    private Optional<Section> findUpStationSection(Station station) {
        return sections.stream()
            .filter(section -> section.isUpStation(station))
            .findFirst();
    }

    private Optional<Section> findDownStationSection(Station station) {
        return sections.stream()
            .filter(section -> section.isDownStation(station))
            .findFirst();
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::getStations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
