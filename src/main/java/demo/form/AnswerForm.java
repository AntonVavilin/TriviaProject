
package demo.form;

import jakarta.validation.constraints.NotBlank;

public class AnswerForm {
    @NotBlank
    private String optionId;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
