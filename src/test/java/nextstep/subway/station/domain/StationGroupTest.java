package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 그룹 테스트")
public class StationGroupTest {

	private Station 구로디지털단지;
	private Station 홍대입구;
	private List<Station> 지하철역들;

	@BeforeEach
	void 초기화() {
		구로디지털단지 = new Station("구로디지털단지");
		홍대입구 = new Station("홍대입구");
		지하철역들 = Arrays.asList(구로디지털단지, 홍대입구);
	}

	@Test
	void 생성() {
		//given

		//when
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//then
		assertThat(지하철역그룹).isNotNull();
	}

	@Test
	void 지하철역들_반환() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		List<Station> 지하철역들_리턴값 = 지하철역그룹.stations();

		//then
		assertThat(지하철역들).containsAll(지하철역들_리턴값);
		assertThat(지하철역들_리턴값).containsAll(지하철역들);
	}

	@Test
	void 사이즈() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		int 지하철역_사이즈 = 지하철역그룹.size();

		//then
		assertThat(지하철역_사이즈).isEqualTo(지하철역들.size());
	}

	@Test
	void 지하철역_포함_여부() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		boolean 지하철역_포함_여부 = 지하철역그룹.contains(홍대입구);

		//then
		assertThat(지하철역_포함_여부).isTrue();
	}

	@Test
	void 지하철역_추가() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);
		Station 신도림 = new Station("신도림");

		//when
		지하철역그룹.add(신도림);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size() + 1);
		assertThat(지하철역그룹.contains(신도림)).isTrue();
	}

	@Test
	void 이미_등록된_지하철역_추가시도_지하철역_추가되지_않음() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		지하철역그룹.add(홍대입구);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size());
		assertThat(지하철역그룹.contains(홍대입구)).isTrue();
	}

	@Test
	void 지하철역그룹들_추가() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);
		Station 신도림 = new Station("신도림");
		Station 대림 = new Station("대림");
		StationGroup 중복된_지하철역이_없는_새로운_지하철역그룹 = new StationGroup(Arrays.asList(신도림, 대림));

		//when
		지하철역그룹.addStationGroup(중복된_지하철역이_없는_새로운_지하철역그룹);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size() + 중복된_지하철역이_없는_새로운_지하철역그룹.size());
	}

	@Test
	void 지하철역그룹들_추가_중복된_지하철역들만_있는_그룹() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		지하철역그룹.addStationGroup(지하철역그룹);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size());
	}

	@Test
	void 지하철역_제거() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);

		//when
		지하철역그룹.remove(홍대입구);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size() - 1);
		assertThat(지하철역그룹.contains(홍대입구)).isFalse();
	}

	@Test
	void 그룹에_들어있지_않은_지하철역_제거_변함없음() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);
		Station 신도림 = new Station("신도림");

		//when
		지하철역그룹.remove(신도림);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size());
		assertThat(지하철역그룹.contains(신도림)).isFalse();
	}

	@Test
	void 지하철역그룹_제거() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);
		StationGroup 동일한_지하철역그룹 = new StationGroup(지하철역들);

		//when
		지하철역그룹.removeStationGroup(동일한_지하철역그룹);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(0);
		assertThat(지하철역그룹.contains(홍대입구)).isFalse();
		assertThat(지하철역그룹.contains(구로디지털단지)).isFalse();
	}

	@Test
	void 역이_하나도_겹치지_않는_지하철역그룹_제거_변함없음() {
		//given
		StationGroup 지하철역그룹 = new StationGroup(지하철역들);
		Station 신도림 = new Station("신도림");
		Station 대림 = new Station("대림");
		StationGroup 하나도_겹치지_않는_지하철역그룹 = new StationGroup(Arrays.asList(신도림, 대림));

		//when
		지하철역그룹.removeStationGroup(하나도_겹치지_않는_지하철역그룹);

		//then
		assertThat(지하철역그룹.size()).isEqualTo(지하철역들.size());
		assertThat(지하철역그룹.contains(홍대입구)).isTrue();
		assertThat(지하철역그룹.contains(구로디지털단지)).isTrue();
	}
}
