package br.com.projetodifm.data.vo.v1;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import br.com.projetodifm.util.ErrorMessages;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonPropertyOrder({ "id", "nomeProduto", "descricao", "quantidade", "preco" })
public class ProdutoVO extends RepresentationModel<ProdutoVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @Mapping("id")
    private Long key;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    private String nomeProduto;
    @NotBlank(message = ErrorMessages.CONTENT_NOT_BLANK)
    private String descricao;
    @NotNull(message = ErrorMessages.CONTENT_NOT_NULL)
    @Min(value = 0)
    private Integer quantidade;
    @NotNull(message = ErrorMessages.CONTENT_NOT_NULL)
    @Min(value = 1)
    private Double preco;
}
