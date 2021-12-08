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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public void addToSections(Section section) {

        if (CollectionUtils.isEmpty(this.sections)) {
            this.sections.add(section);
            return;
        }

        validateDuplicateSection(section);
        validateDistanceSection(section);

        Section persistSection = updateExistingSection(section);
        this.sections.add(persistSection);
    }

    private Section updateExistingSection(Section section) {
        Section existingSection = this.sections.stream()
                .filter(i -> i.isInteractiveStation(section))
                .findFirst()
                .orElseThrow(() -> new SubwayException(SubWayExceptionStatus.NO_INTERSECTION_STATION));

        Section persistSection = section.updateNewSection();
        existingSection.updateExistingSection(section);
        return persistSection;
    }

    private void validateDuplicateSection(Section section) {
        if (isDuplicationSection(section)) {
            throw new SubwayException(SubWayExceptionStatus.DUPLICATE_STATION);
        }
    }

    private Boolean isDuplicationSection(Section section) {
        return this.sections.stream().anyMatch(i -> i.isDuplicateSection(section));
    }

    private void validateDistanceSection(Section section) {
        if (isDistanceLongerThanEqual(section)) {
            throw new SubwayException(SubWayExceptionStatus.LONG_DISTANCE_SECTION);
        }
    }

    private Boolean isDistanceLongerThanEqual(Section section) {
        return this.sections.stream()
                .filter(i -> i.getUpStation().equals(section.getUpStation()) || i.getDownStation().equals(section.getDownStation()))
                .anyMatch(i -> i.isLongerThanEqual(section));
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
