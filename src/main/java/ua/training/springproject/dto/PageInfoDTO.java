package ua.training.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {

    private Integer page = 0;

    private String sort = "id";

    private String sortDirection = "asc";

    private String surnameAndName;

    private Date date;

    private boolean searchByName;

    private boolean searchByDate;

}
