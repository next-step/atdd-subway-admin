package nextstep.subway.line.domain;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections(Section section) {
        sections.add(section);
    }

    protected Sections() {
    }

    public void add(Section section) {
        validateSection(section);
        updateSection(section);
    }

    public List<Station> getStations() {
        Station station = findFirstUpStation();
        List<Station> result = new ArrayList<>(Collections.singletonList(station));
        Optional<Section> nextSection = findDownSectionBy(station);
        while (nextSection.isPresent()) {
            Station nextStation = nextSection.get().getDownStation();
            result.add(nextStation);
            nextSection = findDownSectionBy(nextStation);
        }
        return result;
    }

    public void deleteStation(Station station) {
        if (hasOneOrEmptySection()) {
            throw new CustomException("구간이 1개이면 역을 삭제할 수 없습니다.");
        }
        Optional<Section> upSection = findUpSectionBy(station);
        Optional<Section> downSection = findDownSectionBy(station);
        if (upSection.isPresent() && downSection.isPresent()) {
            sections.add(upSection.get().merge(downSection.get()));
        }
        upSection.ifPresent(this::remove);
        downSection.ifPresent(this::remove);
    }

    private boolean hasOneOrEmptySection() {
        return sections.size() <= 1;
    }

    private void remove(Section section) {
        sections.remove(section);
    }

    private Optional<Section> findDownSectionBy(Station baseStation) {
        return findSectionBy(s -> s.hasDownSection(baseStation));
    }

    private Optional<Section> findUpSectionBy(Station baseStation) {
        return findSectionBy(s -> s.hasUpSection(baseStation));
    }

    private Optional<Section> findSectionBy(Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findFirst();
    }

    private Station findFirstUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::matchUpStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행역이 존재하지 않습니다."));
    }

    private boolean matchUpStation(Station upStation) {
        return sections.stream()
                .noneMatch(section -> section.hasUpSection(upStation));
    }

    private void updateSection(Section section) {
        findDownSectionBy(section.getUpStation())
                .ifPresent(it -> it.updateUpStation(section));
        findUpSectionBy(section.getDownStation())
                .ifPresent(it -> it.updateDownStation(section));
        sections.add(section);
    }

    private void validateSection(Section section) {
        List<Station> stations = getStations();
        if (isAlreadyRegistered(section, stations)) {
            throw new CustomException("이미 등록된 구간 입니다.");
        }
        if (isNotRegistered(section)) {
            throw new CustomException("등록할 수 없는 구간 입니다.");
        }
    }

    private boolean isAlreadyRegistered(Section section, List<Station> stations) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean isNotRegistered(Section section) {
        List<Station> stations = getStations();
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }
}
