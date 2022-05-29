package nextstep.subway.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    private Line line;
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
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
