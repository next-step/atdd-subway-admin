package nextstep.subway.domain.line.domain;

import nextstep.subway.domain.section.domain.Section;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getStationInOrder() {
        List<Section> sectionsResult = new ArrayList<>();

        Optional<Section> preStation = this.sections.stream()
                .filter(st -> st.getPreStation() == null)
                .findFirst();

        while (preStation.isPresent()) {
            final Section section = preStation.get();
            sectionsResult.add(section);

            preStation = sections.stream()
                    .filter(st -> st.getPreStation() != null)
                    .filter(st -> st.getPreStation().equals(section.getStation()))
                    .findFirst();
        }

        return sectionsResult;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }
}
