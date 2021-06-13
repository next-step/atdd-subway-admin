package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import nextstep.subway.ServiceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("노선 서비스 테스트")
public class LineServiceTest extends ServiceTest {

	@Autowired
	private LineService stationService;

	private LineRequest 초록색_라인;
	private LineRequest 새로운_라인_초록색_라인과_같은_색상;
	private LineRequest 파란색_라인;

	@BeforeEach
	void 초기화() {
		초록색_라인 = new LineRequest("2호선", "#FFFFFF");
		새로운_라인_초록색_라인과_같은_색상 = new LineRequest("3호선", "#FFFFFF");
		파란색_라인 = new LineRequest("4호선", "#000000");
	}

	@Test
	void 저장() {
		//given

		//when
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//then
		assertThat(초록색_라인_응답).isNotNull();
	}

	@Test
	void 저장_존재하는_노선이름으로_저장_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(초록색_라인))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 저장_존재하는_색상으로_저장() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineResponse 새로운_라인_초록색_라인과_같은_색상_응답 = stationService.saveLine(새로운_라인_초록색_라인과_같은_색상);

		//then
		assertThat(새로운_라인_초록색_라인과_같은_색상_응답).isNotNull();
	}

	@Test
	void 저장_노선이름_null값_예외발생() {
		//given
		LineRequest 비어있는_노선이름_요청 = new LineRequest(null, "#FFFFFF");

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(비어있는_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_null값_예외발생() {
		//given
		LineRequest 비어있는_색상_요청 = new LineRequest("1호선", null);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(비어있는_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_노선이름_빈문자열_예외발생() {
		//given
		LineRequest 공백_노선이름_요청 = new LineRequest("", "#FFFFFF");

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(공백_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_빈문자열_예외발생() {
		//given
		LineRequest 공백_색상_요청 = new LineRequest("1호선", "");

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(공백_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_노선이름_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		LineRequest 너무_긴_노선이름_요청 = new LineRequest(이백오십육바이트_이름, "#FFFFFF");

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(너무_긴_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";
		LineRequest 너무_긴_색상_요청 = new LineRequest("1호선", 이백오십육바이트_색상);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveLine(너무_긴_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 목록_조회() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);
		LineResponse 파란색_라인_응답 = stationService.saveLine(파란색_라인);

		//when
		List<LineResponse> 조회된_목록 = stationService.findAllLines();

		//then
		assertThat(조회된_목록).contains(초록색_라인_응답);
		assertThat(조회된_목록).contains(파란색_라인_응답);
	}

	@Test
	void 조회() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineResponse 조회된_초록색_라인 = stationService.findLineById(초록색_라인_응답.getId());

		//then
		assertThat(조회된_초록색_라인).isEqualTo(초록색_라인_응답);
	}

	@Test
	void 조회_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> stationService.findLineById(존재하지않는_아이디))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void 조회_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> stationService.findLineById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}

	@Test
	void 수정() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		stationService.updateLine(초록색_라인_응답.getId(), 파란색_라인);
		LineResponse 수정후_초록색_라인_응답 = stationService.findLineById(초록색_라인_응답.getId());

		//then
		assertThat(수정후_초록색_라인_응답.getName()).isEqualTo(파란색_라인.getName());
		assertThat(수정후_초록색_라인_응답.getColor()).isEqualTo(파란색_라인.getColor());
	}

	@Test
	void 수정_존재하는_노선이름_변경_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);
		LineResponse 파란색_라인_응답 = stationService.saveLine(파란색_라인);

		//when

		//then
		assertThatThrownBy(() -> stationService.updateLine(파란색_라인_응답.getId(), 초록색_라인))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 수정_존재하는_색상_변경() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);
		LineResponse 파란색_라인_응답 = stationService.saveLine(파란색_라인);

		//when
		stationService.updateLine(파란색_라인_응답.getId(), 새로운_라인_초록색_라인과_같은_색상);
		LineResponse 수정후_파란색_라인_응답 = stationService.findLineById(파란색_라인_응답.getId());

		//then
		assertThat(수정후_파란색_라인_응답.getName()).isEqualTo(새로운_라인_초록색_라인과_같은_색상.getName());
		assertThat(수정후_파란색_라인_응답.getColor()).isEqualTo(새로운_라인_초록색_라인과_같은_색상.getColor());
	}

	@Test
	void 수정_노선이름_null값_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineRequest 비어있는_노선이름_요청 = new LineRequest(null, "#FFFFFF");

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 비어있는_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_null값_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineRequest 비어있는_색상_요청 = new LineRequest("1호선", null);

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 비어있는_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_노선이름_빈문자열_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineRequest 공백_노선이름_요청 = new LineRequest("", "#FFFFFF");

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 공백_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_빈문자열_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		LineRequest 공백_색상_요청 = new LineRequest("1호선", "");

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 공백_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_노선이름_255바이트_초과_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//when
		LineRequest 너무_긴_노선이름_요청 = new LineRequest(이백오십육바이트_이름, "#FFFFFF");

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 너무_긴_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_255바이트_초과_예외발생() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";

		//when
		LineRequest 너무_긴_색상_요청 = new LineRequest("1호선", 이백오십육바이트_색상);

		//then
		assertThatThrownBy(() -> stationService.updateLine(초록색_라인_응답.getId(), 너무_긴_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 삭제() {
		//given
		LineResponse 초록색_라인_응답 = stationService.saveLine(초록색_라인);

		//when
		stationService.deleteLineById(초록색_라인_응답.getId());

		//then
		assertThatThrownBy(() -> stationService.findLineById(초록색_라인_응답.getId()))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void 삭제_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> stationService.deleteLineById(존재하지않는_아이디))
			.isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	void 삭제_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> stationService.deleteLineById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}
}
