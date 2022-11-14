package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void updateNameAndColor(Long id, LineRequest lineRequest) {
        Line originLine = findLineById(id);
        originLine.changeNameAndColor(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());
        line.addSections(Section.makeInitialSections(findStationById(lineRequest.getUpStationId()),
                findStationById(lineRequest.getDownStationId()), lineRequest.getDistance()));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public void deleteById(Long id) {
        Line line = findLineById(id);
        sectionRepository.deleteByLine(line);
        lineRepository.delete(findLineById(id));
    }

    private Station findStationById(Long lineRequest) {
        return stationRepository.findById(lineRequest)
                .orElseThrow(NoResultException::new);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NoResultException::new);
    }
}
