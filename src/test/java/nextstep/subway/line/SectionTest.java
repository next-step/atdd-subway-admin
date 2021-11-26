package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class SectionTest {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private StationRepository stationRepository;

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private SectionRepository sectionRepository;

	private List<String> tableNames;

	@BeforeEach
	public void setUp() {
		afterPropertiesSet();
		execute();
	}

	@Test
	@DisplayName("구간 생성 테스트")
	public void CreateSectionTest() {
		//given
		Section section = Section.create(saveLine("신분당선", "pink"),
			saveStation("판교역"), saveStation("양재역"), 10);

		//when
		Section savedSection = sectionRepository.save(section);
		Section findSection = sectionRepository.findById(savedSection.getId()).get();

		//then
		assertThat(savedSection).isEqualTo(findSection);
	}

	@Test
	@DisplayName("구간 삭제 테스트")
	public void DeleteSectionTest() {
		//given
		Line line = saveLine("신분당선", "pink");
		Section section = Section.create(line, saveStation("판교역"), saveStation("양재역"), 10);
		Section otherSection = Section.create(line, saveStation("분당역"), saveStation("야탑역"), 5);
		Section savedSection = sectionRepository.save(section);
		Section savedOtherSection = sectionRepository.save(otherSection);

		//when
		sectionRepository.deleteById(savedSection.getId());
		List<Section> sections = sectionRepository.findAll();
		//then
		assertThat(sections).hasSize(1);
	}

	private Station saveStation(String name) {
		return stationRepository.save(new Station(name));
	}

	private Line saveLine(String lineName, String lineColor) {
		return lineRepository.save(new Line(lineName, lineColor));
	}


	public void afterPropertiesSet() {
		tableNames = entityManager.getMetamodel().getEntities().stream()
			.filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
			.map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
			.collect(Collectors.toList());
	}

	@Transactional
	public void execute() {
		entityManager.flush();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

		for (String tableName : tableNames) {
			entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
			entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
		}

		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}
}
