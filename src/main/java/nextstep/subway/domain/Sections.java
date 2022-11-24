package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Embeddable
public class Sections {
    private static final String EXCEPTION_MESSAGE_FOR_INVALID_STATION = "유효하지 않은 역입니다.";
    private static final String EXCEPTION_MESSAGE_FOR_DUPLICATE_STATIONS = "이미 등록되어 있는 구간입니다.";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_FOUND_NEXT_STATION = "다음 구간이 없습니다.";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_FOUND_PRE_STATION = "이전 구간이 없습니다.";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_FOUND_FIRST_STATION = "첫번째 역을 찾을 수 없습니다";
    private static final int MIN_SIZE_OF_SECTIONS = 3;
    private static final String EXCEPTION_MESSAGE_FOR_NOT_REMOVABLE_LINE_STATE = "구간은 최소" + (MIN_SIZE_OF_SECTIONS - 2) + "개의 노선을 가지고 있어야 합니다!";
    private static final String EXCEPTION_MESSAGE_FOR_NOT_EXIST_SECTIONS = "노선에 존재하지 않는 역입니다.";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_of_line"))
    private List<Section> sections = new CopyOnWriteArrayList<>();

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

    public void remove(Station station) {
        validateRemovableStation(station);

        Section section = findSection(station);

        if (isPresentNextStation(station)) {
            Section nextSection = findNextSection(station);
            nextSection.removeSection(section);
        }

        sections.remove(section);
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
        Station station = findFirstStation().getStation();
        stations.add(station);

        while (isPresentNextStation(station)) {
            station = findNextSection(station).getStation();
            stations.add(station);
        }

        return Collections.unmodifiableList(stations);
    }

    private boolean isPresentStation(Station station) {
        return sections.stream()
                .anyMatch(it -> it.getStation().isSame(station));
    }

    private boolean isPresentNextStation(Station station) {
        return sections.stream()
                .filter(it -> !Objects.isNull(it.getPreStation()))
                .anyMatch(it -> it.getPreStation().isSame(station));
    }

    private Section findNextSection(Station station) {
        return sections.stream()
                .filter(it -> !Objects.isNull(it.getPreStation()))
                .filter(it -> it.getPreStation().isSame(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_NOT_FOUND_NEXT_STATION));
    }

    private Section findSection(Station station) {
        return sections.stream()
                .filter(it -> it.getStation().isSame(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_NOT_FOUND_PRE_STATION));
    }

    private Section findFirstStation() {
        return sections.stream()
                .filter(it -> Objects.isNull(it.getPreStation()))
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

    private void validateRemovableStation(Station station) {
        if (sections.size() < MIN_SIZE_OF_SECTIONS) {
            throw new IllegalStateException(EXCEPTION_MESSAGE_FOR_NOT_REMOVABLE_LINE_STATE);
        }
        if (!isPresentStation(station)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_NOT_EXIST_SECTIONS);
        }
    }
}
