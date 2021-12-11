package nextstep.subway.section.domain;

import nextstep.subway.exception.SubWayExceptionStatus;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.domain.Line;
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

    public void addSection(Section section) {

        if (CollectionUtils.isEmpty(this.sections)) {
            this.sections.add(section);
            return;
        }

        validateDuplicateSection(section);
        validateDistanceSection(section);

        Section persistSection = updateExistingSection(section);
        this.sections.add(persistSection);
    }

    public void deleteSection(Line line, Station station) {

        validateDeleteCondition();

        List<Section> existingSection = this.sections.stream()
                .filter(i -> i.isContainStation(station))
                .collect(Collectors.toList());

        if (existingSection.size() == 1) {
            Section deleteSection = existingSection.get(0);
            this.sections.remove(deleteSection);
        }

        if (existingSection.size() == 2) {
            deleteMiddleSection(line, station, existingSection);
        }
    }

    private void deleteMiddleSection(Line line, Station station, List<Section> existingSection) {

        Section before = existingSection.stream().filter(i -> i.getDownStation().equals(station)).findFirst().orElseThrow(IllegalArgumentException::new);
        Section after = existingSection.stream().filter(i -> i.getUpStation().equals(station)).findFirst().orElseThrow(IllegalArgumentException::new);

        Section mergedSection = Section.mergeExistingSection(line, before, after);
        this.sections.remove(before);
        this.sections.remove(after);
        this.sections.add(mergedSection);

    }

    private Section updateExistingSection(Section section) {
        List<Section> existingSection = this.sections.stream()
                .filter(i -> i.isInteractiveStation(section))
                .collect(Collectors.toList());

        validateExistingSection(existingSection);

        Section persistSection = section.updateNewSection();
        existingSection.forEach(i -> i.updateExistingSection(section));
        return persistSection;
    }

    private void validateExistingSection(List<Section> existingSections) {
        if (CollectionUtils.isEmpty(existingSections)) {
            throw new SubwayException(SubWayExceptionStatus.NO_INTERSECTION_STATION);
        }
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
                .filter(i -> isInsertableSection(i, section))
                .anyMatch(i -> i.isLongerThanEqual(section));
    }

    private boolean isInsertableSection(Section existingSection, Section newSection) {
        return existingSection.getUpStation().equals(newSection.getUpStation()) ||
                existingSection.getDownStation().equals(newSection.getDownStation());
    }

    private void validateDeleteCondition() {
        if (isOneSection()) {
            throw new SubwayException(SubWayExceptionStatus.CANNOT_DELETE_SECTION);
        }
    }

    private boolean isOneSection() {
        return this.sections.size() == 1;
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
