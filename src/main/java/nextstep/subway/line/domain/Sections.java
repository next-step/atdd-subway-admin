package nextstep.subway.line.domain;

import nextstep.subway.common.exception.SectionNotCreateException;
import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : Sections
 * author : haedoang
 * date : 2021/11/23
 * description : Sections 일급 컬렉션
 */
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addSection) {
        validate(addSection);
        insertSection(addSection);
        sections.add(addSection);
    }

    public void delete(Station station) {
        getSection(station).ifPresent(
                it -> {
                    if (it.isSameStation(station)) {
                        findPrevSection(station).ifPresent(
                                that -> that.removeSection(it)
                        );
                    }
                    sections.remove(it);
                }
        );
    }


    public List<Station> getStations() {
        return getOrderedStations(findFirstStation());
    }

    private void insertSection(Section addSection) {
        getSection(addSection).ifPresent(
                section -> section.addSection(addSection)
        );
    }

    private void validate(Section section) {
        if (!sections.isEmpty()) {
            validateDistance(section);
            validateDuplicate(section);
            validateExist(section);
        }
    }

    private void validateExist(Section section) {
        if (notExistStation(section)) {
            throw new SectionNotCreateException("구간에 역이 존재하지 않습니다.");
        }
    }

    private void validateDuplicate(Section section) {
        if (sections.stream().allMatch(it -> it.isSameSection(section))) {
            throw new SectionNotCreateException("이미 등록된 구간입니다.");
        }
    }

    private void validateDistance(Section section) {
        getSection(section).ifPresent(it -> {
            if (!it.isPermitDistance(section.getDistance())) {
                throw new SectionNotCreateException("유효한 길이가 아닙니다.");
            }
        });
    }

    private List<Station> getOrderedStations(Station station) {
        List<Station> result = new ArrayList<>();
        while (Optional.ofNullable(station).isPresent()) {
            result.add(station);
            station = findNextStation(station);
        }
        return result;
    }

    private boolean notExistStation(Section section) {
        return getStations().stream()
                .noneMatch(station -> station.equals(section.getStation()) || station.equals(section.getNextStation()));
    }

    private Station findNextStation(Station station) {
        return sections.stream().filter(section -> section.getStation().equals(station))
                .findFirst()
                .orElse(new Section())
                .getNextStation();
    }

    private List<Station> getDownStations() {
        return sections.stream().map(Section::getNextStation).collect(Collectors.toList());
    }

    private Optional<Section> getSection(Section target) {
        return sections.stream()
                .filter(section -> section.isSameStation(target) || section.isSameNextStation(target))
                .findFirst();
    }

    private Optional<Section> getSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasStation(station))
                .findFirst();
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(section -> !getDownStations().contains(section.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getStation();
    }

    private Optional<Section> findPrevSection(Station station) {
        return sections.stream()
                .filter(it -> it.isSameNextStation(station))
                .findFirst();
    }

//    private List<Station> getUpStations() {
//        return sections.stream().map(Section::getStation).collect(Collectors.toList());
//    }

//    private Optional<Section> findSection(Section findSection) {
//        return sections.stream()
//                .filter(section -> section.getStation().equals(findSection.getStation()))
//                .findFirst();
//    }

//    private Optional<Section> getSectionByStation(Section target) {
//        return sections.stream()
//                .filter(section -> section.isSameStation(target))
//                .findFirst();
//    }
//
//    private Optional<Section> getSectionByNextStation(Section target) {
//        return sections.stream()
//                .filter(section -> section.isSameNextStation(target))
//                .findFirst();
//    }
}
