package nextstep.subway.application;

import javassist.NotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    public Line findById(Long id) throws NotFoundException {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id + " 에 해당하는 지하철 노선이 존재하지 않습니다."));
    }

    @Transactional
    public Line saveLine(LineRequest.Create lineCreateRequest) throws NotFoundException {
        Station upStation = null;
        Station downStation = null;

        if (lineCreateRequest.getUpStationId() != null) {
            upStation = stationService.getStation(lineCreateRequest.getUpStationId());
        }
        if (lineCreateRequest.getDownStationId() != null) {
            downStation = stationService.getStation(lineCreateRequest.getDownStationId());
        }

        return lineRepository.save(lineCreateRequest.toLine(upStation, downStation));
    }

    @Transactional
    public void modifyLine(Long id, LineRequest.Modification modify) throws NotFoundException {
        lineRepository.save(findById(id).modify(modify));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
