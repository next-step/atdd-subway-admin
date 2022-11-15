package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        // TODO: 구간 중복 또는 길이 관련 유효성 처리
    }
}
