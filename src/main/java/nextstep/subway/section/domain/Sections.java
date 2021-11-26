package nextstep.subway.section.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Stations;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Section> sections;

    public Sections() {
        this.sections = new HashSet<>();
    }

    public Sections(final Set<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }

    public Stations toStations() {
        return Stations.of(
            this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList))
        );
    }
}
