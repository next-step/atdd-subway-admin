package nextstep.subway.section.domain;

import nextstep.subway.exception.SubWayExceptionStatus;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        if (CollectionUtils.isEmpty(this.sections)) {
            this.sections.add(section);
            return;
        }

        validateDuplicateSection(section);
        validateLengthSection(section);
        this.sections.add(section);
    }

    private void validateDuplicateSection(Section section) {
        if (isDuplicationSection(section)) {
            throw new SubwayException(SubWayExceptionStatus.DUPLICATE_STATION);
        }
    }

    private void validateLengthSection(Section section) {
        Section existingSection = getExistingStation(section);
        if (isDistanceLongerThanEqual(section, existingSection)) {
            throw new SubwayException(SubWayExceptionStatus.LONG_DISTANCE_SECTION);
        }
    }

    private Boolean isDistanceLongerThanEqual(Section section, Section existingSection) {

        Station upStation = existingSection.getUpStation();
        Station downStation = existingSection.getDownStation();

        Station firstStation = getFirstStation();
        Station lastStation = getLastStation();

        if (upStation.isSameName(firstStation) || downStation.isSameName(lastStation)) {
            return Boolean.FALSE;
        }

        return existingSection.isLongerThanEqual(section);
    }

    private Boolean isDuplicationSection(Section section) {
        return this.sections
                .stream()
                .anyMatch(i -> i.isSameNameWithUpStation(section) && i.isSameNameWithDownStation(section));
    }

    private Section getExistingStation(Section section) {
        return this.sections
                .stream()
                .filter(i -> i.isInteractiveWithStation(section))
                .findFirst()
                .orElseThrow(() -> new SubwayException(SubWayExceptionStatus.NO_INTERSECTION_STATION));
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

        List<Station> upStreamStations = getUpstreamStations();
        List<Station> downStreamStations = getDownStreamStation();

        return upStreamStations.stream()
                .filter(upStreamStation -> !downStreamStations.contains(upStreamStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Station getLastStation() {

        List<Station> upStreamStations = getUpstreamStations();
        List<Station> downStreamStations = getDownStreamStation();

        return downStreamStations.stream()
                .filter(downStreamStation -> !upStreamStations.contains(downStreamStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<Station> getUpstreamStations() {
        return this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
    }

    private List<Station> getDownStreamStation() {
        return this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
    }

    private Map<Station, Station> doCacheWithUpStations() {
        return this.sections.stream()
                .collect(toMap(Section::getUpStation, Section::getDownStation));
    }
}
