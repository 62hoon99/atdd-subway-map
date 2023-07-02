package subway.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        //when
        final String 지하철역 = "지하철역";
        final String 새로운지하철역 = "새로운지하철역";

        final String 신분당선 = "신분당선";
        LineCreateRequest request = new LineCreateRequest(
                신분당선,
                "bg-red-600",
                getIdFromResponse(StationAcceptanceTest.generateSubwayStation(지하철역)),
                getIdFromResponse(StationAcceptanceTest.generateSubwayStation(새로운지하철역)),
                10L
        );
        ExtractableResponse<Response> response = generateSubwayLine(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //then
        assertThat(getSubwayLines()).containsExactly(신분당선);
    }

    private Long getIdFromResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("id", Long.class);
    }

    private List<String> getSubwayLines() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name");
    }

    private ExtractableResponse<Response> generateSubwayLine(LineCreateRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }
}
