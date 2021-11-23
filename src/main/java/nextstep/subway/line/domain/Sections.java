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
    }

    public void addAll(List<Section> sections) {
        for(Section section : sections) {
            add(section);
        }
    }

    public List<Section> getSections() {
        return sections;
    }

}
