package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.constant.ErrorCode;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> findStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        validateDuplicateSection(newSection);
        validateNotContainAnySection(newSection);

        Optional<Section> updateUpStationSection = findUpdateUpStationSection(newSection);
        Optional<Section> updateDownStationSection = findUpdateDownStationSection(newSection);
        if(hasNotBothUpAnddownStationSection(updateUpStationSection.isPresent(), updateDownStationSection.isPresent())) {
            newSection.addLineDistance();
        }
        updateUpStationSection.ifPresent(section -> section.updateUpStation(newSection));
        updateDownStationSection.ifPresent(section -> section.updateDownStation(newSection));
        sections.add(newSection);
    }

    public void deleteStationInLine(Station deleteStation) {
        Optional<Section> upStationSection = findSectionByUpStation(deleteStation);
        Optional<Section> downStationSection = findSectionByDownStation(deleteStation);
        validateNotContainAnySection(upStationSection.isPresent(), downStationSection.isPresent());
        validateIfOnlyOneSection();
        if(hasBothUpAndDownStationSection(upStationSection.isPresent(), downStationSection.isPresent())) {
            combineSection(downStationSection.get(), upStationSection.get());
        }
        upStationSection.ifPresent(this::deleteSection);
        downStationSection.ifPresent(this::deleteSection);
    }

    private void combineSection(Section upSection, Section downSection) {
        Distance distance = upSection.addDistance(downSection);
        Section section = Section.of(upSection.getUpStation(), downSection.getDownStation(), upSection.getLine(), distance.value());
        sections.add(section);
        section.addLineDistance();
    }

    private void validateDuplicateSection(Section section) {
        if(isAllContainStations(section)) {
            throw new IllegalArgumentException(ErrorCode.이미_존재하는_구간.getErrorMessage());
        }
    }

    private void validateNotContainAnySection(Section section) {
        if(isNotContainAnyStation(section)) {
            throw new IllegalArgumentException(ErrorCode.구간의_상행역과_하행역이_모두_노선에_포함되지_않음.getErrorMessage());
        }
    }

    private boolean isAllContainStations(Section section) {
        return findStations().containsAll(section.stations());
    }

    private boolean isNotContainAnyStation(Section section) {
        return findStations().stream()
                .noneMatch(station -> section.stations().contains(station));
    }

    private void validateNotContainAnySection(boolean hasUpStationSection, boolean hasDownStationSection) {
        if(hasNotBothUpAnddownStationSection(hasUpStationSection, hasDownStationSection)) {
            throw new IllegalArgumentException(ErrorCode.노선_내_존재하지_않는_역.getErrorMessage());
        }
    }

    private boolean hasNotBothUpAnddownStationSection(boolean hasUpStationSection, boolean hasDownStationSection) {
        return !hasUpStationSection && !hasDownStationSection;
    }

    private void validateIfOnlyOneSection() {
        if(sections.size() == 1) {
            throw new IllegalArgumentException(ErrorCode.노선에_속한_구간이_하나이면_제거_불가.getErrorMessage());
        }
    }

    private boolean hasBothUpAndDownStationSection(boolean hasUpStationSection, boolean hasDownStationSection) {
        return hasUpStationSection && hasDownStationSection;
    }

    private Optional<Section> findUpdateUpStationSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(newSection.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findUpdateDownStationSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(newSection.getDownStation()))
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst();
    }

    private void deleteSection(Section deleteSection) {
        sections.removeIf(section -> section.isSameSection(deleteSection));
        deleteSection.substractLineDistance();
    }
}
