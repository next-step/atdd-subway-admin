package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    Optional<Station> findByName(String name);

    default Station getById(Long id) throws NoSuchElementException{
        return findById(id).orElseThrow(() -> new NoSuchElementException("지하철역이 없습니다. id: " + id));
    }
}
