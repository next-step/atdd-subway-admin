package nextstep.subway.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Comparator;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "name", unique = true, nullable = false))
    private Name name;

    @Embedded
    @AttributeOverride(name = "color", column = @Column(name = "color", nullable = false, unique = true))
    private Color color;
    @Embedded
    private SectionLineUp sectionLineUp = new SectionLineUp();

    public Line(Name name, Color color) {
        this.name = name;
        this.color = color;
    }

    protected Line() {
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public String getNameString() {
        return name.getName();
    }

    public Color getColor() {
        return color;
    }

    public String getColorString() {
        return color.getColor();
    }

    public Line updateName(Name name) {
        this.name = name;
        return this;
    }

    public Line updateColor(Color color) {
        this.color = color;
        return this;
    }

    public void addSection(Section section) {
        sectionLineUp.addSection(section);
    }

    public List<Section> getSectionList(Comparator<Section> comparator) {
        return sectionLineUp.getSectionList(comparator);
    }

    public List<Long> getStationIdsInOrder(Comparator<Section> comparator) {
        return this.sectionLineUp.getStationIds(comparator);
    }
}
