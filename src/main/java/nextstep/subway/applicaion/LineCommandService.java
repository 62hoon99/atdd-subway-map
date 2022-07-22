package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LineCommandService {

    private static final String LINE_NOTFOUND_MESSAGE = "해당 id의 지하철 노선이 존재하지 않습니다.";

    private final StationQueryService stationQueryService;
    private final LineRepository lineRepository;

    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(new Line(lineRequest));
        return createLineResponse(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineOrElseThrow(id);
        lineRepository.save(lineRequest.toLine(line));
    }

    public void deleteLineById(Long id) {
        findLineOrElseThrow(id);
        lineRepository.deleteById(id);
    }

    private Line findLineOrElseThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(LINE_NOTFOUND_MESSAGE));
    }

    private LineResponse createLineResponse(Line line) {
        List<StationResponse> stations = stationQueryService.findAllStations();
        return new LineResponse(line, stations);
    }

}
