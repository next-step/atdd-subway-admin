package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_section"))
    private List<Section> values = new ArrayList<>();

    protected Sections() {

    }

    private Sections(List<Section> sections) {
        this.values = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        values.add(section);
    }

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        Set<Station> stationVisit = new HashSet<>();
        Map<Station, Station> upToDownStation = new HashMap<>();
        Map<Station, Station> downToUpStation = new HashMap<>();

        values.forEach(section -> {
            upToDownStation.put(section.getUpStation(), section.getDownStation());
            downToUpStation.put(section.getDownStation(), section.getUpStation());
        });

        for (Map.Entry<Station, Station> entry : upToDownStation.entrySet()) {
            addSortedStation(result, stationVisit, upToDownStation, downToUpStation, entry);
        }

        return result;
    }

    private void addSortedStation(List<Station> result, Set<Station> stationVisit, Map<Station, Station> upToDownStation, Map<Station, Station> downToUpStation, Map.Entry<Station, Station> entry) {
        Station upStation = entry.getKey();

        if (stationVisit.contains(upStation)) {
            return;
        }

        upStation = findTopUpStation(downToUpStation, upStation);
        addAllSectionFromTopUpStation(result, stationVisit, upToDownStation, upStation);
    }

    private void addAllSectionFromTopUpStation(List<Station> result, Set<Station> stationVisit, Map<Station, Station> upToDownStation, Station upStation) {
        while (upToDownStation.containsKey(upStation)) {
            result.add(upStation);
            stationVisit.add(upStation);

            upStation = upToDownStation.get(upStation);
        }

        result.add(upStation);
    }

    private Station findTopUpStation(Map<Station, Station> downToUpStation, Station upStation) {
        while (downToUpStation.containsKey(upStation)) {
            upStation = downToUpStation.get(upStation);
        }
        return upStation;
    }
}
