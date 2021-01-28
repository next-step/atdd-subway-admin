package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        // 기존 구간의 상행역과 새로운 구간의 하행역이 일치하는 경우
        // A - B, C - A => C - A - B
        if (isMatchUpDown(downStation)) {
            elements.add(getIndexByUpStation(downStation), section);
            return;
        }

        // 기존 구간의 상행역과 새로운 구간의 상행역이 일치하는 경우
        // A - B, A - C => A - C - B
        if (isPresentByUpStation(section, upStation, downStation)) {
            elements.add(getIndexByUpStation(upStation), section);
            return;
        }

        // 기존 구간의 하행역과 새로운 구간의 하행역이 일치하는 경우
        // A - B, C - B => A - C - B
        if (isPresentByDownStation(section, upStation, downStation)) {
            int addIndex = getIndexByDownStation(downStation) + 1;
            elements.add(addIndex, section);
            return;
        }

        // 기존 구간의 하행역과 새로운 구간의 상행역이 일치하는 경우
        // A - B, B - C => A - B - C
        if (isMatchDownUp(upStation)) {
            int addIndex = getIndexByDownStation(upStation) + 1;
            elements.add(addIndex, section);
            return;
        }

        elements.add(section);
        
    }

    private boolean isMatchDownUp(Station station) {
        return elements.stream()
                .anyMatch(org -> org.getDownStation() == station);
    }

    private boolean isMatchUpDown(Station station) {
        return elements.stream()
                .anyMatch(org -> org.getUpStation() == station);
    }

    private boolean isPresentByDownStation(Section section, Station upStation, Station downStation) {
        return elements.stream()
                .filter(org -> org.getDownStation() == downStation)
                .findFirst()
                .map(org -> updateOriginalSection(section, org, org.getUpStation(), upStation))
                .isPresent();
    }

    private boolean isPresentByUpStation(Section section, Station upStation, Station downStation) {
        return elements.stream()
                .filter(org -> org.getUpStation() == upStation)
                .findFirst()
                .map(org -> updateOriginalSection(section, org, downStation, org.getDownStation()))
                .isPresent();
    }

    private int getIndexByDownStation(Station station) {
        return IntStream.range(0, elements.size())
                .filter(index -> elements.get(index).getDownStation() == station)
                .findFirst()
                .orElse(0);
    }

    private int getIndexByUpStation(Station station) {
        return IntStream.range(0, elements.size())
                .filter(index -> elements.get(index).getUpStation() == station)
                .findFirst()
                .orElse(0);
    }

    private Section updateOriginalSection(Section section, Section original, Station upStation, Station downStation) {
        int distance = original.getDistance() - section.getDistance();
        validDistance(distance);
        original.update(new Section(upStation, downStation, distance));
        return original;
    }

    private void validDistance(int distance) {
        if (distance <= 0) {
            throw new RuntimeException("기존 역 사이 길이보다 크거나 같습니다.");
        }
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
