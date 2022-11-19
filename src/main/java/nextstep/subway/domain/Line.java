package nextstep.subway.domain;

import java.util.Collections;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    private Line(Builder builder) {
        this.name = builder.name;
        this.color = builder.color;
        Section section = Section.from(builder.upStation, builder.downStation, Distance.from(builder.distance));
        section.addLine(this);
        this.sections = Sections.from(Collections.singletonList(section));
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public static Builder builder() {
        return new Builder();
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

    public Set<Station> findAssignedStations() {
        return sections.assignedOrderedStation();
    }

    public void addSection(Section section) {
        this.sections.add(section);
        section.addLine(this);
    }

    public void deleteSection(Station station) {
        this.sections.delete(station);
    }

    public static class Builder {
        private String name;
        private int distance;
        private String color;
        private Station upStation;
        private Station downStation;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
