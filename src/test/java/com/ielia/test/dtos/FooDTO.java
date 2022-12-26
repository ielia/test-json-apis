package com.ielia.test.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Getter
@Setter
public class FooDTO {
    @JsonProperty(required = true)
    @NotNull
    private Boolean booleanField1;
    @NotNull
    private Boolean booleanField2;
    @JsonProperty(required = true)
    @Min(1)
    @NotNull
    private Integer intField1;
    @Min(1)
    @NotNull
    private Integer intField2;
    @JsonProperty(required = true)
    @NotBlank
    @NotNull
    private String stringField1;
    @NotBlank
    @NotNull
    private String stringField2;
    @JsonProperty(required = true)
    @NotNull
    private List<BarDTO> barDTOs1;
    @NotNull
    private List<BarDTO> barDTOs2;
    @JsonProperty(required = true)
    private Map<String, Object> stuff1;
    private Map<String, Object> stuff2;
}
