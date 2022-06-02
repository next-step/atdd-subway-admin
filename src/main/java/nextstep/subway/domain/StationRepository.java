package nextstep.subway.domain;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.NoSuchElementException;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    default Station getById(Long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("지하철역이 없습니다. id: " + id));
    }

    default Station saveOne(Station station) {
        try {
            return save(station);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("이미 존재하는 지하철역입니다. name: " + station.getName());
        }
    }
}
