package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.SectionConverter.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new LinkedList<>();

    protected Sections() {
    }

    public List<Station> getStationsInAscending() {
        return getStationsInOrder(values);
    }

    public boolean contains(Section section) {
        return values.contains(section);
    }

    public void addSection(Section section) {
        validateSection(section);

        updateSection(section);
        values.add(section);
    }

    private void updateSection(Section section) {
        values.stream()
            .filter(s -> s.mergeable(section))
            .findAny()
            .ifPresent(oldSection -> {
                values.remove(oldSection);
                values.add(oldSection.reducedBy(section));
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
        Set<Station> stations = getStations(values);
        return stations.contains(section.upStation())
                == stations.contains(section.downStation());
    }
}
