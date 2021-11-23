package nextstep.subway.line.application;

import nextstep.subway.common.exception.DuplicateParameterException;
import nextstep.subway.common.exception.LineNotFoundException;
import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.*;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final EntityManager em;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, EntityManager em) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.em = em;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAllLinesWithSections() {
        final List<Line> lines = em.createQuery(
                        "select distinct l from Line l " +
                                "left join fetch l.sections s " +
                                "left join fetch s.sections sc " +
                                "left join fetch sc.station " +
                                "left join fetch sc.nextStation", Line.class)
                .getResultList();
        return LineResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
    public Line findByLineId(Long id) throws LineNotFoundException {
        final Line line = em.createQuery(
                        "select distinct l from Line l" +
                                " left join fetch l.sections s " +
                                "left join fetch s.sections sc " +
                                "left join fetch sc.station " +
                                "left join fetch sc.nextStation " +
                                "where l.id = :id", Line.class)
                .setParameter("id", id)
                .getSingleResult();
        return Optional.ofNullable(line).orElseThrow(LineNotFoundException::new);
    }

    public LineResponse saveLine(LineRequest request) {
        checkStationIds(request);

        Line line = request.toLine();
        line.addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        return LineResponse.of(lineRepository.save(line));
    }

    public LineResponse saveSection(Long id, SectionRequest request) {
        checkStationIds(request);

        Line line = findByLineId(id);
        line.addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        return LineResponse.of(lineRepository.save(line));
    }

    private Section createSection(Long upStationId, Long downStationId, int distance) {
        final Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(StationNotFoundException::new);
        final Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(StationNotFoundException::new);

        return new Section(upStation, downStation, Distance.of(distance));
    }

    public LineResponse findOne(Long id) {
        return LineResponse.of(findByLineId(id));
    }

    public LineResponse update(Long id, LineRequest request) {
        final Line line = findByLineId(id);
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        final Line line = findByLineId(id);
        lineRepository.deleteById(line.getId());
    }

    private void checkStationIds(BaseRequest request) {
        if (request.hasDuplicateStations()) {
            throw new DuplicateParameterException("상행, 하행역은 중복될 수 없습니다.");
        }
    }


}
