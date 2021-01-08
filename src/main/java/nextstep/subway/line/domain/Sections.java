package nextstep.subway.line.domain;

import nextstep.subway.common.exception.CustomException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

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

    public Optional<Section> findDownSectionBy(Station baseStation) {
        return sections.stream()
                .filter(it -> it.getUpStation() == baseStation)
                .findFirst();
    }

    public Optional<Section> findUpSectionBy(Station baseStation) {
        return sections.stream()
                .filter(it -> it.getDownStation() == baseStation)
                .findFirst();
    }

    public List<Station> getStations() {
        Station station = findFirstUpStation();
        List<Station> result = new ArrayList<>(Collections.singletonList(station));
        Optional<Section> nextSection = findNextSection(station);
        while (nextSection.isPresent()) {
            Station nextStation = nextSection.get().getDownStation();
            result.add(nextStation);
            nextSection = findNextSection(nextStation);
        }
        return result;
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
                .noneMatch(section -> section.getDownStation() == upStation);
    }

    private Optional<Section> findNextSection(Station station) {
        return sections.stream()
                .filter(s -> s.getUpStation() == station)
                .findAny();
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
