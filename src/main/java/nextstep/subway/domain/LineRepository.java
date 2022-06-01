package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.NoSuchElementException;

public interface LineRepository extends JpaRepository<Line, Long> {

    default Line getById(Long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("지하철 노선이 없습니다. id: " + id));
    }
}
