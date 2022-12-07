package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final String ERROR_MESSAGE_EXIST_STATION = "기존과 동일한 상행/하행선 등록 불가 합니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        isValidExistSection(section);
        sections.add(section);
    }

    private void isValidExistSection(Section section) {
        if (isContainsAllStation(section)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_EXIST_STATION);
        }
    }

    private boolean isContainsAllStation(Section section) {
        return sections.stream().map(Section::stations)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .containsAll(section.stations());
    }


    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}
