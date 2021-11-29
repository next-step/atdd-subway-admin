package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final int SECTIONS_START_AT = 0;
    private static final int SECTIONS_UNIT = 1;

    @OneToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        mappedBy = "line",
        orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        sections.forEach(this::add);
    }

    public void add(final Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        final boolean hasUpStation = hasStation(newSection.getUpStation());
        final boolean hasDownStation = hasStation(newSection.getDownStation());
        checkStations(hasUpStation, hasDownStation);
        if (hasUpStation) {
            adjustUpStation(newSection);
        }
        if (hasDownStation) {
            adjustDownStation(newSection);
        }
        sections.add(newSection);
    }

    private boolean hasStation(final Station station) {
        return sections.stream()
            .anyMatch(s -> s.hasStation(station));
    }

    private void checkStations(final boolean hasUpStation, final boolean hasDownStation) {
        checkBothStationsAdded(hasUpStation, hasDownStation);
        checkNeitherStationAdded(hasUpStation, hasDownStation);
    }

    private void checkBothStationsAdded(final boolean hasUpStation, final boolean hasDownStation) {
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있을 경우 구간을 추가할 수 없습니다.");
        }
    }

    private void checkNeitherStationAdded(final boolean hasUpStation,
        final boolean hasDownStation) {
        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나가 포함되어야 구간에 추가할 수 있습니다.");
        }
    }

    private void adjustUpStation(final Section section) {
        sections.stream()
            .filter(s -> Objects.equals(s.getUpStation(), section.getUpStation()))
            .findFirst()
            .ifPresent(s -> s.adjustUpStation(section));
    }

    private void adjustDownStation(final Section section) {
        sections.stream()
            .filter(s -> Objects.equals(s.getDownStation(), section.getDownStation()))
            .findFirst()
            .ifPresent(s -> s.adjustUpStation(section));
    }

    public List<Station> getStationsInOrder() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final Stream<Station> stations = sections.stream().map(Section::getUpStation);
        final Stream<Station> lastStation = Stream.of(getLastStation());
        return Stream.concat(stations, lastStation).collect(Collectors.toList());
    }

    public int size() {
        return sections.size();
    }

    private Section getFirstSection() {
        return sections.get(SECTIONS_START_AT);
    }

    private Section getLastSection() {
        Section theSection = getFirstSection();
        Optional<Section> maybeSection = Optional.ofNullable(theSection);
        while (maybeSection.isPresent()) {
            final Section tmpSection = maybeSection.get();
            maybeSection = sections.stream()
                .filter(s -> Objects.equals(tmpSection.getDownStation(), s.getUpStation()))
                .findFirst();
            theSection = tmpSection;
        }
        return theSection;
    }

    private Station getLastStation() {
        return sections.get(size() - SECTIONS_UNIT).getDownStation();
    }
}
