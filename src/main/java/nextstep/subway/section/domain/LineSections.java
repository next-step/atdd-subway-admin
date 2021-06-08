package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.SortedStations;
import nextstep.subway.station.domain.Station;

import static java.util.stream.Collectors.toList;

@Embeddable
public class LineSections implements Serializable {

    private static final long serialVersionUID = -4483053178441994936L;

    private static final String MESSAGE_SECTION_HAS_CYCLE = "상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.";
    private static final String MESSAGE_SECTION_IS_NOT_UPDATABLE = "신규 구간의 상행역과 하행역 모두가 기존 구간에 포함되어 있지 않습니다.";
    private static final String MESSAGE_HAS_ONE_SECTION = "노선에 구간이 1개이면 삭제할 수 없습니다.";
    private static final String MESSAGE_NOT_FOUND_SECTION = "삭제할 구간을 찾을 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Section> sections;

    public LineSections() {
        sections = new HashSet<>();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Set<Section> getSections() {
        return sections;
    }

    public SortedStations toStations() {
        return new SortedStations(toSortedSections());
    }

    public SortedSection toSortedSections() {
        return new SortedSection(this);
    }

    public void verifyStationCycle(Station upStation, Station downStation) {

        if (sections.isEmpty()) {
            return;
        }

        Optional<Section> maybeUpStation = sections.stream()
                                                   .filter(section -> section.contains(upStation))
                                                   .findAny();

        Optional<Section> maybeDownStation = sections.stream()
                                                     .filter(section -> section.contains(downStation))
                                                     .findAny();

        if (maybeUpStation.isPresent() && maybeDownStation.isPresent()) {
            throw new IllegalArgumentException(MESSAGE_SECTION_HAS_CYCLE);
        }
    }

    public void verifyNotUpdatable(Station upStation, Station downStation) {

        Optional<Section> updatableSection =
            sections.stream()
                    .filter(section -> section.equalsUpStation(downStation)
                        || section.equalsUpStation(upStation)
                        || section.equalsDownStation(upStation)
                        || section.equalsDownStation(downStation))
                    .findAny();

        if (!updatableSection.isPresent()) {
            throw new IllegalArgumentException(MESSAGE_SECTION_IS_NOT_UPDATABLE);
        }
    }

    public Optional<Section> updateSection(Station upStation, Station downStation, int distance) {

        Optional<Section> maybeSection =
            sections.stream()
                    .filter(section -> section.equalsUpStation(upStation)
                        || section.equalsDownStation(downStation))
                    .findAny();

        if (!maybeSection.isPresent()) {
            return Optional.empty();
        }

        Section section = maybeSection.get();
        section.minusDistance(distance);

        if (section.equalsUpStation(upStation)) {
            section.updateUpStation(downStation);
            return Optional.of(section);
        }

        section.updateDownStation(upStation);
        return Optional.of(section);
    }

    public void updateLine(Line line) {
        sections.forEach(section -> section.toLine(line));
    }

    public void deleteSection(Station station) {

        verifyHasSingleSection();

        List<Section> targets = sections.stream()
                                 .filter(section -> section.contains(station))
                                 .collect(toList());

        verifyNotFoundDeleteTarget(targets);

        if (targets.size() == 1) {
            deleteSingleSection(targets);
            return;
        }

        deleteDoubleSectionAndUpdate(station, targets);
    }

    private void verifyHasSingleSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(MESSAGE_HAS_ONE_SECTION);
        }
    }

    private void deleteSingleSection(List<Section> targets) {
        targets.forEach(sections::remove);
    }

    private void verifyNotFoundDeleteTarget(List<Section> targets) {
        if (targets.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_NOT_FOUND_SECTION);
        }
    }

    private void deleteDoubleSectionAndUpdate(Station station, List<Section> targets) {

        Section upSection = targets.stream()
                                   .filter(target -> target.equalsDownStation(station))
                                   .findAny()
                                   .orElseThrow(() -> new IllegalArgumentException(MESSAGE_NOT_FOUND_SECTION));

        Section downSection = targets.stream()
                                     .filter(target -> target.equalsUpStation(station))
                                     .findAny()
                                     .orElseThrow(() -> new IllegalArgumentException(MESSAGE_NOT_FOUND_SECTION));

        sections.remove(downSection);

        upSection.updateDownStation(downSection.getDownStation());
        upSection.plusDistance(downSection.getDistance());
    }
}
