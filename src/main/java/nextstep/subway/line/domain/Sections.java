package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lineId")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        validateAlreadyContainsAll(newSection);
        validateNotContainsAny(newSection);
        sections.stream()
            .filter(section -> section.isConnectable(newSection))
            .findFirst()
            .ifPresent(section -> section.relocate(newSection));
        sections.add(newSection);
    }

    public void removeSectionByStation(Station station) {
        validateSectionsSize();
        validateNotContainsStation(station);

        Optional<Section> removedUpSection = removeUpSection(station);
        Optional<Section> removedDownSection = removeDownSection(station);

        if (removedUpSection.isPresent() && removedDownSection.isPresent()) {
            this.add(mergeSection(removedUpSection.get(), removedDownSection.get()));
        }
    }

    private void validateSectionsSize() {
        if (sections.size() <= 1) {
            throw new IllegalStateException("더 이상 구간을 제거할 수 없습니다.");
        }
    }

    private Optional<Section> removeUpSection(Station station) {
        Optional<Section> upSection = this.sections.stream()
            .filter(section -> section.hasDownStation(station))
            .findAny();
        upSection.ifPresent(section -> this.sections.remove(section));
        return upSection;
    }

    private Optional<Section> removeDownSection(Station station) {
        Optional<Section> downSection = this.sections.stream()
            .filter(section -> section.hasUpStation(station))
            .findAny();
        downSection.ifPresent(section -> this.sections.remove(section));
        return downSection;
    }

    private Section mergeSection(Section upSection, Section downSection) {
        return upSection.merge(downSection);
    }

    private void validateNotContainsAny(Section section) {
        List<Station> stations = this.getStations();
        if (stations.isEmpty()) {
            return;
        }
        if (section.getStations().stream().noneMatch(stations::contains)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void validateAlreadyContainsAll(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public void validateNotContainsStation(Station station) {
        if (!this.getStations().contains(station)) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역입니다.");
        }
    }
}
