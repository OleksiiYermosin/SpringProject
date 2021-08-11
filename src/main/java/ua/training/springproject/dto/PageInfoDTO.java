package ua.training.springproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for pages
 */
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
