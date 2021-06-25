package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.message.DomainExceptionMessage.NOT_FOUND_MESSAGE;

public interface LineRepository extends JpaRepository<Line, Long> {

    List<Line> findByNameContainingAndColorContaining(String name, String color);

    default Line findByIdWithUnWrapped(Long id) {
        Optional<Line> optionalStation = findById(id);
        return optionalStation.orElseThrow(()-> new EntityNotFoundException(NOT_FOUND_MESSAGE.apply(id, "노선")));
    }
}
