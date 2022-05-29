package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    private Integer distance;

    @OneToOne(fetch = FetchType.LAZY)
    private Section nextSection;

    @OneToOne(fetch = FetchType.LAZY)
    private Section backSection;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Line line, Integer distance,
        Section nextSection, Section backSection) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
        this.nextSection = nextSection;
        this.backSection = backSection;
    }
}
