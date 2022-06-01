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

    @Embedded
    private Distance distance;

    protected Line() {

    }

    public Line(String name, String color, Distance distance) {
        valid(name, color);
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line(String name, String color, int distance) {
        this(name, color, Distance.of(distance));
    }

    public Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        valid(name, color);
        this.name = name;
        this.color = color;
        sections = new Sections(Section.builder().upStation(upStation)
                .downStation(downStation)
                .distance(distance).build());
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

    public int getDistance() {
        return distance.value();
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

}
