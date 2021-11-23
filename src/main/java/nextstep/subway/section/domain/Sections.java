package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private static final int START_SECTION_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        return this.sections.stream().
                map(Section::getUpStation).collect(Collectors.toList());
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getStationsInOrder() {
        // 출발지점 찾기
//        Optional<Section> upLineStation = Optional.ofNullable(sections.get(0));
//
//        List<Section> result = new ArrayList<>();
//        while (upLineStation.isPresent()) {
//            Optional<Section> upStation = Optional.ofNullable(upLineStation.get());
//            result.add(upStation.get());
//            upStation = sections.stream()
//                    .filter(it -> it.getUpStation().getId() == upLineStation.get().getId())
//                    .findFirst();
//        }
//        return result;
        return null;
    }
}
