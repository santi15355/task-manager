package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDto {
    private static final int MIN_V = 1;

    @NotBlank
    @Size(min = MIN_V)
    private String name;
}
