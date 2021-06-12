package nextstep.subway.domain;

import nextstep.subway.exception.ValueFormatException;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @JoinColumn(name = "down_section_id", foreignKey = @ForeignKey(name = "fk_station_down_section"))
    @OneToOne(fetch = LAZY)
    private Section downSection;

    @OneToOne(mappedBy = "downStation", fetch = LAZY)
    private Section upSection;

    @OneToMany(mappedBy = "station", orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    /**
     * 생성자
     */
    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    //테스트용 생성자
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 생성 메소드
     */
    public static Station create(String name) {
        validate(name);
        return new Station(name);
    }

    private static void validate(String name) {
        if (Strings.isBlank(name)) {
            throw new ValueFormatException("역의 이름이 존재하지 않습니다.", "name", name, null);
        }
    }

    /**
     * 연관관계 메소드
     */

    public void registerDownSection(Section downSection) {
        this.downSection = downSection;
        if (downSection != null) {
            downSection.setUpStation(this);
        }
    }

    public void setUpSection(Section upSection) {
        this.upSection = upSection;
    }

    public void addLineStation(LineStation lineStation) {
        if (!lineStations.contains(lineStation)) {
            lineStations.add(lineStation);
        }
    }

    /**
     * 그 밖의 메소드
     */
    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id(), station.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Section downSection() {
        return downSection;
    }

    public Section upSection() {
        return upSection;
    }

    public List<LineStation> lineStations() {
        return lineStations;
    }
}
