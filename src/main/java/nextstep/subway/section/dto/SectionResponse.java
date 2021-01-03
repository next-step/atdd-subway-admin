package nextstep.subway.section.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SectionResponse {
    private Long id;
    private String name;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    protected SectionResponse() {
    }

    public SectionResponse(Long id, String name, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
