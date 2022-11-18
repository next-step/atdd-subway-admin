package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void addSection(Section newSection) {
        validateNotMatchedStaton(newSection);
        validateDistance(newSection);
        validateDuplicated(newSection);

        changeSections(newSection);
        sections.add(newSection);
    }


    private void validateNotMatchedStaton(Section newSection) {
        // 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        boolean anyMatch = sections.stream()
                .anyMatch(s -> s.isContainAnyStaion(newSection));

        if (!anyMatch) {
            throw new IllegalArgumentException(NOT_VALID_ANY_STATION);
        }
    }

    private void validateDistance(Section newSection) {
        // 두번째 추가이거나 종점 추가일 경우 길이 체크x
        if(sections.size() == 1 || newSection.isLastStation(sections)) {
            return;
        }

        // 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        Optional<Section> invalidSections = sections.stream()
                .filter(s -> s.getDistance() <= newSection.getDistance())
                .findFirst();

        if (invalidSections.isPresent()) {
            throw new IllegalArgumentException(NOT_VALID_SECTION_DISTANCE);
        }
    }


    private void validateDuplicated(Section section) {
        Optional<Section> duplicate = sections.stream()
                .filter(s -> s.isContainSameStations(section))
                .findFirst();

        if (duplicate.isPresent()) {
            throw new IllegalArgumentException(NOT_VALID_DUPLICATED_SECTION_STATIONS);
        }
    }

    private void changeSections(Section newSection) {
        // upStation change
        sections.stream()
                .filter(s -> s.getUpStation().equals(newSection.getUpStation()))
                .findAny()
                .ifPresent(s -> s.changeUpStation(newSection));
    }

    public List<Station> getAllStations() {
        return sections.stream()
                .flatMap(Section::stations)// Stream<Station> return
                .distinct()
                .collect(Collectors.toList());
    }
}
