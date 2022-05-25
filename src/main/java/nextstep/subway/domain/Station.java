package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "upStation", fetch = FetchType.LAZY)
    private List<Line> upLines = new ArrayList<>();

    @OneToMany(mappedBy = "downStation", fetch = FetchType.LAZY)
    private List<Line> downLines = new ArrayList<>();

    private Integer distance;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<Line> getUpLines() {
        return upLines;
    }

    public List<Line> getDownLines() {
        return downLines;
    }

    public List<Line> getAllLines() {
        List<Line> lines = new ArrayList<>();
        lines.addAll(upLines);
        lines.addAll(downLines);
        return lines;
    }

    public void addUpLine(Line line) {
        upLines.add(line);
    }

    public void downLine(Line line) {
        downLines.add(line);
    }
}
