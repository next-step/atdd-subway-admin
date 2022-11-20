package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "line")
    private List<Section> content = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> content) {
        this.content = new ArrayList<>(content);
    }

    public void addSection(Section section) {
        content.add(section);
    }

    public void removeSectionById(Long id) {
        content.removeIf(section -> section.getId().equals(id));
    }

    public List<Station> getStations() {
        return content.stream()
                .flatMap(section -> section.getStations().stream())
                .collect(toList());
    }
}
