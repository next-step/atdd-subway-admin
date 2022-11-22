package nextstep.subway.domain;

import com.google.common.collect.Lists;
import javassist.tools.web.BadHttpRequest;
import nextstep.subway.exception.DataRemoveException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.*;

@Embeddable
public class Sections {
    private static final int SECTIONS_MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        return new Sections(Lists.newArrayList(section));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void addSection(Section newSection) {
        validateNotMatchedStation(newSection);
        validateDistance(newSection);
        validateDuplicated(newSection);

        changeSections(newSection);
        sections.add(newSection);
    }

    public void removeSection(Station station) {
        validateContainStation(station);
        validateOnlyOneSection();

        deleteSection(station);
    }

    private void deleteSection(Station station) {
        // 순서대로 정렬
        List<Section> sortedSections = sortSections();

        // 종점 삭제
        if (isLastStation(sortedSections, station)) {
            removeLastSection(sortedSections, station);
            return;
        }

        // 중간 삭제
        removeNotLastSection(station);
    }


    private void changeSectionsAndRemove(Station station, Section deleteSection) {
        Section downSection = sections.stream()
                .filter(u -> u.getUpStation().equals(station))
                .findFirst()
                .get();
        downSection.changeDownStation(deleteSection);

        // section-line 매핑 끊기
        deleteSection.removeFromLine();
        sections.remove(deleteSection);

    }
    

    public List<Station> getAllStations() {
        return sections.stream()
                .flatMap(Section::stations)// Stream<Station> return
                .distinct()
                .collect(Collectors.toList());
    }

    private void deleteSection(Section section) {
        // section-line 매핑 끊기
        section.removeFromLine();
        sections.remove(section);
    }

    private boolean isLastStation(List<Section> sortedSections, Station targetStation) {
        Section firstSection = sortedSections.get(0);
        Section lastSection = sortedSections.get(sortedSections.size() - 1);
        return firstSection.getUpStation().equals(targetStation) || lastSection.getDownStation().equals(targetStation);
    }

    private Section findLastStation(Section firstSection, Section lastSection, Station targetStation) {
        // 상행 종점
        if (firstSection.getUpStation().equals(targetStation)) {
            return firstSection;
        }

        if (lastSection.getDownStation().equals(targetStation)) {
            return lastSection;
        }

        return null;
    }

    private void validateContainStation(Station targetStation) {
        Optional<Section> invalidSections = sections.stream()
                .filter(s -> s.isIncludedSection(targetStation))
                .findAny();

        if (!invalidSections.isPresent()) {
            throw new IllegalArgumentException(NOT_CONTAIN_STATION_IN_LINE);
        }
    }

    private void validateOnlyOneSection() {
        if (sections.size() == SECTIONS_MIN_SIZE) {
            throw new DataRemoveException(NOT_VALID_REMOVE_ONLY_ONE_SECTION);
        }
    }


    private void validateNotMatchedStation(Section newSection) {
        // 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        boolean anyMatch = sections.stream()
                .anyMatch(s -> s.isContainAnyStaion(newSection));

        if (!anyMatch) {
            throw new IllegalArgumentException(NOT_VALID_ANY_STATION);
        }
    }

    private void validateDistance(Section newSection) {
        // 두번째 추가이거나 종점 추가일 경우 길이 체크x
        if (sections.size() == 1 || newSection.isLastStation(sections)) {
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

    private List<Section> sortSections() {
        return sections.stream()
                .sorted(Section::compareTo)
                .collect(Collectors.toList());
    }

    private void removeLastSection(List<Section> sortedSections, Station station) {
        Section targetSection = sortedSections
                .stream()
                .map(section -> findLastStation(sortedSections.get(0), sortedSections.get(sortedSections.size() - 1), station))
                .findAny()
                .get();

        deleteSection(targetSection);
    }

    private void removeNotLastSection(Station station) {
        sections.stream()
                .filter(s -> s.getDownStation().equals(station))
                .findAny()
                .ifPresent(s -> changeSectionsAndRemove(station, s));
    }
}
