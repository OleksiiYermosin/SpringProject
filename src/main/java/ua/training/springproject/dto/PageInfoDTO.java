package ua.training.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {

    private Integer page = 0;

    private String sort = "id";

    private String sortDirection = "asc";

    private String surname;

    private String name;

    private String date;

    private boolean searchByName;

    private boolean searchByDate;

}
