package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.NoRelationStationException;
import nextstep.subway.line.exception.SameStationException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final Section... sections) {
        this(new ArrayList<>(Arrays.asList(sections)));
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> stations() {
        return sections.stream()
            .sorted()
            .map(Section::stations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        validateNewSection(newSection);
        sections.stream()
            .filter(section -> section.hasSameUpOrDownStation(newSection))
            .findFirst()
            .ifPresent(section -> section.insertBetween(newSection));
        sections.add(newSection);
    }

    public void removeStation(final Station station) {
        Optional<Section> upSection = getUpSection(station);
        Optional<Section> downSection = getDownSection(station);

        if (upSection.isPresent() && downSection.isPresent()) {
            upSection.ifPresent(section -> section.merge(downSection.get()));
            sections.remove(downSection.get());
        }
    }

    private Optional<Section> getDownSection(final Station station) {
        return sections.stream()
            .filter(section -> section.isUpStation(station))
            .findAny();
    }

    private Optional<Section> getUpSection(final Station station) {
        return sections.stream()
            .filter(section -> section.isDownStation(station))
            .findAny();
    }

    private void validateNewSection(Section newSection) {
        if (sections.stream()
            .noneMatch(section -> section.hasRelation(newSection))) {
            throw new NoRelationStationException();
        }

        if (sections.contains(newSection)) {
            throw new SameStationException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }

}
