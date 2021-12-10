package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
@Table(name = "station")
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    // TODO 환승역의 경우 노선이 여러개일 수 있음
    // ManyToMany를 사용하지 않고 할 수 있는 방법을 찾아보기
    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Line line;

    public Station() {
    }

    public Station(String name) {
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name.printName();
    }
}
