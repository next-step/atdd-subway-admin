package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<StationResponse> getStations() {
        Set<StationResponse> upStations = sections.stream()
            .map(section -> StationResponse.of(section.getUpStation()))
            .collect(Collectors.toSet());
        Set<StationResponse> downStations = sections.stream()
            .map(section -> StationResponse.of(section.getDownStation()))
            .collect(Collectors.toSet());

        return Stream.of(upStations, downStations)
            .flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

}
