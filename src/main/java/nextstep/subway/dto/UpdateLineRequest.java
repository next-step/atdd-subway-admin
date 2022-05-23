package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UpdateLineRequest {

    @JsonIgnore
    private Long id;
    private String name;
    private String color;

    public void setId(Long id) {
        this.id = id;
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
