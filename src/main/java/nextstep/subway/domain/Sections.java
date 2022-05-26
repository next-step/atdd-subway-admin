package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSection(section);
    }

    public Set<Station> getStations() {
        return sections.stream()
            .flatMap(section -> section.getStations().stream())
            .collect(Collectors.toSet());
    }

    private void addSection(Section newSection) {
        checkValidation(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void checkValidation(Section section) {
        Set<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("section is already registered.");
        }
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("must be one station contains.");
        }
    }

}
