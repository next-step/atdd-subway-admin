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

        Section interactiveSection = getInteractiveWithSection(section);

        validateDuplicateSection(section, interactiveSection);
        validateLengthSection(section, interactiveSection);

        Section updatedSection = updateSection(section, interactiveSection);
        this.sections.add(updatedSection);
    }

    private Section updateSection(Section section, Section interactiveSection) {
        return interactiveSection.updateSection(section);
    }

    private void validateDuplicateSection(Section section, Section interactiveSection) {
        if (isDuplicationSection(section, interactiveSection)) {
            throw new SubwayException(SubWayExceptionStatus.DUPLICATE_STATION);
        }
    }

    private void validateLengthSection(Section section, Section interactiveSection) {
        if (isDistanceLongerThanEqual(section, interactiveSection)) {
            throw new SubwayException(SubWayExceptionStatus.LONG_DISTANCE_SECTION);
        }
    }

    private Boolean isDistanceLongerThanEqual(Section section, Section existingSection) {

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        Station firstStation = getFirstStation();
        Station lastStation = getLastStation();

        if (upStation.isSameName(lastStation) || downStation.isSameName(firstStation)) {
            return Boolean.FALSE;
        }

        return existingSection.isLongerThanEqual(section);
    }

    private Boolean isDuplicationSection(Section section, Section interactiveSection) {
        return section.isSameNameWithUpStation(interactiveSection) && section.isSameNameWithDownStation(interactiveSection);
    }

    private Section getInteractiveWithSection(Section section) {
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
