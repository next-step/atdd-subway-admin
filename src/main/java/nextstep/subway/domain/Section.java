package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Line line;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;
    @Column(nullable = false)
    private long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean add(Section section) {
        if (this.upStation == section.upStation) {
            addPrevious(section);
            return true;
        }
        if (this.downStation == section.downStation) {
            addNext(section);
            return true;
        }
        return false;
    }

    private void addPrevious(Section section) {
        if (this.distance <= section.distance) {
            throw new IllegalArgumentException();
        }
        this.upStation = section.downStation;
        this.distance -= section.distance;
    }

    private void addNext(Section section) {
        if (this.distance <= section.distance) {
            throw new IllegalArgumentException();
        }
        this.downStation = section.upStation;
        this.distance -= section.distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
