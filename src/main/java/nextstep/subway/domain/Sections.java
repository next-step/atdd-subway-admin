package nextstep.subway.domain;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany
    @JoinColumn(name = "line_id")
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }
}
