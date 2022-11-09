package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    List<Station> findByLine_Id(Long lineId);

    List<Station> findByLine_Id_In(List<Long> lineIds);
}