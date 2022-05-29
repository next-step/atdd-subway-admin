package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            addValidate(section);
            updateSection(section);
        }
        sections.add(section);
    }

    public void removeSection(Station station) {
        removeValidate(station);

        Section sectionIncludeUpStation = sections.stream()
                .filter(section -> section.getUpStation().equals(station)).findFirst().get();
        Section sectionIncludeDownStation = sections.stream()
                .filter(section -> section.getDownStation().equals(station)).findFirst().get();

        sections.remove(sectionIncludeDownStation);
        sections.remove(sectionIncludeUpStation);

        sections.add(
                Section.of(sectionIncludeDownStation.getUpStation(),
                        sectionIncludeUpStation.getDownStation(),
                        sectionIncludeUpStation.mergeDistance(sectionIncludeDownStation))
        );
    }

    public Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    private void addValidate(Section section) {
        Set<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("구간이 이미 등록되어 있습니다.");
        }
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 포함되어있어야 합니다.");
        }
    }

    private void removeValidate(Station station) {
        if (!hasStation(station)) {
            throw new IllegalArgumentException("노선에 등록되지 않는 역입니다.");
        }
        if (sections.size() < 2) {
            throw new IllegalArgumentException("구간이 1개이하인 노선은 삭제할수 없습니다.");
        }
    }

    private void updateSection(Section newSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateByUpSection(newSection));

        sections.stream()
                .filter(it -> it.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateByDownSection(newSection));
    }

    private boolean hasStation(Station station) {
        return sections.stream().anyMatch(section ->
                section.getDownStation().equals(station) || section.getUpStation().equals(station));
    }
}
