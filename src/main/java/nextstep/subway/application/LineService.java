package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import nextstep.subway.exception.CannotFindException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.*;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(Long.valueOf(lineRequest.getUpLastStationId()));
        Station downStation = findStationById(Long.valueOf(lineRequest.getDownLastStationId()));

        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.of(lines);
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CannotFindException(NOT_FOUND_LINE_ERR));
        return LineResponse.from(line);
    }

    // @Transactional 이 있어야 update문 탐
    @Transactional
    public void updateLineById(Long id, LineRequest updateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new CannotFindException(NOT_FOUND_LINE_ERR));

        line.update(updateRequest);


        // save 없어도 처리
//        lineRepository.save(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new CannotFindException(NOT_FOUND_STATION_ERR));
    }

    @Transactional
    public SectionResponse.Section saveSection(Long lineId, SectionRequest sectionRequest) {
        // 이전에 저장됐던 section이 Line에 같이 조회됨
        Line line = lineRepository.findById(lineId).orElseThrow(()-> new CannotFindException(NOT_FOUND_LINE_ERR));
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        // 새로운 section에 위에서 가져온 line도 매핑
        Section section = new Section(upStation, downStation, line, sectionRequest.getDistance());

        // line에도 새로운 section 정보 등록 (양방향) -> line.save하지 않아도 transacition 종료 후 업데이트
        line.addSection(section);

        return SectionResponse.of(section);
    }
}
