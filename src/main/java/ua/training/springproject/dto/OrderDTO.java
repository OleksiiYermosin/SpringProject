package ua.training.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.training.springproject.utils.annotations.FieldsNotEqual;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldsNotEqual(firstField = "startAddress", secondField = "finishAddress", message = "Addresses must be different")
public class OrderDTO {

    @NotEmpty
    private String startAddress;

    @NotEmpty
    private String finishAddress;

    @Pattern(regexp = "ECONOMY|COMFORT|BUSINESS")
    private String taxiClass;

    @Min(value = 1)
    private int peopleAmount = 1;

    private String comment;

}