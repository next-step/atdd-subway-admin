package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final int SECTIONS_START_AT = 0;

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
            .ifPresent(s -> s.adjustDownStation(section));
    }

    public List<Station> getStationsInOrder() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final Section firstSection = getFirstSection();
        final List<Station> stationsInOrder = new ArrayList<>(
            Arrays.asList(firstSection.getUpStation())
        );
        Section it = firstSection;
        while (it != null) {
            final Section tmp = it;
            it = sections.stream()
                .filter(s -> Objects.equals(s.getUpStation(), tmp.getDownStation()))
                .findFirst()
                .orElse(null);
            stationsInOrder.add(tmp.getDownStation());
        }
        return stationsInOrder;
    }

    private Section getFirstSection() {
        final Station firstStation = getFirstStation();
        return sections.stream()
            .filter(s -> Objects.equals(s.getUpStation(), firstStation))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    private Station getFirstStation() {
        final Set<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
        final Set<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
        upStations.removeAll(downStations);
        return new ArrayList<>(upStations).get(SECTIONS_START_AT);
    }

    public int size() {
        return sections.size();
    }

    public void deleteStation(final Station station) {
        validateStationIsRemovable(station);

        final Optional<Section> maybeUpSection = sections.stream()
            .filter(s -> Objects.equals(s.getDownStation(), station))
            .findFirst();
        final Optional<Section> maybeDownSection = sections.stream()
            .filter(s -> Objects.equals(s.getUpStation(), station))
            .findFirst();

        if (maybeUpSection.isPresent() && maybeDownSection.isPresent()) {
            final Section upSection = maybeUpSection.get();
            final Section downSection = maybeDownSection.get();
            upSection.merge(downSection);
            sections.remove(downSection);
            return;
        }

        maybeUpSection.ifPresent(s -> sections.remove(s));
        maybeDownSection.ifPresent(s -> sections.remove(s));
    }

    private void validateStationIsRemovable(final Station station) {
        if (sections.isEmpty() || !hasStation(station)) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역을 제거할 수 없습니다");
        }
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서 역을 제거할 수 없습니다.");
        }
    }
}
