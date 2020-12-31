package nextstep.subway.line.application;

import nextstep.subway.common.exception.EntityNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findByStationId(request.getUpStationId());
        Station downStation = stationService.findByStationId(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return LineResponse.of(findByLineId(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findByLineId(id);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findByLineId(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 지하철 노선을 찾을 수가 없습니다."));
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationService.findByStationId(sectionRequest.getUpStationId());
        Station downStation = stationService.findByStationId(sectionRequest.getDownStationId());
        Line line = findByLineId(id);
        line.addSection(new Section(upStation, downStation, sectionRequest.getDistance()));
    }
}
