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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    public Sections(Section section) {
        new Sections(Arrays.asList(section));
    }


    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Long> allStationIds() {
        List<Long> results = new ArrayList<>();
        sections.stream()
                .map(Section::allStationIds)
                .forEach(results::addAll);
        return results;
    }

    public void addSection(Section section) {
        sections.add(section);
    }
}
