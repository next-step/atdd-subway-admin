package nextstep.subway.station.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    @OrderColumn
    private List<Section> sections = new LinkedList<>();

    public void addSection(Section section) {
        addSection(sections.size(),section);
    }
    public void addSection(int idx,Section section) {
        checkArgument(ObjectUtils.isNotEmpty(section), "section should not be null");
        sections.add(idx,section);
    }

    public List<Station> getStations() {
        return sections.stream().map(section->section.getStation()).collect(Collectors.toList());
    }
}
