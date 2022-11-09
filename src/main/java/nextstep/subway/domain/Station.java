package nextstep.subway.domain;

import nextstep.subway.message.ExceptionMessage;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station from(String name) {
        validate(name);
        return new Station(name);
    }

    private static void validate(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addTo(Line line) {
        this.line = line;
    }

    public void removeFromLine() {
        this.line = null;
    }

    public Long getLineId() {
        return line.getId();
    }
}
