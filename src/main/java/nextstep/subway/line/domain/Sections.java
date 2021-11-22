package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

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
        this.sections.add(section);

        if(this.sections.size() > 2) {
            throw new IllegalArgumentException("종점역(상행, 하행) 개수가 일치하지 않습니다.");
        }
    }

    public void addAll(List<Section> sections) {

        for(Section section : sections) {
            add(section);
        }


    }
}
