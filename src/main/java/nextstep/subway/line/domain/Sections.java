package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    void addSection(final Section section) {
        if (!sections.isEmpty()) {
            validateAddable(section);

            sections.stream()
                .filter(it -> it.isOverlapped(section))
                .findAny()
                .ifPresent(it -> it.divideBy(section));
        }

        sections.add(section);
    }

    void deleteSection(final Station targetStation) {
        validateDeletable(targetStation);

        final Iterator<Section> matchedIterator = sections.stream()
            .filter(it -> it.getUpStation().equals(targetStation) || it.getDownStation().equals(targetStation))
            .iterator();
        final Section toBeDeleted = matchedIterator.next();
        if (matchedIterator.hasNext()) {
            matchedIterator.next().connectWith(toBeDeleted);
        }
        sections.remove(toBeDeleted);
    }

    private void validateAddable(final Section section) {
        final Set<Station> allStations = extractAllStations();
        final List<Station> sectionStations = section.getStations();

        final Set<Station> matchedStations = sectionStations.stream()
            .filter(allStations::contains)
            .collect(Collectors.toSet());

        if (matchedStations.isEmpty()) {
            throw new BadRequestException("추가되는 구간은 기존의 구간과 연결 가능하여야 합니다.");
        }
        if (matchedStations.containsAll(sectionStations)) {
            throw new BadRequestException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private void validateDeletable(final Station station) {
        if (sections.size() == MIN_SIZE) {
            throw new BadRequestException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }

        final Set<Station> allStations = extractAllStations();
        if (!allStations.contains(station)) {
            throw new BadRequestException("노선에 등록되어 있지 않은 역은 삭제할 수 없습니다.");
        }
    }

    List<Station> computeSortedStations() {
        final List<Station> sortedStations = new ArrayList<>();
        final Section upEdgeSection = computeUpEdgeSection();
        sortedStations.add(upEdgeSection.getUpStation());
        sortedStations.add(upEdgeSection.getDownStation());

        final List<Station> nextStations = computeNextSections(upEdgeSection).stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        sortedStations.addAll(nextStations);

        return sortedStations;
    }

    private Section computeUpEdgeSection() {
        final Set<Station> downStations = extractDownStations();

        return sections.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("지하철 노선에 상행 종점 구간이 존재하지 않습니다."));
    }

    private List<Section> computeNextSections(final Section previousSection) {
        final List<Section> nextSections = new ArrayList<>();

        final Optional<Section> nextSection = sections.stream()
            .filter(section -> section.isNextSection(previousSection))
            .findAny();

        if (nextSection.isPresent()) {
            nextSections.add(nextSection.get());
            nextSections.addAll(computeNextSections(nextSection.get()));
        }

        return nextSections;
    }

    private Set<Station> extractAllStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toSet());
    }

    private Set<Station> extractDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }
}
