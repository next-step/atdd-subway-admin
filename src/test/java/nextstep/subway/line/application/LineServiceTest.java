package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import nextstep.subway.ServiceTest;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("노선 서비스 테스트")
public class LineServiceTest extends ServiceTest {

	@Autowired
	private LineService lineService;
	@Autowired
	private StationService stationService;

	private LineRequest 이호선;
	private LineRequest 삼호선_이호선과_같은_색상;
	private LineRequest 칠호선_순서뒤바꿈;
	private LineRequest 사호선;

	private String 강남역_아이디;
	private String 선릉역_아이디;
	private String 고속터미널역_아이디;
	private String 교대역_아이디;
	private String 이수역_아이디;
	private String 사당역_아이디;

	private String 강남역_선릉역_간격;
	private String 고속터미널역_교대역_간격;
	private String 이수역_사당역_간격;
	private String 이수역_고속터미널_간격;

	@BeforeEach
	void 초기화() {
		강남역_아이디 = 역정보_영속화_후_아이디추출("강남역");
		선릉역_아이디 = 역정보_영속화_후_아이디추출("선릉역");
		고속터미널역_아이디 = 역정보_영속화_후_아이디추출("고속터미널역");
		교대역_아이디 = 역정보_영속화_후_아이디추출("교대역");
		이수역_아이디 = 역정보_영속화_후_아이디추출("이수역");
		사당역_아이디 = 역정보_영속화_후_아이디추출("사당역");

		강남역_선릉역_간격 = "100";
		고속터미널역_교대역_간격 = "200";
		이수역_사당역_간격 = "300";
		이수역_고속터미널_간격 = "400";

		이호선 = new LineRequest("2호선", "#FFFFFF", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);
		삼호선_이호선과_같은_색상 = new LineRequest("3호선", "#FFFFFF", 고속터미널역_아이디, 교대역_아이디, 고속터미널역_교대역_간격);
		칠호선_순서뒤바꿈 = new LineRequest("7호선", "#FFF000", 이수역_아이디, 고속터미널역_아이디, 이수역_고속터미널_간격);
		사호선 = new LineRequest("4호선", "#000000", 이수역_아이디, 사당역_아이디, 이수역_사당역_간격);
	}

	private String 역정보_영속화_후_아이디추출(String 역이름) {
		StationResponse 영속화된_역정보 = stationService.saveStation(new StationRequest(역이름));
		return 영속화된_역정보.getId().toString();
	}

	@Test
	void 저장() {
		//given

		//when
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//then
		assertThat(이호선_응답).isNotNull();
		노선에_역_정보가_포함되어_있음(이호선, 이호선_응답);
	}

	private void 노선에_역_정보가_포함되어_있음(LineRequest 저장요청, LineResponse 저장응답) {
		List<Long> 입력된_역_아이디들 = Arrays.asList(저장요청.getUpStationId(), 저장요청.getDownStationId());
		List<Long> 응답된_역_아이디들 = 저장응답.getStations().stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		assertThat(응답된_역_아이디들).containsSequence(입력된_역_아이디들);
	}

	@Test
	void 저장_존재하는_노선이름으로_저장_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(이호선))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 저장_존재하는_색상으로_저장() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineResponse 삼호선_이호선과_같은_색상_응답 = lineService.saveLine(삼호선_이호선과_같은_색상);

		//then
		assertThat(삼호선_이호선과_같은_색상_응답).isNotNull();
	}

	@Test
	void 저장_노선이름_null값_예외발생() {
		//given
		LineRequest 비어있는_노선이름_요청 = new LineRequest(null, "#FFFFFF", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(비어있는_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_null값_예외발생() {
		//given
		LineRequest 비어있는_색상_요청 = new LineRequest("1호선", null, 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(비어있는_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_노선이름_빈문자열_예외발생() {
		//given
		LineRequest 공백_노선이름_요청 = new LineRequest("", "#FFFFFF", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(공백_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_빈문자열_예외발생() {
		//given
		LineRequest 공백_색상_요청 = new LineRequest("1호선", "", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(공백_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_노선이름_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		LineRequest 너무_긴_노선이름_요청 = new LineRequest(이백오십육바이트_이름, "#FFFFFF", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(너무_긴_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_색상_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";
		LineRequest 너무_긴_색상_요청 = new LineRequest("1호선", 이백오십육바이트_색상, 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);

		//when

		//then
		assertThatThrownBy(() -> lineService.saveLine(너무_긴_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 목록_조회() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);
		LineResponse 사호선_응답 = lineService.saveLine(사호선);
		LineResponse 이호선_순서뒤바꿈_응답 = lineService.saveLine(칠호선_순서뒤바꿈);

		//when
		List<LineResponse> 조회된_목록 = lineService.findAllLines();

		//then
		assertThat(조회된_목록).contains(이호선_응답);
		assertThat(조회된_목록).contains(사호선_응답);
		assertThat(조회된_목록).contains(이호선_순서뒤바꿈_응답);
	}

	@Test
	void 조회() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineResponse 조회된_이호선 = lineService.findLineById(이호선_응답.getId());

		//then
		assertThat(조회된_이호선).isEqualTo(이호선_응답);
	}

	@Test
	void 조회_순서뒤바꿈() {
		//given
		LineResponse 칠호선_순서뒤바꿈_응답 = lineService.saveLine(칠호선_순서뒤바꿈);

		//when
		LineResponse 조회된_이호선_순서뒤바꿈 = lineService.findLineById(칠호선_순서뒤바꿈_응답.getId());

		//then
		assertThat(조회된_이호선_순서뒤바꿈).isEqualTo(칠호선_순서뒤바꿈_응답);
		assertThat(조회된_이호선_순서뒤바꿈.getStations().stream().map(StationResponse::getId).collect(Collectors.toList()))
			.containsSequence(
				Arrays.asList(이수역_아이디, 고속터미널역_아이디).stream().map(Long::parseLong).collect(Collectors.toList()));
	}

	@Test
	void 조회_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> lineService.findLineById(존재하지않는_아이디))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 조회_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> lineService.findLineById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}

	@Test
	void 수정() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		lineService.updateLine(이호선_응답.getId(), 사호선);
		LineResponse 수정후_이호선_응답 = lineService.findLineById(이호선_응답.getId());

		//then
		assertThat(수정후_이호선_응답.getName()).isEqualTo(사호선.getName());
		assertThat(수정후_이호선_응답.getColor()).isEqualTo(사호선.getColor());
	}

	@Test
	void 수정_존재하는_노선이름_변경_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);
		LineResponse 사호선_응답 = lineService.saveLine(사호선);

		//when

		//then
		assertThatThrownBy(() -> lineService.updateLine(사호선_응답.getId(), 이호선))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 수정_존재하는_색상_변경() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);
		LineResponse 사호선_응답 = lineService.saveLine(사호선);

		//when
		lineService.updateLine(사호선_응답.getId(), 삼호선_이호선과_같은_색상);
		LineResponse 수정후_사호선_응답 = lineService.findLineById(사호선_응답.getId());

		//then
		assertThat(수정후_사호선_응답.getName()).isEqualTo(삼호선_이호선과_같은_색상.getName());
		assertThat(수정후_사호선_응답.getColor()).isEqualTo(삼호선_이호선과_같은_색상.getColor());
	}

	@Test
	void 수정_노선이름_null값_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineRequest 비어있는_노선이름_요청 = new LineRequest(null, "#FFFFFF");

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 비어있는_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_null값_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineRequest 비어있는_색상_요청 = new LineRequest("1호선", null);

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 비어있는_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_노선이름_빈문자열_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineRequest 공백_노선이름_요청 = new LineRequest("", "#FFFFFF");

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 공백_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_빈문자열_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		LineRequest 공백_색상_요청 = new LineRequest("1호선", "");

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 공백_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_노선이름_255바이트_초과_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//when
		LineRequest 너무_긴_노선이름_요청 = new LineRequest(이백오십육바이트_이름, "#FFFFFF");

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 너무_긴_노선이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_색상_255바이트_초과_예외발생() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);
		String 이백오십육바이트_색상 = "색상 255바이트 넘기려고 지은 색상입니다. 색상이 아닌 것 같지만 색상 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 색상입니다. 색상들의 길이는 확인하지 않으셔도 됩니다.";

		//when
		LineRequest 너무_긴_색상_요청 = new LineRequest("1호선", 이백오십육바이트_색상);

		//then
		assertThatThrownBy(() -> lineService.updateLine(이호선_응답.getId(), 너무_긴_색상_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 삭제() {
		//given
		LineResponse 이호선_응답 = lineService.saveLine(이호선);

		//when
		lineService.deleteLineById(이호선_응답.getId());

		//then
		assertThatThrownBy(() -> lineService.findLineById(이호선_응답.getId()))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 삭제_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> lineService.deleteLineById(존재하지않는_아이디))
			.isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	void 삭제_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> lineService.deleteLineById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}
}
