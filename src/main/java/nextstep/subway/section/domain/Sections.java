package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.section.exception.NotExistStationOnLineException;
import nextstep.subway.section.exception.OnlyOneSectionExistException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    public static final String SECTIONS_CANNOT_BE_NULL = "구간목록은 NULL이 될수 없습니다.";
    public static final String SECTION_ALREADY_EXISTS = "이미 상행역과 하행역으로 연결되는 구간이 등록되어 있습니다.";
    public static final String THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS = "상행역과 하행역 둘중 포함되는 역이 없습니다.";
    public static final int ZERO = 0;
    public static final int ONE = 1;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        if (sections == null) {
            throw new IllegalArgumentException(SECTIONS_CANNOT_BE_NULL);
        }
        this.sections = sections;
    }

    public boolean add(Section newSection) {
        if (!sections.isEmpty()) {
            validationForAdd(newSection);
        }
        changeSectionWhenUpStationMatch(newSection);
        changeSectionWhenDownStationMatch(newSection);
        return sections.add(newSection);
    }

    private void changeSectionWhenUpStationMatch(Section newSection) {
        sections.stream()
            .filter(section -> section.isEqualUpStation(newSection))
            .findFirst()
            .ifPresent(section -> {
                section.updateUpStationTo(newSection.getDownStation());
                section.minusDistance(newSection.getDistance());
            });
    }

    private void changeSectionWhenDownStationMatch(Section newSection) {
        sections.stream()
            .filter(section -> section.isEqualDownStation(newSection))
            .findFirst()
            .ifPresent(section -> {
                section.updateDownStationTo(newSection.getUpStation());
                section.minusDistance(newSection.getDistance());
            });
    }

    private void validationForAdd(Section section) {
        if (isPresent(section)) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXISTS);
        }
        if (!isPresentAnyStation(section)) {
            throw new IllegalArgumentException(THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS);
        }
    }

    private boolean isPresent(Section section) {
        return sections.stream()
            .anyMatch(s -> s.isEqualAllStation(section));
    }

    private boolean isPresentAnyStation(Section section) {
        return sections.stream()
            .anyMatch(s -> s.isPresentAnyStation(section));
    }

    public List<Station> findStationsInOrder() {
        List<Station> results = new ArrayList<>();

        //첫번째 구간 찾기
        if (findFirstSection().isPresent()) {
            Section firstSection = findFirstSection().get();
            List<Section> sortSections = new ArrayList<>(Arrays.asList(firstSection));

            //재귀호출하여 구간 이어붙히기
            recursiveSort(sortSections, firstSection);

            //정렬된 역 목록 만들어서 반환하기
            results.add(firstSection.getUpStation());
            results.addAll(getDownStations(sortSections));
        }
        return results;
    }

    private void recursiveSort(List<Section> sortSections, Section beforeSection) {
        this.sections.stream()
            .filter(section -> section.isAfter(beforeSection))
            .findFirst()
            .ifPresent(section -> {
                //뒤에 붙히기
                sortSections.add(section);

                //재귀호출
                recursiveSort(sortSections, section);
            });
    }

    private Optional<Section> findFirstSection() {
        return sections.stream()
            .filter(this::notExistPrevious)
            .findFirst();
    }

    private boolean notExistPrevious(Section dest) {
        return sections.stream()
            .noneMatch(section -> section.isBefore(dest));
    }

    private List<Station> getDownStations(List<Section> list) {
        return list.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    public void delete(Station station) {
        validationNotExistStationOnLine(station);
        validationOnlyOneSectionExists();

        List<Section> foundSections = findSectionsContainStation(station);
        if (foundSections.size() == ONE) {
            sections.remove(foundSections.get(ZERO));
        }
        if (foundSections.size() > ONE) {
            deleteMiddleStation(foundSections, station);
        }
    }

    private void validationOnlyOneSectionExists() {
        if (sections.size() == ONE) {
            throw new OnlyOneSectionExistException();
        }
    }

    private void validationNotExistStationOnLine(Station station) {
        sections.stream()
            .filter(section -> section.contain(station))
            .findFirst().orElseThrow(NotExistStationOnLineException::new);
    }

    private List<Section> findSectionsContainStation(Station station) {
        return sections.stream()
            .filter(section -> section.contain(station))
            .collect(Collectors.toList());
    }

    private void deleteMiddleStation(List<Section> foundSections, Station station) {
        Optional<Section> updateSectionOptional = findUpdateSection(foundSections, station);
        Optional<Section> removeSectionOptional = findRemoveSection(foundSections, station);
        if (updateSectionOptional.isPresent() && removeSectionOptional.isPresent()) {
            Section updateSection = updateSectionOptional.get();
            Section removeSection = removeSectionOptional.get();
            updateSection.updateDownStationTo(removeSection.getDownStation());
            updateSection.plusDistance(removeSection.getDistance());
            sections.remove(removeSection);
        }
    }

    private Optional<Section> findUpdateSection(List<Section> foundSections, Station station) {
        return foundSections.stream()
            .filter(section -> station.equals(section.getDownStation()))
            .findFirst();
    }

    private Optional<Section> findRemoveSection(List<Section> foundSections, Station station) {
        return foundSections.stream()
            .filter(section -> station.equals(section.getUpStation()))
            .findFirst();
    }
}
