package nextstep.subway.domain;


import java.util.List;
import java.util.Objects;
import javassist.NotFoundException;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    protected Line(){ }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void update(LineRequest lineRequest){
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void addSection(Section section){
        section.addLine(this);
        sections.add(section);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName())
                && Objects.equals(getColor(), line.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor());
    }

    public List<Station> getOrderedStations() {
        return sections.getOrderedStations();
    }

    public Sections getSections() {
        return sections;
    }


    public void removeStation(Station station) throws NotFoundException {
        this.sections.deleteStation(station);
    }
}
