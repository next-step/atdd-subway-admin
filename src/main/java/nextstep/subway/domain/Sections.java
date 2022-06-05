package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public boolean isEmpty() {
        return sections == null || sections.isEmpty();
    }

    public boolean containsSection(Section section) {
        Station newUpStation = section.getUpStation();
        Station newDownStation = section.getDownStation();

        return containsStation(newUpStation) && containsStation(newDownStation);
    }

    public boolean containsStation(Station station) {
        boolean hasStation = false;
        for (Section oldSection : sections) {
            hasStation |= oldSection.getUpStation().equals(station);
            hasStation |= oldSection.getDownStation().equals(station);
        }
        return hasStation;
    }

    public List<Section> getSections() {
        return sections;
    }
}
