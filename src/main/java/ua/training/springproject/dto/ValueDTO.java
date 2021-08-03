package ua.training.springproject.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueDTO {

    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1000.0")
    @Digits(integer=6, fraction=2)
    private BigDecimal value;

}
