package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.InvalidSectionException;

@Embeddable
public class Sections {

    private static final String STATION_PAIR_BOTH_EXISTS = "상행역(%s)과 하행역(%s)이 모두 노선에 등록되어 있으면 구간을 추가할 수 없습니다.";
    private static final String STATION_PAIR_NONE_EXISTS = "상행역(%s), 하행역(%s) 중 최소한 하나의 역이 등록되어 있어야 합니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        validate(section);
        update(section);
        sections.add(section);
    }

    private void validate(Section section) {
        if (!isEmpty()) {
            validateDuplicate(section);
            validateExistence(section);
        }
    }

    private boolean isEmpty() {
        return sections.isEmpty();
    }

    private void validateDuplicate(Section target) {
        Optional<Section> optionalSection = sections.stream()
            .filter(element -> element.isSameStationPair(target))
            .findFirst();

        if (optionalSection.isPresent()) {
            throw new InvalidSectionException(
                String.format(STATION_PAIR_BOTH_EXISTS, target.getUpStation(),
                    target.getDownStation()));
        }
    }

    private void validateExistence(Section target) {
        if (!isExistStation(target.getDownStation())
            && !isExistStation(target.getUpStation())) {
            throw new InvalidSectionException(
                String.format(STATION_PAIR_NONE_EXISTS, target.getUpStation(),
                    target.getDownStation()));
        }
    }

    private boolean isExistStation(Station target) {
        Set<Station> stations = findAllStations();
        return stations.contains(target);
    }

    private Set<Station> findAllStations() {
        return sections.stream().
            flatMap(s -> s.getStationPair().stream()).
            collect(Collectors.toSet());
    }

    private void update(Section target) {
        for (Section section : sections) {
            section.update(target);
        }
    }

    public List<Station> stations() {
        return sections.stream()
            .map(Section::getStationPair)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

}
