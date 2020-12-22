package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineRequest {

	@NotBlank(message = "노선 이름을 입력해주십시오.")
	private String name;

	@NotBlank(message = "노선 색상을 입력해주십시오.")
	private String color;

	@NotNull(message = "상행 종점역을 입력해주십시오.")
	private Long upStationId;

	@NotNull(message = "하행 종점역을 입력해주십시오.")
	private Long downStationId;

	@NotNull(message = "거리를 입력해주십시오.")
	private Integer distance;

}
