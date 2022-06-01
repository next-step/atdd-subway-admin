package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections{
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections;

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations(){
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void addSection(Section section) {
        validateSectionToAdd(section);
        updateAdjacentSection(section);
        sections.add(section);
    }

    private void validateSectionToAdd(Section section) {
        if (containStation(section.getUpStation()) && containStation(section.getDownStation())) {
            throw new IllegalArgumentException("[ERROR] UpStation to DownStation : 이미 존재하는 구간입니다. ");
        }
        if (!containStation(section.getUpStation()) && !containStation(section.getDownStation())) {
            throw new IllegalArgumentException("[ERROR] UpStation to DownStation : 존재하지 않습니다. ");
        }
    }

    private boolean containStation(Station station){
        List<Station> stations = this.getStations();
        return stations.contains(station);
    }

    private void updateAdjacentSection(Section newSection) {
        for (Section section : sections) {
            section.updateIfAdjacentSection(newSection);
        }
    }

    public List<Station> getSortedStations() {
        sections.sort(new SectionComparator());
        return getStations();
    }

    private class SectionComparator implements Comparator<Section> {
        @Override
        public int compare(Section o1, Section o2) {
            if (o1.getDownStation().equals(o2.getUpStation())) {
                return -1;
            }
            return 1;
        }
    }
}
