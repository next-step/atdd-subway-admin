package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }


    public void update(Section section) {
        Section sameUpStationSection = findSameUpStationSection(section);
        Section sameDownStationSection = findSameDownStationSection(section);

        if (sameDownStationSection != null && sameUpStationSection != null) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }
        if (sameDownStationSection == null && sameUpStationSection == null) {
            throw new IllegalArgumentException("연결할 수 있는 역이 없습니다.");
        }

        if (sameUpStationSection != null) {
            sameUpStationSection.updateUpStation(section);
        }
        if (sameDownStationSection != null) {
            sameDownStationSection.updateDownStation(section);
        }

        sections.add(section);
    }

    private Section findSameUpStationSection(Section section) {
        return sections.stream().filter(s -> s.existUpStation(section)).findFirst().orElse(null);
    }

    private Section findSameDownStationSection(Section section) {
        return sections.stream().filter(s -> s.existDownStation(section)).findFirst().orElse(null);
    }

    public List<Station> orderedStations() {
        Station station = firstStation();
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(station);

        Section nextSection = findNextSection(station);
        do {
            orderedStations.add(nextSection.getDownStation());
            nextSection = findNextSection(nextSection.getDownStation());
        } while (nextSection != null);

        return orderedStations;
    }

    private Section findNextSection(Station station) {
        Optional<Section> nextSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station)).findFirst();
        return nextSection.orElse(null);
    }

    private Station firstStation() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();

        sections.forEach(section -> {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        });

        upStations.removeAll(downStations);
        return upStations.get(0);
    }

}
