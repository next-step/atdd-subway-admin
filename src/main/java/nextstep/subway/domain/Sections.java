package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public boolean addSection(final Section section) {
        return sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(section ->
                        Arrays.asList(
                                section.getUpStation(),
                                section.getDownStation()
                        ))
                .flatMap(stationArray -> stationArray.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
