package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.CannotRemoveException;
import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.NoSuchStationException;

@Embeddable
public class Sections {

    private static final int REDUCIBLE_COUNT = 2;
    private static final int SIZE_LOWER_LIMIT = 1;

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> values = new LinkedList<>();

    protected Sections() {
    }

    private Sections(List<Section> values) {
        this.values = values;
    }

    public List<Station> getStationsInOrder() {
        return SectionSorter.getStationsInOrder(values);
    }

    public boolean contains(Section section) {
        return values.contains(section);
    }

    public boolean isReducible() {
        return values.size() >= REDUCIBLE_COUNT;
    }

    public void addSection(Section section) {
        validateSection(section);

        updateSection(section);
        values.add(section);
    }

    private void updateSection(Section section) {
        values.stream()
            .filter(s -> s.matchesOnlyOneEndOf(section))
            .findAny()
            .ifPresent(oldSection -> {
                values.remove(oldSection);
                values.add(oldSection.shiftedBy(section));
            });
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 인 구간을 추가할 수는 없습니다.");
        }

        if (!values.isEmpty() && allOrNothingMatches(section)) {
            throw new InvalidSectionException("구간의 역은 오직 하나만 노선에 존재해야 합니다.");
        }
    }

    private boolean allOrNothingMatches(Section section) { // XOR Existence Check
        Set<Station> stations = getStations();
        return stations.contains(section.upStation())
                == stations.contains(section.downStation());
    }

    public void removeStation(Station station) {
        validateRemovable(station);

        Sections sections = new Sections(values.stream()
            .filter(s -> s.contains(station))
            .collect(toList()));

        reduceSections(sections);
    }

    private void reduceSections(Sections sections) {
        if (sections.isReducible()) {
            values.add(sections.reduce());
        }

        sections.values.forEach(values::remove);
    }

    private Section reduce() {
        return values.stream()
            .reduce(Section::mergeWith)
            .orElseThrow(() -> new CannotRemoveException("구간 축약에 실패했습니다."));
    }

    private void validateRemovable(Station station) {
        if (!getStations().contains(station)) {
            throw new NoSuchStationException("노선에 해당 지하철 역이 존재하지 않습니다.");
        }

        if (values.size() == SIZE_LOWER_LIMIT) {
            throw new CannotRemoveException("마지막 남은 구간은 삭제할 수 없습니다.");
        }
    }

    private Set<Station> getStations() {
        return values.stream()
            .flatMap(s -> Stream.of(s.upStation(), s.downStation()))
            .collect(toSet());
    }
}
