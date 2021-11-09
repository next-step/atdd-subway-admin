package nextstep.subway.line.domain;

import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list;

    protected Sections() {
    }

    private Sections(List<Section> list) {
        Assert.notEmpty(list, "section list must not be empty");
        this.list = list;
    }

    public static Sections from(List<Section> list) {
        return new Sections(list);
    }

    List<Station> getStations() {
        return Collections.emptyList();
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
}
