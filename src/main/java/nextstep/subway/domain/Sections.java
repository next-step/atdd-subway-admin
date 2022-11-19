package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public Optional<Section> findSameUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst();
    }

    public Optional<Section> findSameDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst();
    }

    public void validateAlreadyExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateAlreadyExistsStation(upStation, downStation));
    }

    public void validateNotExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateNotExistsStation(upStation, downStation));
    }

}
