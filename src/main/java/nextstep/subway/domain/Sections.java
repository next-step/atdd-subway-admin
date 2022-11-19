package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        validate(section);
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

    private void validate(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        Set<Station> matchedStations = getMatchedSectionStations(section);
        if(matchedStations.containsAll(section.getStations())) {
            throw new IllegalArgumentException("추가 하고자 하는 상행역과 하행역이 이미 노선에 등록되어 있습니다");
        }
        if (matchedStations.isEmpty()) {
            throw new IllegalArgumentException("추가 하고자 하는 상행역과 하행역 중 매칭되는 역이 없습니다");
        }

    }

    private Set<Station> getMatchedSectionStations(Section section) {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .filter(station -> section.getStations().contains(station))
                .collect(Collectors.toSet());
    }
}
