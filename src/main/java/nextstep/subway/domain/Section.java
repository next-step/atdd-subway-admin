package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Line line;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Station upStation;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
