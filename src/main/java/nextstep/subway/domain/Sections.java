package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void addStations(Line line, Station upStation, Station downStation, Long distance) {
        Section section = new Section(line, upStation, downStation, distance);
        values.add(section);
    }

    public List<Station> allStation() {
        LinkedHashSet<Station> stations = new LinkedHashSet<>();
        this.values.forEach(v -> {
            stations.add(v.getUpStation());
            stations.add(v.getDownStation());
        });
        return Collections.unmodifiableList(new ArrayList<>(stations));
    }
}
