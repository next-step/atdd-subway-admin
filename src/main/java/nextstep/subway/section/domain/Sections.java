package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getUpDownStations().stream())
                .collect(Collectors.toList());
    }

    public void checkSectionContainStations(Station upStation, Station downStation) {
        if (isContain(upStation) && isContain(downStation)) {
            throw new IllegalArgumentException("역이 모두 구간에 포함되어 있습니다.");
        }
    }

    private boolean isContain(Station station) {
        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(station))
                .isPresent();
    }
}
