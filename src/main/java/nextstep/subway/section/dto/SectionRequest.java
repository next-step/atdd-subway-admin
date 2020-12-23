package nextstep.subway.section.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionRequest {

    @NotNull(message = "상행 역을 입력해주십시오.")
    private Long upStationId;

    @NotNull(message = "하행 역을 입력해주십시오.")
    private Long downStationId;

    @NotNull(message = "거리를 입력해주십시오.")
    private Integer distance;

}
