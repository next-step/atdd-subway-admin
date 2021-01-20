package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<Section> elements;

    public Sections() {
        this.elements = new ArrayList<>();
    }

    public Sections(List<Section> elements) {
        this.elements = elements;
    }

    public List<Section> getElements() {
        return elements;
    }

    public void addSection(Section section) {

        int addIndex = 0;
        for (int i = 0; i < elements.size(); i++) {

            Section original = elements.get(i);
            int distance = original.getDistance() - section.getDistance();

            Station orgUpStation = original.getUpStation();
            Station orgDownStation = original.getDownStation();
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            // 기존 구간의 상행역과 새로운 구간의 하행역이 일치하는 경우
            // A - B, C - A => C - A - B
            if (orgUpStation.equals(downStation)) {
                addIndex = i;
                break;
            }

            // 기존 구간의 상행역과 새로운 구간의 상행역이 일치하는 경우
            // A - B, A - C => A - C - B
            if (orgUpStation.equals(upStation)) {
                original.update(new Section(downStation, orgDownStation, distance));
                addIndex = i;
                break;
            }

            // 기존 구간의 하행역과 새로운 구간의 하행역이 일치하는 경우
            // A - B, C - B => A - C - B
            if (orgDownStation.equals(downStation)) {
                original.update(new Section(orgUpStation, upStation, distance));
                addIndex = i + 1;
                break;
            }

            // 기존 구간의 하행역과 새로운 구간의 상행역이 일치하는 경우
            // A - B, B - C => A - B - C
            if (orgDownStation.equals(upStation)) {
                addIndex = i + 1;
                break;
            }

        }

        elements.add(addIndex, section);
    }

    public void removeSection(Section section) {
        elements.remove(section);
    }

    public List<Station> toStations() {
        List<Station> stations = new ArrayList<>();
        Station upStation = findUpStation();
        stations.add(upStation);
        addSortByUpStation(upStation, stations);
        return stations;
    }

    private Station findUpStation() {
        Station upStation = findFirstUpStation();
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> findSection = elements.stream()
                    .filter(section -> section.getDownStation() == finalUpStation)
                    .findFirst();
            if (!findSection.isPresent()) {
                break;
            }
            upStation = findSection.get().getUpStation();
        }
        return upStation;
    }

    private Station findFirstUpStation() {
        return this.elements
                .stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getUpStation();
    }

    private void addSortByUpStation(Station upStation, List<Station> stations) {
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> findSection = this.elements.stream()
                    .filter(section -> section.getUpStation() == finalUpStation)
                    .findFirst();
            if (!findSection.isPresent()) {
                break;
            }
            upStation = findSection.get().getDownStation();
            stations.add(upStation);
        }
    }

}
