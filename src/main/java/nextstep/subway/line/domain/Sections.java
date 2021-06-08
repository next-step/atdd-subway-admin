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
            throw new IllegalArgumentException("상행역, 하행역 모두 등록되어 있습니다.");
        }
    }

    private void checkNotExistAnyEqualStation(Section section) {
        if (!isMatchWithUpStation(section) && !isMatchWithDownStation(section)) {
            throw new IllegalArgumentException("상행역, 하행역 모두 등록되어 있지 않습니다.");
        }
    }

    private void addEqualUpStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsUpStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(makeNewAfterSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    private Section makeNewAfterSection(Section preSection, Section section) {
        return Section.makeAfterSection(preSection, section);
    }

    private void addEqualDownStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsDownStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(makeNewBeforeSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    private Section makeNewBeforeSection(Section preSection, Section section) {
        return Section.makeBeforeSection(preSection, section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> findStationInSections() {
        List<Station> stations = new ArrayList<>();
        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());
        stations.addAll(findOthersStations(firstSection.getDownStation()));
        return stations;
    }

    public List<Station> findOthersStations(Station downStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(downStation);
        Section nextSection = findSectionInUpStation(downStation);
        while (!Objects.isNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionInUpStation(nextSection.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public Section findFirstSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(findSectionInDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Section findSectionInDownStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.hasSameDownStation(upStation))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionInUpStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(downStation))
                .findFirst()
                .orElse(null);
    }

    public boolean isFirstSection() {
        return sections.isEmpty();
    }

    public boolean isMatchWithUpStation(Section section) {
        return findStationInSections().stream().anyMatch(section::hasSameUpStation);
    }

    public boolean isMatchWithDownStation(Section section) {
        return findStationInSections().stream().anyMatch(section::hasSameDownStation);
    }
}
