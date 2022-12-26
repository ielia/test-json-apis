package com.ielia.test.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Getter
@Setter
public class BarDTO {
    @DecimalMin("0.001")
    @JsonProperty(required = true)
    @NotNull
    private Double doubleField1;
    @DecimalMin("0.001")
    @NotNull
    private Double doubleField2;
    @JsonProperty(required = true)
    @NotBlank
    @NotNull
    private String stringField1;
    @NotBlank
    @NotNull
    private String stringField2;
    @JsonIgnore
    private String stringField3;
}
