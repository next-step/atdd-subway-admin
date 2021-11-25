package nextstep.subway.line.domain;

import nextstep.subway.line.application.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        validateDuplication(section);
        this.sections.add(section);
    }

    private void validateDuplication(Section section) {
        List<Station> downStations = getDownStations();
        if (downStations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("중복되는 노선이 있습니다.");
        }
    }

    private void sortSections() {
        List<Section> orderedSections = new LinkedList<>();
        Section orderedSection = findFirstSection();
        orderedSections.add(orderedSection);

        int i = orderedSections.size();
        while (i < sections.size()) {
            orderedSection = findNextStation(orderedSection.getDownStation());
            orderedSections.add(orderedSection);
            i++;
        }
    }

    public Section findFirstSection() {
        List<Station> downStations = getDownStations();

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SectionNotFoundException("상행 종점 구간을 찾을 수 없습니다."));
    }

    private Section findNextStation(Station orderedDownStation) {
        return sections.stream()
                .filter(section -> orderedDownStation.equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SectionNotFoundException("구간이 끊어져 있습니다."));
    }

    public List<Station> getDownStations() {
        return Collections.unmodifiableList(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
    }

    public List<Section> getSections() {
        sortSections();
        return sections;
    }
}
