package nextstep.subway.line.domain;

import nextstep.subway.common.exception.SectionNotCreateException;
import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
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

    public void add(Section section) {
        if (!sections.isEmpty()) {
            validate(section);
            updateSection(section);
        }
        sections.add(section);
    }

    private void updateSection(Section section) {
        if(getStations().contains(section.getStation())) {
            sections.stream()
                    .filter(it -> it.isSameStation(section))
                    .findFirst()
                    .ifPresent(it -> it.updateStation(section));
            return;
        }
        if(getStations().contains(section.getNextStation())) {
            sections.stream()
                    .filter(it -> it.isSameNextStation(section))
                    .findFirst()
                    .ifPresent(it -> it.updateNextStation(section));
            return;
        }
    }

    public List<Station> getStations() {
        return getOrderedStations(findFirstStation());
    }

    private void validate(Section section) {
        validateDistance(section);
        validateDuplicate(section);
        validateExist(section);
    }

    private void validateExist(Section section) {
        if (notExistStation(section)) {
            throw new SectionNotCreateException("구간에 역이 존재하지 않습니다.");
        }
    }

    private void validateDuplicate(Section section) {
        if (sections.stream().allMatch(it -> it.isDuplicate(section))) {
            throw new SectionNotCreateException("이미 등록된 구간입니다.");
        }
    }

    private void validateDistance(Section section) {
        findSection(section).ifPresent(it -> {
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
        return !getStations().contains(section.getStation()) && !getStations().contains(section.getNextStation());
    }

    private Station findNextStation(Station station) {
        return sections.stream().filter(section -> section.getStation().equals(station))
                .findFirst()
                .orElse(new Section())
                .getNextStation();
    }

    private List<Station> getUpStations() {
        return sections.stream().map(Section::getStation).collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream().map(Section::getNextStation).collect(Collectors.toList());
    }

    private Optional<Section> findSection(Section section) {
        return sections.stream()
                .filter(it -> it.getStation().equals(section.getStation()))
                .findFirst();
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(it -> !getDownStations().contains(it.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getStation();
    }
}
