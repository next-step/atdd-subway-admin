package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void add(Section section) {
        this.values.add(section);
    }

    public List<Station> toStations() {
        List<Station> result = new ArrayList<>();
        Section section = values.get(0);
        result.addAll(getUpStations(section.getUpStation()));
        result.addAll(getDownStations(section.getDownStation()));
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
                .map(section -> section.getUpStation())
                .findFirst();
    }

    private Optional<Station> findDownStation(Station downStation) {
        return values.stream()
                .filter(section -> section.getUpStation().getId().equals(downStation.getId()))
                .map(section -> section.getDownStation())
                .findFirst();
    }
}
