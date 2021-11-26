package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long from;
    private Long to;

    @ManyToOne
    private Line line;

    public Section(Long from, Long to) {
        this.from = from;
        this.to = to;
    }

    protected Section() {
    }
}
