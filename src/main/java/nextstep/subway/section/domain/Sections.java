package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> stations() {
        Set<Station> stations = new LinkedHashSet<>();

        sections.sort(Comparator.comparingInt(Section::sectionOrder));

        for (Section section : sections) {
            stations.add(section.upStation());
            stations.add(section.downStation());
        }

        return Collections.unmodifiableList(new ArrayList<>(stations));
    }
}
