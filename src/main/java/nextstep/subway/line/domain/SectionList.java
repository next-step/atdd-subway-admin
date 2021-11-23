package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionType;
import nextstep.subway.station.domain.Station;

@Embeddable
public class SectionList {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public void add(Section section) {
        if (contains(section)) {
            return;
        }
        this.sections.add(section);
    }

    public List<Section> getSortedList() {
        return Collections.unmodifiableList(
            this.sections.stream()
                .sorted()
                .collect(Collectors.toList()));
    }

    boolean contains(Section section) {
        return this.sections.contains(section);
    }

    void remove(Section section) {
        this.sections.remove(section);
    }

    Section findByStation(Station station) {
        return this.sections.stream()
            .filter(section -> section.hasStation(station))
            .findFirst().orElse(null);
    }

    Section findByLinkStation(Station linkStation) {
        return this.sections.stream()
            .filter(section -> section.hasLinkStation(linkStation))
            .findFirst().orElse(null);
    }

}
