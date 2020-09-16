package dev.ruivieira.ccfd.routes.messages.v0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PredictionData {

    @JsonProperty("names")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> names = new ArrayList<>();

    public void setOutcomes(int outcomes) {
        this.outcomes = outcomes;
    }

    //@JsonProperty("tensor")
    //private List<Double> outcomes = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    @JsonProperty("tensor")
    public int getOutcomes() {
        return outcomes.get(1);
    }

}
