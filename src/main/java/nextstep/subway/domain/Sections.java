package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    public Sections(Section ascendEndpoint, Section descendEndpoint) {
        this.sections.add(ascendEndpoint);
        this.sections.add(descendEndpoint);
    }

    public List<Section> getAll() {
        return sections.stream().sorted().collect(Collectors.toList());
    }

    public void addSection(Line line, Station preStation, Station station, Integer distance) {
        checkSame(preStation, station);
        checkAlreadyAdded(preStation, station);

        if (isAscendEndpoint(preStation)) {
            changeAscendEndpoint(line, station, distance);
            return;
        }

        if (isDescendEndpoint(preStation)) {
            changeDescendEndpoint(line, station, distance);
            return;
        }

        addBetween(line, preStation, station, distance);
    }

    private Section getAscendEndpoint() {
        return getAll().stream().filter(it -> it.getPreStation() == null).findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Section getDescendEndpoint() {
        List<Section> sections = getAll();
        return sections.get(sections.size() - 1);
    }

    private void checkSame(Station preStation, Station station) {
        if (Objects.equals(preStation, station)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkAlreadyAdded(Station preStation, Station station) {
        if (getAll().stream().anyMatch(section -> Objects.equals(section.getStation(), station) && Objects.equals(section.getPreStation(), preStation))) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private boolean isAscendEndpoint(Station station) {
        return Objects.equals(station, null);
    }

    private boolean isDescendEndpoint(Station station) {
        return Objects.equals(getDescendEndpoint().getStation(), station);
    }

    private void addBetween(Line line, Station preStation, Station station, Integer distance) {
        Section startSection = getAll().stream().filter(it -> Objects.equals(preStation, it.getStation())).findFirst()
                .orElseThrow(IllegalAccessError::new);
        Section endSection = getAll().get(getAll().indexOf(startSection) + 1);

        if (endSection.getDistance() - startSection.getDistance() <= distance) {
            throw new IllegalArgumentException("distance 는 기존 역 구간 내에 속할 수 있는 값이어야 합니다.");
        }

        endSection.updatePreStation(station);
        this.sections.add(Section.of(line, station, preStation, startSection.getDistance() + distance));
    }

    private void changeAscendEndpoint(Line line, Station station, Integer distance) {
        for (Section section : getAll()) {
            section.updateDuration(section.getDistance() + distance);
        }

        Section section = getAscendEndpoint();
        section.updatePreStation(station);

        this.sections.add(Section.ascendEndPoint(line, station));
    }

    private void changeDescendEndpoint(Line line, Station station, Integer distance) {
        Section section = getDescendEndpoint();
        this.sections.add(Section.of(line, station, section.getStation(),
                section.getDistance() + distance));
    }
}
