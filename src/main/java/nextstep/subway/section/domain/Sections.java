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

    public int size() {
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
        Optional<Section> upLineSection = sections.stream()
                .filter(it -> it.getDistance() == 0).findFirst();

        List<Station> result = new ArrayList<>();
        while (upLineSection.isPresent()) {
            Station downStation = upLineSection.get().getDownStation();

            result.add(downStation);
            upLineSection = sections.stream()
                    .filter(it -> it.getDistance() != 0 && it.getUpStation().getId() == downStation.getId())
                    .findFirst();
        }
        return result;
    }

    public List<Section> getSectionInOrder() {
        // 출발지점 찾기
        Optional<Section> upSection = sections.stream()
                .filter(it -> it.getDistance() == 0).findFirst();

        List<Section> result = new ArrayList<>();
        while (upSection.isPresent()) {
            Section foundSection = upSection.get();
            result.add(foundSection);
            upSection = sections.stream()
                    .filter(it -> it.getUpStation() != null && it.getUpStation().getId() == foundSection.getDownStation().getId())
                    .findFirst();
        }
        return result;
    }

    private boolean isFirstSection(Section it) {
        return it == null || it.getDistance() == 0;
    }


    public List<Section> getSections() {
        return sections;
    }
}
