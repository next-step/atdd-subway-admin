package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final String ALREADY_CONTAIONS_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.";
    private static final String NOT_CONTAIONS_ANY_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.";
    private static final String NOT_CONTAIONS_STATION_ERROR_MESSAGE = "노선에 등록되어있지 않은 역을 제거할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Stations allStations() {
        return sections.stream()
            .map(Section::getStations)
            .reduce(Stations::concatDistinct)
            .orElseGet(Stations::empty);
    }

    public void addSection(Section newSection) {
        Stations stations = this.allStations();
        validateAlreadyContainsAll(stations, newSection);
        validateNotContainsAny(stations, newSection);
        this.sections.forEach(section -> section.modify(newSection));
        this.sections.add(newSection);
    }

    private void validateNotContainsAny(Stations stations, Section newSection) {
        if (stations.isEmpty()) {
            return;
        }
        if (newSection.nonMatch(stations)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_ANY_ERROR_MESSAGE);
        }
    }

    private void validateAlreadyContainsAll(Stations stations, Section newSection) {
        if (stations.containsAll(newSection.getStations())) {
            throw new IllegalArgumentException(ALREADY_CONTAIONS_ERROR_MESSAGE);
        }
    }

    public void removeByStation(Station station) {
        validateNotContainsStation(station);
        validateOnlySection();

        Section upSection = findUpSection(station).orElse(null);
        Section downSection = findDownSection(station).orElse(null);

        combine(upSection, downSection);
    }

    private void combine(Section upSection, Section downSection) {
        if (Objects.isNull(upSection)) {
            this.sections.remove(downSection);
            return;
        }

        if (Objects.isNull(downSection)) {
            this.sections.remove(upSection);
            return;
        }
        this.sections.remove(upSection);
        this.sections.remove(downSection);
        addSection(upSection.merge(downSection));
    }

    private Optional<Section> findUpSection(Station station) {
        return this.sections.stream().filter(section -> section.hasDownStation(station))
            .findAny();
    }

    private Optional<Section> findDownSection(Station station) {
        return this.sections.stream().filter(section -> section.hasUpStation(station))
            .findAny();
    }

    private void validateOnlySection() {
        if (this.sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선의 역을 제거할 수 없습니다.");
        }
    }

    private void validateNotContainsStation(Station station) {
        if (this.allStations().notContains(station)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_STATION_ERROR_MESSAGE);
        }
    }

    public List<Section> getList() {
        return this.sections;
    }
}
