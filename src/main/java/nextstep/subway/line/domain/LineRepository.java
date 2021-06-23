package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    List<Line> findByNameContainingAndColorContaining(String name, String color);

    default Line findByIdWithUnWrapped(Long id) {
        Optional<Line> optionalStation = findById(id);
        return optionalStation.orElseThrow(()-> new LineNotFoundException(id));
    }
}
