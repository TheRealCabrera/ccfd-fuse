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

        public List<String> getShape() {
            return shape;
        }

        public List<Double> getOutcomes() {
            return outcomes;
        }
}
