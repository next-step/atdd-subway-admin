package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @Column(name = "sections")
    private List<Section> values;

    private Sections(List<Section> values) {
        this.values = values;
    }

    public Sections() {
    }

    public List<Section> getValues() {
        return values;
    }

    public static Sections of(List<Section> sections){
        return new Sections(sections);
    }
}
