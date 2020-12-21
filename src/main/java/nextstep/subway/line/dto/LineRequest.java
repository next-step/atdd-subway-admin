package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineRequest {

	@NotBlank(message = "노선 이름을 입력해주십시오.")
	private String name;

	@NotBlank(message = "노선 색상을 입력해주십시오.")
	private String color;

	public Line toLine() {
		return new Line(name, color);
	}
}
