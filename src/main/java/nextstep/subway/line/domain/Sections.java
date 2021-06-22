package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.CanNotAddSectionException;
import nextstep.subway.exception.CanNotDeleteStateException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.RegisteredSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    public static final int MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validate(section);
        sections.stream()
                .filter(s -> s.equalsUpStation(section) || s.equalsDownStation(section))
                .findFirst()
                .ifPresent(s -> {
                    int distance = s.getDistance() - section.getDistance();
                    Section moveSection = new Section(section.getDownStation(), s.getDownStation(), distance);
                    if (s.equalsDownStation(section)) {
                        moveSection = new Section(s.getUpStation(), section.getUpStation(), distance);
                    }
                    moveSection.toLine(section.getLine());
                    sections.remove(s);
                    sections.add(moveSection);
                });
        sections.add(section);
    }

    public List<Station> stations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            sections.stream().filter(s -> isConnectUpStation(s, section))
                             .findFirst()
                             .ifPresent(s -> stations.addAll(Arrays.asList(s.getUpStation(), s.getDownStation())));
            stations.addAll(Arrays.asList(section.getUpStation(), section.getDownStation()));
        }
        return new ArrayList<>(stations);
    }

    private boolean isConnectUpStation(Section target, Section compare) {
        return !target.equals(compare) && target.getDownStation() == compare.getUpStation();
    }

    public void validate(Section section) {
        if (!sections.isEmpty() && contains(section.getUpStation()) && contains(section.getDownStation())) {
            throw new RegisteredSectionException();
        }
        if (!sections.isEmpty() &&!contains(section.getUpStation()) && !contains(section.getDownStation())) {
            throw new CanNotAddSectionException();
        }
    }

    public boolean contains(Station station) {
        return sections.stream().anyMatch(s -> s.containStation(station));
    }

    public void delete(Station station) {
        if (canNotDelete()) {
            throw new CanNotDeleteStateException();
        }
        if (!contains(station)) {
            throw new NotFoundException();
        }

        Section upSection = sections.stream().filter(s -> s.getDownStation() == station)
                                             .findFirst().map(this::remove).orElse(null);
        Section downSection = sections.stream().filter(s -> s.getUpStation() == station)
                                               .findFirst().map(this::remove).orElse(null);

        if (existSections(upSection, downSection)) {
            Section section = new Section(upSection.getUpStation(), downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance());
            sections.add(section);
        }
    }

    private boolean existSections(Section ... sections) {
        return Arrays.stream(sections).noneMatch(Objects::isNull);
    }

    private Section remove(Section section) {
        sections.remove(section);
        return section;
    }

    private boolean canNotDelete() {
        return sections.size() == MIN_SIZE;
    }
}
