package subway.ui;

public class LineUpdateRequest {

    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}
