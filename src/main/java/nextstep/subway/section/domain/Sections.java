package nextstep.subway.section.domain;

import com.google.common.collect.Lists;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = Lists.newArrayList();

    protected Sections() {
    }

    public static Sections createEmpty() {
        return new Sections();
    }

    public void add(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(ExceptionMessage.ALREADY_ADDED_SECTION);
        }

        sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findAny()
                .ifPresent(it -> it.update(section.getDownStation(), it.getDistance() - section.getDistance()));

        sections.add(section);
    }

    public List<Station> getStationsInOrder() {
        Optional<Section> preSection = findSectionIncludingUpStation();

        List<Station> results = Lists.newArrayList();

        preSection.ifPresent(section -> results.add(section.getUpStation()));
        addDownStationsOfSection(preSection, results);

        return results;
    }

    private void addDownStationsOfSection(Optional<Section> preSection, List<Station> results) {
        while (preSection.isPresent()) {
            Section section = preSection.get();
            results.add(section.getDownStation());

            preSection = sections.stream()
                    .filter(it -> it.getUpStation().equals(section.getDownStation()))
                    .findAny();
        }
    }

    private Optional<Section> findSectionIncludingUpStation() {
        List<Station> downStationsOfSection = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !downStationsOfSection.contains(section.getUpStation()))
                .findAny();
    }
}
