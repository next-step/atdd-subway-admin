package nextstep.subway.line.domain;

import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * packageName : nextstep.subway.line.domain
 * fileName : Sections
 * author : haedoang
 * date : 2021/11/23
 * description :
 */
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        //존재하는지
        sections.stream()
                .filter(it -> it.getStation().equals(section.getStation()))
                .findFirst()
                .ifPresent(it -> it.updateStation(section.getNextStation(), section.getDistance()));

        this.sections.add(section);
    }

    public List<Station> getStations() {
        Station firstStation = findFirstStation();
        return getOrderedStations(firstStation);
    }

    private List<Station> getOrderedStations(Station station) {
        List<Station> result = new ArrayList<>();
        while (Optional.ofNullable(station).isPresent()) {
            result.add(station);
            station = findNextStation(station);
        }
        return result;
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

    //첫째 구간 찾기
    private Section findFirstSection() {
        return sections.stream()
                .filter(it -> !getDownStations().contains(it.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(it -> !getDownStations().contains(it.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getStation();
    }

    //마지막 역
    private Station findLastStation() {
        return sections.stream()
                .filter(it -> !getUpStations().contains(it.getNextStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getNextStation();
    }
}
