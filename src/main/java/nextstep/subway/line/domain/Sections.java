package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.SectionConverter.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final long NO_MATCH = 0L;
    private static final long ALL_MATCH = 2L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new LinkedList<>();

    protected Sections() {
    }

    public List<Station> getStationsInAscending() {
        return stationsInOrder(values);
    }

    public boolean contains(Section section) {
        return values.contains(section);
    }

    public void addSection(Section section) {
        validateSection(section);

        if (values.isEmpty()) {
            values.add(section);
            return;
        }

        // TODO merge
        validateStations(section);
        values.add(section);
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 인 구간을 추가할 수는 없습니다.");
        }
    }

    private void validateStations(Section section) {
        long matchCount = containsCount(section);
        if (matchCount == NO_MATCH) {
            throw new InvalidSectionException("연결할 역이 없습니다.");
        }

        if (matchCount == ALL_MATCH) {
            throw new InvalidSectionException("두 역은 이미 연결되어 있습니다.");
        }
    }

    private long containsCount(Section section) {
        Set<Station> stations = getStations(values);
        return section.getStations()
            .stream()
            .filter(stations::contains)
            .count();
    }
}
