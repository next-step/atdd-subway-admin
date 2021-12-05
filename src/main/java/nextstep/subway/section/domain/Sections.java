package nextstep.subway.section.domain;

import nextstep.subway.exception.SubWayExceptionStatus;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addToSections(Section section) {
        validateDuplicateSection(section);
        this.sections.add(section);
    }

    private void validateDuplicateSection(Section section) {
        if (isDuplicationSection(section)) {
            throw new SubwayException(SubWayExceptionStatus.DUPLICATE_STATION);
        }
    }

    private Boolean isDuplicationSection(Section section) {
        return this.sections
                .stream()
                .anyMatch(i -> i.isSameNameWithUpStation(section) && i.isSameNameWithDownStation(section));
    }

    public List<Station> getStations() {
        List<Station> results = new ArrayList<>();

        Map<Station, Station> sortedStations = doCacheWithUpStations();
        Station upStation = getFirstStation();

        while (upStation != null) {
            results.add(upStation);
            upStation = sortedStations.get(upStation);
        }

        return results;
    }

    private Station getFirstStation() {
        List<Station> upStreamStations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStreamStations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        return upStreamStations.stream()
                .filter(upStreamStation -> !downStreamStations.contains(upStreamStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Map<Station, Station> doCacheWithUpStations() {
        return this.sections.stream()
                .collect(toMap(Section::getUpStation, Section::getDownStation));
    }
}
