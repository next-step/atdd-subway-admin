package nextstep.subway.section.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    public Sections(Section section) {
        this.sections = Arrays.asList(section);
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Long> allDistinctStationIds() {
        List<Long> results = new ArrayList<>();
        sections.forEach(it -> addStationIds(results, it));
        return results;
    }

    private List<Long> addStationIds(List<Long> results, Section it) {
        for (Long stationId : it.allStationIds()) {
            if(!results.contains(stationId)) results.add(stationId);
        }
        return results;
    }

    public void addSection(Section newSection) {
        sections.stream()
                .filter(down -> down.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .ifPresent(orgSection -> {
                    sections.add(new Section(orgSection.getUpStationId(), newSection.getUpStationId(), orgSection.getDistance() - newSection.getDistance()));
                    sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                    sections.remove(orgSection);
                });

        sections.stream()
                .filter(up -> up.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .ifPresent(org -> {
                    sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                    sections.add(new Section(newSection.getDownStationId(), org.getDownStationId(), org.getDistance() - newSection.getDistance()));
                    sections.remove(org);
                });
    }
}
