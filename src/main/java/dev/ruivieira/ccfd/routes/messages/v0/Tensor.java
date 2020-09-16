package dev.ruivieira.ccfd.routes.messages.v0;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;
import java.util.List;


    @JsonRootName(value = "tensor")
    public class Tensor {

        @JsonProperty(value="values")
        private List<Double> outcomes;
        
        @JsonProperty(value="shape")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Double> shape = new ArrayList<>();

        public void setOutcomes(List<Double> outcomes) {
            this.outcomes = outcomes;
        }

        public List<Double> getShape() {
            return shape;
        }

        public List<Double> getOutcomes() {
            return outcomes;
        }
}
