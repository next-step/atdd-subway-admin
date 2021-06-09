package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private static final String EXCEPTION_FOR_EQUAL_SECTION = "상행역, 하행역 모두 등록되어 있습니다.";
    private static final String EXCEPTION_FOR_HAS_NOT_STATIONS = "상행역, 하행역 모두 등록되어 있지 않습니다.";

    protected Sections() {
    }

    public void add(Section section) {
        if (isFirstSection()) {
            sections.add(section);
            return;
        }

        checkExistEqualSection(section);
        checkNotExistAnyEqualStation(section);

        addEqualUpStation(section);
        addEqualDownStation(section);
        sections.add(section);
    }

    private void checkExistEqualSection(Section section) {
        if (isMatchWithUpStation(section) && isMatchWithDownStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_EQUAL_SECTION);
        }
    }

    private void checkNotExistAnyEqualStation(Section section) {
        if (!isMatchWithUpStation(section) && !isMatchWithDownStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_HAS_NOT_STATIONS);
        }
    }

    private void addEqualUpStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsUpStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(Section.makeAfterSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    private void addEqualDownStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsDownStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(Section.makeBeforeSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> findStationInSections() {
        Section firstSection = findFirstSection();
        return firstSection.findAllStations(this);
    }

    public List<Station> findOthersStations(Section section) {
        List<Station> stations = section.findStationsByFirstSection(this);
        return new ArrayList<>(stations);
    }

    public Section findFirstSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(findSectionInDownStation(section)))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Section findSectionInDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasSameDownStation(newSection))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionInUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(newSection))
                .findFirst()
                .orElse(null);
    }

    public boolean isFirstSection() {
        return sections.isEmpty();
    }

    public boolean isMatchWithUpStation(Section section) {
        return findStationInSections().stream().anyMatch(section::isMatchUpStation);
    }

    public boolean isMatchWithDownStation(Section section) {
        return findStationInSections().stream().anyMatch(section::isMatchDownStation);
    }
}
