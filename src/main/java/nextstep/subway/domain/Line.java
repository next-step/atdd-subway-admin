package nextstep.subway.domain;

import nextstep.subway.dto.LineDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private int distance;


    public Line(LineDto.Request request) {
    }

    public Line() {

    }


}
