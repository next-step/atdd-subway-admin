package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        findUpSection(section.getUpStation())
                .ifPresent(station -> station.updateUpStation(section));
        findDownSection(section.getDownStation())
                .ifPresent(station -> station.updateDownStation(section));
        sections.add(section);
    }

    public Optional<Section> findUpSection(Station upStation) {
        return sections.stream()
                .filter(section -> section.equalUpStation(upStation))
                .findFirst();
    }

    public Optional<Section> findDownSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.equalDownStation(downStation))
                .findFirst();
    }
}
