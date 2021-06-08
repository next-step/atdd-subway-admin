package nextstep.subway.section.dto;

import nextstep.subway.line.dto.LineResponse;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LineResponse lineResponse;
}
