package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.BadParameterException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionTest {
	@Test
	@DisplayName("updateByUpSection 성공")
	void updateByUpSection_success() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");

		Section section = new Section(station1, station2, 5);
		Section upSection = new Section(station1, station3, 2);
		section.updateByUpSection(upSection);

		assertThat(section.getUpStation()).isEqualTo(upSection.getDownStation());
		assertThat(section.getDistance().get()).isEqualTo(3);
	}

	@Test
	@DisplayName("updateByDownSection 성공")
	void updateByDownSection_success() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");

		Section section = new Section(station1, station2, 5);
		Section downSection = new Section(station3, station2, 3);
		section.updateByDownSection(downSection);

		assertThat(section.getDownStation()).isEqualTo(downSection.getUpStation());
		assertThat(section.getDistance().get()).isEqualTo(2);
	}

	@Test
	@DisplayName("updateByUpSection이 같거나 긴 거리로 요청되었을 때 예외")
	void updateByUpSection_upSectionHasTooFarDistance_Exception() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");

		Section section = new Section(station1, station2, 5);
		Section upSection = new Section(station1, station3, 5);
		Section upSection2 = new Section(station1, station3, 10);

		assertThatThrownBy(() -> section.updateByUpSection(upSection))
			.isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");

		assertThatThrownBy(() -> section.updateByUpSection(upSection2))
			.isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}

	@Test
	@DisplayName("updateByDownSection이 같거나 긴 거리로 요청되었을 때 예외")
	void updateByUpSection_downSectionHasTooFarDistance_Exception() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");

		Section section = new Section(station1, station2, 5);
		Section downSection = new Section(station3, station2, 5);
		Section downSection2 = new Section(station3, station2, 10);

		assertThatThrownBy(() -> section.updateByDownSection(downSection))
			.isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");

		assertThatThrownBy(() -> section.updateByDownSection(downSection2))
			.isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}
}
