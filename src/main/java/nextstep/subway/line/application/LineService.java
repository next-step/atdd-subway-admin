package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.Distance;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    @Autowired
    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    /**
     * 지하철 노선을 저장합니다.
     * @param request
     * @return
     */
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(this.lineRequestToLine(request));
        return LineResponse.of(persistLine);
    }

    /**
     * 지하철 노선을 수정합니다.
     * @param request
     */
    public void updateLine(Long id, LineRequest request) {
        Line line = findLineById(id);
        line.update(this.lineRequestToLine(request));
//        this.lineRepository.save(line);
    }

    /**
     * LineRequest를 Line으로 변환합니다.
     * 이때 구간 정보가 있는 경우 Line에 구간을 추가해줍니다.
     * @param lineRequest
     * @return
     */
    private Line lineRequestToLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        line.addSections(this.stationService.findStation(lineRequest.getUpStationId())
                , this.stationService.findStation(lineRequest.getDownStationId())
                , lineRequest.getDistance());
        return line;
    }

    /**
     * 모든 지하철 노선을 검색합니다.
     * @return
     */
    public List<LineResponse> findAllLines() {
        return this.lineRepository.findAll().stream()
                            .map(LineResponse::of)
                            .collect(Collectors.toList());
    }

    /**
     * ID로 지하철 노선을 검색합니다.
     * @param id
     * @return
     */
    public Optional<LineResponse> findLine(Long id) {
        return this.lineRepository.findById(id).map(LineResponse::of);
    }

    /**
     * ID로 지하철 노선을 삭제합니다.
     * @param id
     */
    public void deleteLine(Long id) {
        this.lineRepository.delete(findLineById(id));
    }

    /**
     * 해당 ID로 노선을 찾고, 존재하지 않으면 예외를 발생시킵니다.
     * @param id
     * @return
     */
    private Line findLineById(Long id) {
        return this.lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cannot find line."));
    }
}
