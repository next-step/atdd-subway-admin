package nextstep.subway.station.application;

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
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 서비스 테스트")
public class StationServiceTest extends ServiceTest {

	@Autowired
	private StationService stationService;

	private StationRequest 홍대입구역_요청;
	private StationRequest 구로디지털단지역_요청;

	@BeforeEach
	void 초기화() {
		홍대입구역_요청 = new StationRequest("홍대입구역");
		구로디지털단지역_요청 = new StationRequest("구로디지털단지역");
	}

	@Test
	void 저장() {
		//given

		//when
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//then
		assertThat(홍대입구역_응답).isNotNull();
	}

	@Test
	void 저장_기존_존재하는_역이름_생성_예외발생() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveStation(홍대입구역_요청))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 저장_null값_예외발생() {
		//given
		StationRequest 비어있는_지하철역이름_요청 = new StationRequest(null);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveStation(비어있는_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_빈문자열_예외발생() {
		//given
		StationRequest 공백_지하철역이름_요청 = new StationRequest("");

		//when

		//then
		assertThatThrownBy(() -> stationService.saveStation(공백_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 저장_역이름_255바이트_초과_예외발생() {
		//given
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";
		StationRequest 너무_긴_지하철역이름_요청 = new StationRequest(이백오십육바이트_이름);

		//when

		//then
		assertThatThrownBy(() -> stationService.saveStation(너무_긴_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 목록_조회() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);
		StationResponse 구로디지털단지역_응답 = stationService.saveStation(구로디지털단지역_요청);

		//when
		List<StationResponse> 조회된_목록 = stationService.findAllStations();

		//then
		assertThat(조회된_목록).contains(홍대입구역_응답);
		assertThat(조회된_목록).contains(구로디지털단지역_응답);
	}

	@Test
	void 조회() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when
		StationResponse 조회된_홍대입구역 = stationService.findStationById(홍대입구역_응답.getId());

		//then
		assertThat(조회된_홍대입구역).isEqualTo(홍대입구역_응답);
	}

	@Test
	void 조회_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> stationService.findStationById(존재하지않는_아이디))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 조회_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> stationService.findStationById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}

	@Test
	void 수정() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when
		stationService.updateStation(홍대입구역_응답.getId(), 구로디지털단지역_요청);
		StationResponse 수정후_홍대입구역_응답 = stationService.findStationById(홍대입구역_응답.getId());

		//then
		assertThat(수정후_홍대입구역_응답.getName()).isEqualTo(구로디지털단지역_요청.getName());
	}

	@Test
	void 수정_존재하는_역이름으로_변경_예외발생() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);
		StationResponse 구로디지털단지역_응답 = stationService.saveStation(구로디지털단지역_요청);

		//when

		//then
		assertThatThrownBy(() -> stationService.updateStation(구로디지털단지역_응답.getId(), 홍대입구역_요청))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void 수정_null값_예외발생() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when
		StationRequest 비어있는_지하철역이름_요청 = new StationRequest(null);

		//then
		assertThatThrownBy(() -> stationService.updateStation(홍대입구역_응답.getId(), 비어있는_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_빈문자열_예외발생() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when
		StationRequest 공백_지하철역이름_요청 = new StationRequest("");

		//then
		assertThatThrownBy(() -> stationService.updateStation(홍대입구역_응답.getId(), 공백_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 수정_역이름_255바이트_초과_예외발생() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);
		String 이백오십육바이트_이름 = "역이름 또는 노선이름 255바이트 넘기려고 지은 이름입니다. 이름이 아닌 것 같지만 이름 맞습니다. "
			+ "Character Set이 UTF-8로 맞춰서 256 바이트 길이가 딱 맞는 이름입니다. 확인하지 않으셔도 됩니다.";

		//when
		StationRequest 너무_긴_지하철역이름_요청 = new StationRequest(이백오십육바이트_이름);

		//then
		assertThatThrownBy(() -> stationService.updateStation(홍대입구역_응답.getId(), 너무_긴_지하철역이름_요청))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void 삭제() {
		//given
		StationResponse 홍대입구역_응답 = stationService.saveStation(홍대입구역_요청);

		//when
		stationService.deleteStationById(홍대입구역_응답.getId());

		//then
		assertThatThrownBy(() -> stationService.findStationById(홍대입구역_응답.getId()))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 삭제_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> stationService.deleteStationById(존재하지않는_아이디))
			.isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	void 삭제_null값_아이디_예외발생() {
		//given

		//when
		Long null값_아이디 = null;

		//then
		assertThatThrownBy(() -> stationService.deleteStationById(null값_아이디))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}
}
