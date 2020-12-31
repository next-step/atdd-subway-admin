package nextstep.subway.line.domain;

import nextstep.subway.line.application.SectionValidationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class LineSectionsTest {

	@Autowired
	private EntityManager em;

	private Line 노선;
	private Station A역;
	private Station 추가역;
	private Station C역;

	@BeforeEach
	void setUp() {
		A역 = new Station("A");
		C역 = new Station("C");
		추가역 = new Station("추가역");
		노선 = new Line("노선", "green", A역, C역, 50);

		em.persist(A역);
		em.persist(C역);
		em.persist(추가역);
		em.persist(노선);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void addSection_역사이_상행() {
		// when
		노선.addSection(A역, 추가역, 30);

		// then
		assertThat(노선.getSortedStations()).containsExactly(A역, 추가역, C역);
	}

	@Test
	void addSection_역사이_하행() {
		// when
		노선.addSection(추가역, C역, 30);

		// then
		assertThat(노선.getSortedStations()).containsExactly(A역, 추가역, C역);
	}

	@Test
	void addSection_역사이_상행_DistanceOver() {
		assertThatThrownBy(() -> 노선.addSection(A역, 추가역, 70))
				.isInstanceOf(SectionValidationException.class)
				.hasMessageContaining("distance over");
	}

	@Test
	void addSection_상행종점() {
		// when
		노선.addSection(추가역, A역, 80);

		// then
		assertThat(노선.getSortedStations()).containsExactly(추가역, A역, C역);
	}

	@Test
	void addSection_하행종점() {
		// when
		노선.addSection(C역, 추가역, 30);

		// then
		assertThat(노선.getSortedStations()).containsExactly(A역, C역, 추가역);
	}

	@Test
	void addSection_이미추가됨() {
		// when & then
		assertThatThrownBy(() -> 노선.addSection(A역, C역, 70))
				.isInstanceOf(SectionValidationException.class)
				.hasMessageContaining("already");
	}

	@Test
	void addSection_이미추가됨역행() {
		// when & then
		assertThatThrownBy(() -> 노선.addSection(C역, A역, 70))
				.isInstanceOf(SectionValidationException.class)
				.hasMessageContaining("already");
	}
 }
