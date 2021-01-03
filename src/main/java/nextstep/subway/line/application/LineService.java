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
     * 지하철 노선을 수정합니다.
     * @param request
     */
    public void updateLine(Long id, LineRequest request) {
        Line line = findLineById(id);
        line.update(this.lineRequestToLine(request));
        LineResponse.of(lineRepository.save(line));
    }

    /**
     * LineRequest를 Line으로 변환합니다.
     * 이때 종점역 정보가 있는 경우 Line에 station을 추가해줍니다.
     * @param lineRequest
     * @return
     */
    private Line lineRequestToLine(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        addStationToLine(line, lineRequest.getUpStationId());
        addStationToLine(line, lineRequest.getDownStationId());
        return line;
    }

    /**
     * 노선에 요청에 있는 지하철 역을 추가합니다.
     * @param line
     * @param stationId
     */
    private void addStationToLine(Line line, Long stationId) {
        if (stationId != null) {
            this.stationRepository.findById(stationId).ifPresent(line::addStations);
        }
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
