package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void addSection(Section section) {
        for (Section target : sections) {
            target.rebase(section);
        }
        sections.add(section);
    }

    public List<Station> getOrderedStationsStartsWith(Station station) {
        Optional<Section> preSection = sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();

        Set<Station> result = new LinkedHashSet<>();
        while (preSection.isPresent()) {
            Section pre = preSection.get();
            result.addAll(pre.getStations());
            preSection = sections.stream()
                    .filter(it -> it.getUpStation() == pre.getDownStation())
                    .findFirst();
        }
        return new ArrayList<>(result);
    }
}
