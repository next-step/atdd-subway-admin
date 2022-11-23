package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {
    private static final String EXCEPTION_MESSAGE_FOR_INVALID_STATION = "유효하지 않은 역입니다.";
    private static final String EXCEPTION_MESSAGE_FOR_DUPLICATE_STATIONS = "이미 등록되어 있는 구간입니다.";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_FOUND_NEXT_STATION = "다음 구간이 없습니다.";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_FOUND_FIRST_STATION = "첫번째 역을 찾을 수 없습니다";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_of_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        validateSection(section);
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        updateWhenAddablePre(section);
        updateWhenAddablePost(section);
        checkContinuable(section);

        sections.add(section);
    }

    public List<Station> stationValues() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        return orderedBySection();
    }

    public List<Section> values() {
        return Collections.unmodifiableList(sections);
    }

    private void updateWhenAddablePre(Section section) {
        sections.stream()
                .filter(it -> section.getStation().isSame(it.getStation()))
                .findFirst()
                .ifPresent(it -> it.updatePreSection(section));
    }

    private void updateWhenAddablePost(Section section) {
        sections.stream()
                .filter(it -> section.getPreStation().isSame(it.getPreStation()))
                .findFirst()
                .ifPresent(it -> it.updateSection(section));
    }

    private void checkContinuable(Section section) {
        sections.stream()
                .filter(it -> section.getPreStation().isSame(it.getStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<Station> orderedBySection() {
        List<Station> stations = new ArrayList<>();
        Station station = findFirstStation();
        stations.add(station);

        while (isPresentNextStation(station)) {
            station = findNextStation(station);
            stations.add(station);
        }

        return Collections.unmodifiableList(stations);
    }

    private boolean isPresentNextStation(Station station) {
        return sections.stream()
                .filter(it -> !Objects.isNull(it.getPreStation()))
                .anyMatch(it -> it.getPreStation().isSame(station));
    }

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(it -> !Objects.isNull(it.getPreStation()))
                .filter(it -> it.getPreStation().isSame(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_NOT_FOUND_NEXT_STATION))
                .getStation();
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(it -> Objects.isNull(it.getPreStation()))
                .map(it -> it.getStation())
                .findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_NOT_FOUND_FIRST_STATION));
    }


    private void validateSection(Section section) {
        if (Objects.isNull(section.getStation())) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_INVALID_STATION);
        }
        if (sections.contains(section)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_DUPLICATE_STATIONS);
        }
    }
}
