package nextstep.subway.line.application;

import static nextstep.subway.line.LineTestFixture.*;
import static nextstep.subway.station.StationTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import nextstep.subway.BaseTest;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("LineService 단위테스트")
class LineServiceTest extends BaseTest {

	@Autowired
	private LineService lineService;

	@Autowired
	private StationService stationService;

	private LineResponse exampleLine1;
	private LineResponse exampleLine2;
	private StationResponse exampleStation1;
	private StationResponse exampleStation2;
	private StationResponse exampleStation3;

	@BeforeEach
	void setup() {
		exampleStation1 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION1_NAME));
		exampleStation2 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION2_NAME));
		exampleStation3 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION3_NAME));
		exampleLine1 = lineService.saveLine(
			LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE1_COLOR, exampleStation1.getId(), exampleStation2.getId(),
				100)
		);
		exampleLine2 = lineService.saveLine(
			LineRequest.of(EXAMPLE_LINE2_NAME, EXAMPLE_LINE2_COLOR, exampleStation2.getId(), exampleStation3.getId(),
				200)
		);
	}

	@DisplayName("saveLine 메서드는 Line을 생성할 수 있다.")
	@Test
	void saveLine() {
		String name = "3호선";
		String color = "주황색";

		LineResponse lineResponse = lineService.saveLine(
			LineRequest.of(name, color, exampleStation1.getId(), exampleStation3.getId(), 300)
		);

		assertAll(
			() -> assertThat(lineResponse.getId()).isNotNull(),
			() -> assertThat(lineResponse.getName()).isEqualTo(name),
			() -> assertThat(lineResponse.getColor()).isEqualTo(color),
			() -> assertThat(lineResponse.getStations()).hasSize(2)
		);
	}

	@DisplayName("saveLine 메서드는 동일한 노선이름을 전달하면 DataIntegrityViolationException이 발생한다.")
	@Test
	void saveLineDuplicateName() {
		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> {
				lineService.saveLine(
					LineRequest.of(
						EXAMPLE_LINE1_NAME,
						EXAMPLE_LINE2_COLOR,
						exampleStation1.getId(),
						exampleStation2.getId(),
						100
					)
				);
			});
	}

	@DisplayName("getLines 메서드는 노선 목록을 가져온다.")
	@Test
	void getLines() {
		List<LineResponse> lines = lineService.getLines();
		List<String> lineNames = lines.stream()
			.map(LineResponse::getName)
			.collect(Collectors.toList());
		boolean isAllHasStation = lines.stream()
			.allMatch(lineResponse -> lineResponse.countStations() > 0);

		assertAll(
			() -> assertThat(lines).hasSize(2),
			() -> assertThat(lineNames).contains(EXAMPLE_LINE1_NAME, EXAMPLE_LINE2_NAME),
			() -> assertThat(isAllHasStation).isTrue()
		);
	}

	@DisplayName("getLineById 메서드는 노선 ID를 전달하여 노선 정보를 가져온다.")
	@Test
	void getLineById() {
		LineResponse line = lineService.getLineById(exampleLine1.getId());

		assertAll(
			() -> assertThat(line.getId()).isNotNull(),
			() -> assertThat(line.getColor()).isEqualTo(EXAMPLE_LINE1_COLOR),
			() -> assertThat(line.getStations()).hasSize(2)
		);
	}

	@DisplayName("getLineById 메서드는 없는 노선 이름 파라미터를 전달하면 LineNotFoundException이 발생한다.")
	@Test
	void getLineByIdThrow() {
		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> {
				lineService.getLineById(NOT_FOUND_ID);
			});
	}

	@DisplayName("updateLine 메서드는 대상 노선 ID와 변경될 정보를 전달하면 노선정보를 수정할 수 있다.")
	@Test
	void updateLine() {
		String changeName = "5호선";
		String changeColor = "노란색";

		LineResponse line = lineService.updateLine(
			exampleLine1.getId(),
			LineRequest.of(changeName, changeColor, exampleStation1.getId(), exampleStation2.getId(), 150)
		);

		List<String> stationNames = line.getStations()
			.stream()
			.map(StationResponse::getName)
			.collect(Collectors.toList());

		assertAll(
			() -> assertThat(line.getId()).isNotNull(),
			() -> assertThat(line.getName()).contains(changeName),
			() -> assertThat(line.getColor()).contains(changeColor),
			() -> assertThat(stationNames).contains(EXAMPLE_STATION1_NAME, EXAMPLE_STATION2_NAME)
		);
	}

	@DisplayName("updateLine 메서드는 없는 노선 ID를 전달하면 LineNotFoundException이 발생한다.")
	@Test
	void updateLineThrow() {
		String changeName = "5호선";
		String changeColor = "노란색";

		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> {
				lineService.updateLine(
					NOT_FOUND_ID,
					LineRequest.of(changeName, changeColor, exampleStation1.getId(), exampleStation2.getId(), 150)
				);
			});

	}

	@DisplayName("deleteLine 메서드는 대상 노선 ID를 전달하면 삭제할 수 있다.")
	@Test
	void deleteLine() {
		lineService.deleteLine(exampleLine2.getId());

		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> {
				lineService.getLineById(exampleLine2.getId());
			});
	}

	@DisplayName("deleteLine 메서드는 없는 노선 ID를 전달하면 LineNotFoundException이 발생한다.")
	@Test
	void deleteLineThrow() {
		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> {
				lineService.deleteLine(NOT_FOUND_ID);
			});

	}
}