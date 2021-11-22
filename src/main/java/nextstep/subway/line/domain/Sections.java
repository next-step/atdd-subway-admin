package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {

        if(!this.sections.contains(section)) {
            section.updatePosition(findPosition());
            this.sections.add(section);
        }

        validateSectionSize();
    }

    public void addAll(List<Section> sections) {
        this.sections.clear();
        for(Section section : sections) {
            add(section);
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    private Double findPosition() {

        if(this.sections.isEmpty()) {
            return 65535.0;
        }

        return this.sections.get(this.sections.size() - 1).getPosition() * 2;
    }

    private void validateSectionSize() {
        if(this.sections.size() > 2) {
            throw new IllegalArgumentException("종점역(상행, 하행) 개수가 일치하지 않습니다.");
        }
    }
}
