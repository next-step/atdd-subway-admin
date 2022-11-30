package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    public void add(Section section) {
        ifContainsStationAdd(section);
        sections.add(section);
    }

    private void ifContainsStationAdd(Section newSection) {
        sections.stream()
                .filter(section -> section.containsStation(newSection))
                .findFirst()
                .ifPresent(section -> section.swapStation(newSection));
    }



    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());

    }

    public void removeStation(Station station) {
        Optional<Section> upSection = findUpStation(station);
        Optional<Section> downSection = findDownStation(station);
        validateSections(upSection, downSection);

        if(checkAndRemoveMiddleSection(upSection, downSection)) return;

        upSection.ifPresent(this::removeSection);
        downSection.ifPresent(this::removeSection);
    }
    private boolean checkAndRemoveMiddleSection(Optional<Section> upSection, Optional<Section> downSection){
        if(upSection.isPresent() && downSection.isPresent()){
            downSection.get().union(upSection.get());
            removeSection(upSection.get());
            return true;
        }
        return false;
    }

    private void removeSection(Section section) {
        sections.remove(section);
    }

    private Optional<Section> findUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst();
    }

    private Optional<Section> findDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isDownStation(station))
                .findFirst();
    }

    public void validateSections(Optional<Section> upSection, Optional<Section> downSection) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }

        if (!upSection.isPresent() && !downSection.isPresent()) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역은 제거할 수 없습니다.");
        }
    }
}
