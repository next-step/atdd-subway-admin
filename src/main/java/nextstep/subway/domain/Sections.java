package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.constants.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public void addSection(Section newSection) {
        validateNotExistsStations(newSection);
        validateDuplicateStations(newSection);

        Optional<Section> sectionContainsUpStation = getSectionContainsUpStation(newSection.getUpStation());
        Optional<Section> sectionContainsDownStation = getSectionContainsDownStation(newSection.getDownStation());
        if (isAddInTheMiddleOfSection(sectionContainsUpStation, sectionContainsDownStation)) {
            validateDistance(newSection);
        }
        sectionContainsUpStation.ifPresent(section -> section.updateUpStation(newSection));
        sectionContainsDownStation.ifPresent(section -> section.updateDownStation(newSection));
        sections.add(newSection);
    }

    private boolean isAddInTheMiddleOfSection(Optional<Section> sectionContainsUpStation, Optional<Section> sectionContainsDownStation) {
        return sectionContainsUpStation.isPresent() || sectionContainsDownStation.isPresent();
    }

    private void validateNotExistsStations(Section newSection) {
        if (!getStations().contains(newSection.getUpStation()) && !getStations().contains(newSection.getDownStation())) {
            throw new IllegalArgumentException(NOT_EXISTS_SECTION_STATION_MSG);
        }
    }

    private void validateDuplicateStations(Section newSection) {
        if (sections.stream().anyMatch(section -> section.getStations().containsAll(newSection.getStations()))) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_STATION_MSG);
        }
    }

    private void validateDistance(Section newSection) {
        if (sections.stream().anyMatch(section -> section.getDistance().isSameOrLonger(newSection.getDistance()))) {
            throw new IllegalArgumentException(INVALID_DISTANCE_MSG);
        }
    }

    public List<Station> getStations() {
        return sections.stream().map(section -> section.getStations()).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    public void deleteSection(Station deleteStation) {
        validateSectionSizeGreaterThanOne();

        Optional<Section> sectionContainsUpStation = getSectionContainsUpStation(deleteStation);
        Optional<Section> sectionContainsDownStation = getSectionContainsDownStation(deleteStation);
        validateNotExistsStations(sectionContainsUpStation, sectionContainsDownStation);

        if (isDeleteInTheMiddleOfSection(sectionContainsUpStation, sectionContainsDownStation)) {
            //A-B-C 에서 B(중간역) 삭제시, A-C 구간 신규생성
            mergeSection(sectionContainsUpStation.get(), sectionContainsDownStation.get());
        }
        sectionContainsUpStation.ifPresent(section -> removeSection(section));
        sectionContainsDownStation.ifPresent(section -> removeSection(section));
    }

    private void mergeSection(Section sectionContainsUpStation, Section sectionContainsDownStation) {
        Distance mergedDistance = sectionContainsDownStation.addDistance(sectionContainsUpStation);
        sections.add(Section.of(sectionContainsUpStation.getLine(),
                                sectionContainsDownStation.getUpStation(),
                                sectionContainsUpStation.getDownStation(),
                                mergedDistance));
    }

    private void validateSectionSizeGreaterThanOne() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_ONLY_IF_ONE_SECTION_MSG);
        }
    }

    private void validateNotExistsStations(Optional<Section> sectionContainsUpStation, Optional<Section> sectionContainsDownStation) {
        if (!sectionContainsUpStation.isPresent() && !sectionContainsDownStation.isPresent()) {
            throw new IllegalArgumentException(NOT_EXISTS_SECTION_STATION_MSG);
        }
    }
    private boolean isDeleteInTheMiddleOfSection(Optional<Section> sectionContainsUpStation, Optional<Section> sectionContainsDownStation) {
        return sectionContainsUpStation.isPresent() && sectionContainsDownStation.isPresent();
    }

    private Optional<Section> getSectionContainsUpStation(Station deleteStation) {
        return sections.stream().filter(section -> section.isEqualsUpStation(deleteStation)).findFirst();
    }

    private Optional<Section> getSectionContainsDownStation(Station deleteStation) {
        return sections.stream().filter(section -> section.isEqualsDownStation(deleteStation)).findFirst();
    }

    private void removeSection(Section deleteSection) {
        sections.removeIf(section -> section.equals(deleteSection));
    }

    public Distance getTotalDistance() {
        return new Distance(sections.stream().mapToInt(section -> section.getDistance().getDistance()).sum());
    }
}
