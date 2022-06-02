package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStationsToUpStation(Station upStation) {
        return sections
                .stream()
                .filter((target) -> target.getUpStation().getId() == upStation.getId())
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Station> getStationsFromDownStation(Station downStation) {
        return sections
                .stream()
                .filter((target) -> target.getDownStation().getId() == downStation.getId())
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

}
