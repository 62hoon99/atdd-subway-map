package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static nextstep.subway.acceptance.station.StationPrepare.지하철역_생성_요청;
import static nextstep.subway.acceptance.station.StationPrepare.지하철역_조회_요청;
import static nextstep.subway.acceptance.station.StationPrepare.지하철역_삭제_요청;
import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:/database-init.sql")
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철역_조회_요청();
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    // TODO: 지하철역 목록 조회 인수 테스트 메서드 생성
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        지하철역_생성_요청("잠실역");
        지하철역_생성_요청("선릉역");

        List<String> 지하철역_목록 = 지하철역_조회_요청();

        assertThat(지하철역_목록).containsExactly("잠실역", "선릉역");
        assertThat(지하철역_목록).hasSize(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    // TODO: 지하철역 제거 인수 테스트 메서드 생성
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> 교대역 = 지하철역_생성_요청("교대역");

        지하철역_삭제_요청(교대역.header("Location"));

        List<String> 지하철역_목록 = 지하철역_조회_요청();
        assertThat(지하철역_목록).doesNotContain("교대역");
    }
}
