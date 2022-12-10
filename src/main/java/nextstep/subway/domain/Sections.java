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
    private static final String ERROR_MESSAGE_NOT_CONTAIN_STATION = "기존 등록된 상행/하행선이 하나도 포함되어 있지 않습니다.";

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

    public void addSection(Section newSection) {
        isValidExistSection(newSection);
        isValidNotContainSection(newSection);
        sections.stream()
                .filter(section -> section.isConnectable(newSection))
                .findFirst()
                .ifPresent(section -> section.reorganize(newSection));
        sections.add(newSection);
    }

    private void isValidNotContainSection(Section section) {
        if (!sections.isEmpty() && isNotContainSection(section)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_CONTAIN_STATION);
        }
    }

    private boolean isNotContainSection(Section section) {
        return getStations().stream()
                .noneMatch(station -> section.stations().contains(station));
    }


    private void isValidExistSection(Section section) {
        if (isContainsAllStation(section)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_EXIST_STATION);
        }
    }

    private boolean isContainsAllStation(Section section) {
        return getStations()
                .containsAll(section.stations());
    }

    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}
