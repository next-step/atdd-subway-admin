package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {


    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if(!this.sections.contains(section)) {
            this.sections.add(section);
        }

        validateSectionSize();
    }

    public void addAll(List<Section> sections) {
        for(Section section : sections) {
            add(section);
        }
    }

    public void addUpSection(Section section) {
        section.updatePosition(section.UP_SECTION);
        add(section);
    }

    public void addDownSection(Section section) {
        section.updatePosition(section.DOWN_SECTION);
        add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    private void validateSectionSize() {
        if(this.sections.size() > 2) {
            throw new IllegalArgumentException("종점역(상행, 하행) 개수가 일치하지 않습니다.");
        }
    }

}
