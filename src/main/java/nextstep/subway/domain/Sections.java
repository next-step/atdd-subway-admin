package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nextstep.subway.common.Messages.DUPLICATE_SECTION_ERROR;
import static nextstep.subway.common.Messages.NOT_MATCH_STATION_ERROR;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSections(Section section) {
        validate(section);
        checkUpStation(section);
        checkDownStation(section);
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        Set<Station> stations = new HashSet<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return new ArrayList<>(stations);
    }

    private void validate(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_ERROR);
        }

        if (getStations().size() > 0 && !isContainsStation(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_ERROR);
        }
    }

    private boolean isContainsStation(Station upStation, Station downStation) {
        return getStations().contains(upStation) || getStations().contains(downStation);
    }

    private void checkUpStation(Section section) {
        Station upStation = section.getUpStation();

        if (matchUpStation(upStation)) {
            Section station = findUpStation(upStation);
            station.updateUpStation(section.getDownStation(), section.getDistance());
        }
    }

    private void checkDownStation(Section section) {
        Station downStation = section.getDownStation();

        if (matchDownStation(downStation)) {
            Section station = findDownStation(downStation);
            station.updateDownStation(section.getUpStation(), section.getDistance());
        }
    }

    private boolean matchUpStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsUpStation(station));
    }

    private Section findUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseGet(Section::new);
    }

    private boolean matchDownStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsDownStation(station));
    }

    private Section findDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst()
                .orElseGet(Section::new);
    }
}
