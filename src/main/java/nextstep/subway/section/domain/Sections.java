package nextstep.subway.section.domain;

import nextstep.subway.section.exception.UnaddableSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void add(Section section) {
        validateExistingSection(section);
        addSectionByCase(section);
    }

    private void addSectionByCase(Section section) {
        if (isEmpty() || isStartOrEndStationInLine(section)) {
            this.values.add(section);
        }

        if (isOverlappedUpStationBetweenSection(section)) {
            connectUpStationBetweenSection(section);
        }

        if (isOverlappedDownStationBetweenSection(section)) {
            connectDownStationBetweenSection(section);
        }
    }

    private boolean isEmpty() {
        return values.isEmpty();
    }

    private boolean isOverlappedUpStationBetweenSection(Section section) {
        return values.stream()
                .filter(value -> value.isSameUpStationWithUpStation(section))
                .count() >= 1;
    }

    private boolean isOverlappedDownStationBetweenSection(Section section) {
        return values.stream()
                .filter(value -> value.isSameDownStationWithDownStation(section))
                .count() >= 1;
    }

    private void connectUpStationBetweenSection(Section section) {
        Optional<Section> foundSection = values.stream()
                .filter(value -> value.isSameUpStationWithUpStation(section)).findFirst();
        if (foundSection.isPresent()) {
            Section connectedSection = foundSection.get();
            connectedSection.replaceUpStation(section);
            values.add(section);
        }
    }

    private void connectDownStationBetweenSection(Section section) {
        Optional<Section> foundSection = values.stream()
                .filter(value -> value.isSameDownStationWithDownStation(section)).findFirst();
        if (foundSection.isPresent()) {
            Section connectedSection = foundSection.get();
            connectedSection.replaceDownStation(section);
            values.add(section);
        }
    }

    private void validateExistingSection(Section section) {
        List<Station> stations = toStations();
        boolean result = stations.stream()
                .filter(station -> station.equals(section.getUpStation())
                        || station.equals(section.getDownStation()))
                .count() == 2;
        if (result) {
            throw new UnaddableSectionException("기존에 등록된 구간과 중복됩니다.");
        }
    }

    public boolean isStartOrEndStationInLine(Section section) {
        List<Station> stations = toStations();
        Station startStation = stations.get(0);
        Station endStation = stations.get(stations.size() - 1);

        return section.isSameDownStationWithUpStation(startStation)
                || section.isSameUpStationWithDownStation(endStation);
    }

    public List<Station> toStations() {
        List<Station> result = new ArrayList<>();
        if (!isEmpty()) {
            Section section = values.get(0);
            result.addAll(getUpStations(section.getUpStation()));
            result.addAll(getDownStations(section.getDownStation()));
        }
        return result;
    }

    private List<Station> getUpStations(Station upStation) {
        LinkedList<Station> result = new LinkedList<>();
        Station foundUpStation = upStation;
        while (foundUpStation != null) {
            result.addFirst(foundUpStation);
            foundUpStation = findUpStation(foundUpStation).orElse(null);
        }
        return result;
    }

    private List<Station> getDownStations(Station downStation) {
        List<Station> result = new ArrayList<>();
        Station foundDownStation = downStation;
        while (foundDownStation != null) {
            result.add(foundDownStation);
            foundDownStation = findDownStation(foundDownStation).orElse(null);
        }
        return result;
    }

    private Optional<Station> findUpStation(Station upStation) {
        return values.stream()
                .filter(section -> section.getDownStation().getId().equals(upStation.getId()))
                .map(Section::getUpStation)
                .findFirst();
    }

    private Optional<Station> findDownStation(Station downStation) {
        return values.stream()
                .filter(section -> section.getUpStation().getId().equals(downStation.getId()))
                .map(Section::getDownStation)
                .findFirst();
    }
}
