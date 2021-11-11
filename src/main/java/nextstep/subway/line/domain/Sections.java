package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    protected Sections() {
    }

    private Sections(Section section) {
        Assert.notNull(section, "section must not be null");
        this.list.add(section);
    }

    public static Sections from(Section section) {
        return new Sections(section);
    }

    List<Station> stations() {
        return new ArrayList<>(removedDuplicateStations());
    }

    @Override
    public String toString() {
        return "Sections{" +
            "list=" + list +
            '}';
    }

    void setLine(Line line) {
        for (Section section : list) {
            section.setLine(line);
        }
    }

    private Set<Station> removedDuplicateStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : list) {
            stations.addAll(section.stations());
        }
        return stations;
    }
}
