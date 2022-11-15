package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<StationResponse> toResponse() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream()).distinct()
                .map(station -> new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate()))
                .collect(Collectors.toList());
    }

    public List<Section> values() {
        return Collections.unmodifiableList(sections);
    }
}
