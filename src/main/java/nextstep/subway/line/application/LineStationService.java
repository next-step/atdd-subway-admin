package nextstep.subway.line.application;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineStationCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class LineStationService {

    public LineStationService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    private final LineRepository lineRepository;

    public void addSectionToLine(long lineId, LineStationCreateRequest request) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
        LineStation lineStation = buildLineStation(line, request);
        line.addLineStation(lineStation);
    }

    public void deleteSectionFromLine(Long lineId, Long lineStationId){
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
        line.delete(lineStationId);
    }

    private LineStation buildLineStation(Line line, LineStationCreateRequest request) {
        if(line.getSameLineStation(request.getUpStationId()).isPresent()) {
            return LineStation.of(request.getUpStationId(), request.getDownStationId(), request.getDistance());
        }
        Optional<LineStation> downStation = line.getSameLineStation(request.getDownStationId());
        if(downStation.isPresent()){
           return buildDownLineStation(downStation.get(), request);
        }
        throw new IllegalArgumentException("상행역 혹은 하행역 중 하나를 포함해야합니다.");
    }

    private LineStation buildDownLineStation(LineStation downStation, LineStationCreateRequest request){
        if(downStation.isFirst()){
            return LineStation.ofFirst(request.getUpStationId());
        }
        int distance = downStation.getDistance() - request.getDistance();
        if(distance <= 0){
            throw new IllegalArgumentException("기존 구간 사이보다 크거나 같을 수 없습니다.");
        }
        return LineStation.of(downStation.getUpStationId(), request.getUpStationId(), distance);
    }

}
