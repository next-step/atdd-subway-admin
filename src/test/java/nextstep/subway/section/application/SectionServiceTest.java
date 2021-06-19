package nextstep.subway.section.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.ServiceTest;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("섹션 서비스 테스트")
class SectionServiceTest extends ServiceTest {

	@Autowired
	private SectionService sectionService;
	@Autowired
	private StationService stationService;
	@Autowired
	private LineRepository lineRepository;

	private String 강남역_아이디;
	private String 선릉역_아이디;
	private String 강남역_선릉역_간격;
	private LineRequest 이호선_요청;
	private Line 영속화된_이호선;

	@BeforeEach
	void 초기화() {
		강남역_아이디 = 역정보_영속화_후_아이디추출("강남역");
		선릉역_아이디 = 역정보_영속화_후_아이디추출("선릉역");

		강남역_선릉역_간격 = "100";

		이호선_요청 = new LineRequest("2호선", "#FFFFFF", 강남역_아이디, 선릉역_아이디, 강남역_선릉역_간격);
		영속화된_이호선 = lineRepository.save(이호선_요청.toLine());
	}

	private String 역정보_영속화_후_아이디추출(String 역이름) {
		StationResponse 영속화된_역정보 = stationService.saveStation(new StationRequest(역이름));
		return 영속화된_역정보.getId().toString();
	}

	@Test
	void 저장() {
		//given

		//when
		Section 이호선_강남역_선릉역_섹션 = sectionService.saveSection(이호선_요청, 영속화된_이호선);

		//then
		assertThat(이호선_강남역_선릉역_섹션).isNotNull();
		assertThat(이호선_강남역_선릉역_섹션.upStation().id()).isEqualTo(Long.parseLong(강남역_아이디));
		assertThat(이호선_강남역_선릉역_섹션.downStation().id()).isEqualTo(Long.parseLong(선릉역_아이디));
		assertThat(이호선_강남역_선릉역_섹션.line()).isEqualTo(영속화된_이호선);
		assertThat(이호선_강남역_선릉역_섹션.distance()).isEqualTo(Double.parseDouble(강남역_선릉역_간격));
	}

	@Test
	void 조회() {
		//given
		Section 이호선_강남역_선릉역_섹션 = sectionService.saveSection(이호선_요청, 영속화된_이호선);

		//when
		Section 조회된_이호선_강남역_선릉역_섹션 = sectionService.findSectionById(이호선_강남역_선릉역_섹션.id());

		//then
		assertThat(조회된_이호선_강남역_선릉역_섹션).isEqualTo(이호선_강남역_선릉역_섹션);
	}

	@Test
	void 조회_존재하지않는_아이디_예외발생() {
		//given

		//when
		Long 존재하지않는_아이디 = Long.MIN_VALUE;

		//then
		assertThatThrownBy(() -> sectionService.findSectionById(존재하지않는_아이디))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 노선과_연관된_섹션_제거() {
		//given
		Section 이호선_강남역_선릉역_섹션 = sectionService.saveSection(이호선_요청, 영속화된_이호선);

		//when
		sectionService.deleteAllByLine(영속화된_이호선);

		//then
		assertThatThrownBy(() -> sectionService.findSectionById(이호선_강남역_선릉역_섹션.id()))
			.isInstanceOf(NotFoundException.class);
	}
}
