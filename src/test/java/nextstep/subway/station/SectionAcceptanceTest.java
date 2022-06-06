package nextstep.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionAcceptanceTest extends AcceptanceTest {

	/**
	 *	When 지하철 노선에 구간 추가를 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 */
	@DisplayName("새로운 구간을 생성한다.")
	@Test
	void createSection() {

	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 사이에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 */
	@DisplayName("기존 구간 사이에 새로운 구간을 생성한다.")
	@Test
	void addSectionInMiddle() {

	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 앞 상행역에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 */
	@DisplayName("기존 구간 앞에 새로운 구간을 생성한다.")
	@Test
	void addSectionInUpStation() {

	}

	/**
	 *  Given 지하철 노선에 구간을 생성하고
	 *	When 기존 구간 뒤 하행역에 새로운 구간을 요청하면
	 * 	Then 지하철 노선에 새로운 구간이 생성된다.
	 */
	@DisplayName("기존 구간 뒤에 새로운 구간을 생성한다.")
	@Test
	void addSectionInDownStation() {

	}
}
