package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "upStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_1"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_2"))
    private Station downStation;

    @Column(nullable = false)
    private int distance;

}
