package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final EntityManager em;

    public LineService(LineRepository lineRepository, EntityManager em) {
        this.lineRepository = lineRepository;
        this.em = em;
    }

//    @Transactional(readOnly = true)
//    public List<LineResponse> showAllLines() {
//        return LineResponse.ofList(lineRepository.findAll());
//    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAllLinesWithSections() {
        List<Line> lines = em.createQuery(
                        "select distinct l from Line l " +
                                "left join fetch l.sections s " +
                                "left join fetch s.station " +
                                "left join fetch s.nextStation", Line.class)
                .getResultList();
        return LineResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
    public Line findByLineIdJPQL(Long id) throws NotFoundException {
        Line line = em.createQuery(
                        "select distinct l from Line l" +
                                " left join fetch l.sections s " +
                                "left join fetch s.station " +
                                "left join fetch s.nextStation " +
                                "where l.id = :id", Line.class)
                .setParameter("id", id)
                .getSingleResult();
        return Optional.ofNullable(line).orElseThrow(() -> new NotFoundException("노선이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Line findByLineId(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("노선이 존재하지 않습니다."));
    }


    public LineResponse saveLine(LineRequest request, Station upStation, Station downStation) {
        Line line = request.toLine(upStation, downStation);
        return LineResponse.of(lineRepository.save(line));
    }

    public LineResponse findOne(Long id) {
        return LineResponse.of(findByLineIdJPQL(id));
    }

    public LineResponse update(Long id, LineRequest request) {
        final Line line = findByLineId(id);
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = findByLineId(id);
        lineRepository.deleteById(line.getId());
    }
}
