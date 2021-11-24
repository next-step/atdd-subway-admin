package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public int size(){
        return this.sections.size();
    }

    public List<Station> getStations() {
        return this.sections.stream().
                map(Section::getUpStation).collect(Collectors.toList());
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStationsInOrder() {
        // 출발지점 찾기

        Optional<Section> startLineSection = sections.stream()
                .filter(it -> it.getUpStation() == null).findFirst();

        List<Station> result = new ArrayList<>();
        while (startLineSection.isPresent()) {
//            Section startStationId = startLineSection.get().getDownStation();
//
//
//            result.add(startLineSection.get().getDownStation());
//
//            result.add(upStation.get());
//            upStation = sections.stream()
//                    .filter(it -> it.getUpStation().getId() == startLineSection.get().getId())
//                    .findFirst();
        }

        return result;

    }

    public List<Section> getSections() {
        return sections;
    }
}
