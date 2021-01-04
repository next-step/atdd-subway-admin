package nextstep.subway.section.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {

    @NotNull
    private long upStationId;
    @NotNull
    private long downStationId;
    @Min(1)
    private int distance;
}
