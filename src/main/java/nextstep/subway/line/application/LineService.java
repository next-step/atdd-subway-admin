package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.StationRepository;
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
    private final StationRepository stationRepository;

    @Autowired
    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
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
     * 노선 생성 요청시 종점역 정보가 있는 경우 노선에 추가합니다.
     * @param lineRequest
     * @return
     */
    private Line lineRequestToLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        this.stationRepository.findById(lineRequest.getUpStationId()).ifPresent(line::addStations);
        this.stationRepository.findById(lineRequest.getDownStationId()).ifPresent(line::addStations);
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
        this.lineRepository.delete(this.lineRepository.getOne(id));
    }

}
