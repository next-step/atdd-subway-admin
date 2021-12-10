package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Column
    private int distnace;

    protected Section() {};

    public Section(Station upStation, Station downStation, int distnace) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distnace = distnace;
    }

    public Section(Line line, Station upStation, Station downStation, int distnace) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distnace = distnace;
    }

    public Long getId() {
		return this.id;
	}

    public Line getLine() {
		return this.line;
	}

    public Station getUpStation() {
		return this.upStation;
	}

    public Station getDownStation() {
		return this.downStation;
	}

}
