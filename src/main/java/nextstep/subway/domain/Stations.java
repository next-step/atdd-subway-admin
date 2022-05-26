package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Stations {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private List<Station> list = new ArrayList<>();

    public void addStation(Station station) {
        if (!list.contains(station)) {
            list.add(station);
        }
    }

    public void removeStation(Station station) {
        list.remove(station);
    }

    public List<Station> getLists() {
        return list;
    }

    public void clearLines() {
        list.forEach(Station::clearLine);
    }

    public boolean containsStation(Station station) {
        return list.contains(station);
    }

    @Override
    public String toString() {
        return "Stations{" +
                "list=" + list +
                '}';
    }
}
