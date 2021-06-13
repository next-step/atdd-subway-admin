package nextstep.subway.section.domain;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean validDuplicationSection(Section section) {
        return sections.stream().anyMatch(it -> it.getUpStation().equals(section.getUpStation())
                   && it.getDownStation().equals(section.getDownStation()));
    }

    public void updateUpStation(Section section) {
        Station inputUpStation = section.getUpStation();
        sections.stream()
                .filter(it -> it.getUpStation().equals(inputUpStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section));
    }

    public void updateDownStation(Section section) {
        Station inputDownStation = section.getDownStation();
        sections.stream()
                .filter(it -> it.getDownStation().equals(inputDownStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section));
    }
}
