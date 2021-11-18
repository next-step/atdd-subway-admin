package nextstep.subway.line.domain;

import static nextstep.subway.line.SectionFixture.*;
import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("구간들")
class SectionsTest {

	@DisplayName("구간들을 생성한다.")
	@Test
	void of() {
		// given
		List<Section> values = Arrays.asList(강남역_역삼역_구간(), 역삼역_선릉역_구간(), 선릉역_삼성역_구간());

		// when
		Sections sections = Sections.of(values);

		// then
		assertAll(
			() -> assertThat(sections).isNotNull(),
			() -> assertThat(sections.size()).isEqualTo(values.size())
		);
	}

	@DisplayName("구간들 내 역들을 상행역부터 하행역순으로 가져온다.")
	@Test
	void getStations() {
		// given
		Sections sections = Sections.of(Arrays.asList(선릉역_삼성역_구간(), 강남역_역삼역_구간(), 역삼역_선릉역_구간()));

		// when
		List<Station> stations = sections.getStations();

		// then
		assertThat(stations).isEqualTo(Arrays.asList(강남역(), 역삼역(), 선릉역(), 삼성역()));
	}
}
