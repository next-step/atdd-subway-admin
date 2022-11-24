package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {

        if (sections.isEmpty()) {
            setInitSection(newSection);
            return;
        }



    }

    public List<Section> getSectionList() {
        return sections;
    }

    private void setInitSection(Section newSection) {
        sections.add(new Section(newSection.getLine(), null, newSection.getUpStation(), 0));
        sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        sections.add(new Section(newSection.getLine(), newSection.getDownStation(), null, 0));
    }
}
