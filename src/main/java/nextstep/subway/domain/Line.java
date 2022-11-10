package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "upstream_station_id",nullable = false)
    private Station upstreamStation;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "downstream_station_id",nullable = false)
    private Station downstreamStation;


    public Line() {
    }

    public Line(String name,String color,Station upstreamStation,Station downstreamStation) {
        this.name = name;
        this.color = color;
        this.upstreamStation = upstreamStation;
        this.downstreamStation = downstreamStation;
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
}
