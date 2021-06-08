package nextstep.subway.section.domain;

import nextstep.subway.section.application.SectionDuplicatedException;
import nextstep.subway.section.application.StationNotRegisterException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    private static final String SECTION_DUPLICATE = "이미 등록된 구간입니다.";
    public static final String NOT_REGISTERED_STATION = "주어진 역을 포함하지 않아 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new LinkedList<>();
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }
        validate(section);
        LinkedList<Section> result = new LinkedList(sections);
        if (isFirstSectionNext(section, result.getFirst()) || isFirstSectionBefore(section, result.getFirst())) {
            addToFirstSection(section, result, result.getFirst());
            return;
        }
        if (isLastSectionNext(section, result.getLast())) {
            addToLastSectionNext(section, result, result.getLast());
            return;
        }
        if (isLastSectionBefore(section, result.getLast())) {
            addToLastSectionBefore(section, result, result.getLast());
            return;
        }
        addToMiddle(section, result);
        sections = new ArrayList<>(result);
    }

    private void addToMiddle(Section section, LinkedList<Section> temp) {
        changeUpStation(section);
        changeDownStation(section);
        temp.add(section);
    }

    private boolean isLastSectionBefore(Section section, Section last) {
        return last.getDownStation().equals(section.getDownStation());
    }

    private void addToLastSectionBefore(Section section, LinkedList<Section> temp, Section last) {
        temp.addLast(new Section(last));
        last.changeUpStation(section);
        sections = new ArrayList<>(temp);
    }

    private void addToLastSectionNext(Section section, LinkedList<Section> temp, Section last) {
        last.changeDownStation(section);
        temp.addLast(section);
        sections = new ArrayList<>(temp);
    }

    private boolean isLastSectionNext(Section section, Section last) {
        return last.getDownStation().equals(section.getUpStation());
    }

    private void addToFirstSection(Section section, LinkedList<Section> temp, Section first) {
        first.changeUpStation(section);
        temp.addFirst(section);
        sections = new ArrayList<>(temp);
    }

    private boolean isFirstSectionBefore(Section section, Section first) {
        return first.getUpStation().equals(section.getDownStation());
    }

    private boolean isFirstSectionNext(Section section, Section first) {
        return first.getUpStation().equals(section.getUpStation());
    }

    private void validate(Section section) {
        if (checkSectionDuplicated(section)) {
            throw new SectionDuplicatedException(SECTION_DUPLICATE);
        }
        if (checkSectionStationExisted(section)) {
            throw new StationNotRegisterException(NOT_REGISTERED_STATION);
        }
    }

    private boolean checkSectionDuplicated(Section section) {
        return sections.stream()
                .allMatch(section::containsAllStations);
    }

    private boolean checkSectionStationExisted(Section section) {
        return sections.stream()
                .allMatch(section::containsNoneStations);
    }

    private void changeDownStation(Section section) {
        sections.stream()
                .filter(v -> v.getDownStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(v -> v.changeDownStation(section));
    }

    private void changeUpStation(Section section) {
        sections.stream()
                .filter(v -> v.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(v -> v.changeUpStation(section));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        LinkedList<Section> result = new LinkedList<>();
        Deque<Section> queue = new LinkedList<>(sections);
        result.add(queue.pop());

        while (!queue.isEmpty()) {
            Section section = queue.pop();

            result.stream()
                    .map(Section::getUpStation)
                    .filter(v -> Objects.nonNull(section.getDownStation()) && v.equals(section.getDownStation()))
                    .findFirst()
                    .ifPresent(
                            v -> result.addFirst(section)
                    );
            result.stream()
                    .map(Section::getDownStation)
                    .filter(v -> Objects.nonNull(section.getUpStation()) && v.equals(section.getUpStation()))
                    .findFirst()
                    .ifPresent(
                            v -> result.addLast(section)
                    );
        }
        return collectStations(result);
    }

    private List<Station> collectStations(LinkedList<Section> result) {
        return result.stream().
                flatMap(Section::getProcessStations)
                .distinct()
                .collect(toList());
    }
}
