package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest request) {
        LineResponse response = lineService.createLine(request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(value = "id") Long lineId) {
        return ResponseEntity.ok().body(lineService.showLine(lineId));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable(value = "id") Long lineId,
                                                   @RequestBody LineUpdateRequest request) {
        return ResponseEntity.ok().body(lineService.updateLine(lineId, request));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable(value = "id") Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable(value = "id") Long lineId,
                                           @RequestBody SectionRequest request) {
        lineService.addSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable(value = "id") Long lineId,
                                              @RequestParam(value = "stationId") Long stationId) {

        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
