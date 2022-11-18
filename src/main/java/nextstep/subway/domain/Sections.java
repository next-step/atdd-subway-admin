package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void addStations(Line line, Station upStation, Station downStation, Long distance) {
        Section section = new Section(line, upStation, downStation, distance);
        values.add(section);
    }

    public List<Station> allStation() {
        List<Station> stations = new ArrayList<>();
        this.values.forEach(v -> {
            stations.add(v.getUpStation());
            stations.add(v.getDownStation());
        });
        return stations;
    }
}
