package nextstep.subway.domain;

import static nextstep.subway.message.ErrorMessage.LINE_CHANGE_IS_NO_COLOR;
import static nextstep.subway.message.ErrorMessage.LINE_CHANGE_IS_NO_NAME;
import static nextstep.subway.message.ErrorMessage.LINE_COLOR_IS_ESSENTIAL;
import static nextstep.subway.message.ErrorMessage.LINE_NAME_IS_ESSENTIAL;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.message.ErrorMessage;
import org.springframework.util.ObjectUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {

    }


    private Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        valid(name, color);
        this.name = name;
        this.color = color;
        sections = new Sections(Section.builder().upStation(upStation)
                .downStation(downStation)
                .distance(distance).build());
    }

    public static Builder builder() {
        return new Builder();
    }

    public void changeColor(String color) {
        validEmpty(color, LINE_CHANGE_IS_NO_COLOR);
        this.color = color;
    }

    public void changeName(String name) {
        validEmpty(name, LINE_CHANGE_IS_NO_NAME);
        this.name = name;
    }

    private void valid(String name, String color) {
        validEmpty(name, LINE_NAME_IS_ESSENTIAL);
        validEmpty(color, LINE_COLOR_IS_ESSENTIAL);
    }

    private void validEmpty(String name, ErrorMessage msg) {
        if (ObjectUtils.isEmpty(name)) {
            throw new IllegalArgumentException(msg.toMessage());
        }
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

    public Sections getSections() {
        return sections;
    }

    public static class Builder {
        private String name;
        private String color;
        private Station upStation;
        private Station downStation;
        private Distance distance;

        public Line build() {
            return new Line(name, color, upStation, downStation, distance);
        }

        public Builder name(String name) {
            this.name = name;
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

        public Builder distance(Distance distance) {
            this.distance = distance;
            return this;
        }
    }

}
