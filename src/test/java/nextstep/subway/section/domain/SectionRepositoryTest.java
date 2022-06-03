package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SectionRepositoryTest {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("구간을 저장한다")
    @Test
    void save() {
        Section actual = sectionRepository.save(new Section("2", "1", 10));
        entityManager.clear();

        Section expected = sectionRepository.findById(1L).orElseThrow(IllegalAccessError::new);

        assertThat(actual).isEqualTo(expected);
    }
}
