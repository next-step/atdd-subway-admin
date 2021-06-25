package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.hibernate.mapping.Collection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void initSections(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation() && oldSection.getDistance() > section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(section);
                    sections.add(new Section(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
    }

    public void forEach(Consumer<Section> section) {
        sections.forEach(section);
    }

    public List<StationResponse> getStationResponse() {
        return sections.stream()
                .flatMap(list -> list.getStations().stream()
                .map(StationResponse::of))
                .distinct()
                .collect(Collectors.toList());
    }
}
