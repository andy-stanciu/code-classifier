package me.andystanciu;

public enum SolutionType {
    RAW("./solutions/raw", "./data/raw"),
    REDACTED("./solutions/redacted", "./data/redacted"),
    REDACTED_STRIPPED("./solutions/redacted-stripped", "./data/redacted-stripped"),;

    private final String solutionPath;
    private final String dataPath;

    SolutionType(String solutionPath, String dataPath) {
        this.solutionPath = solutionPath;
        this.dataPath = dataPath;
    }

    public String getSolutionPath() {
        return solutionPath;
    }

    public String getDataPath() {
        return dataPath;
    }
}
