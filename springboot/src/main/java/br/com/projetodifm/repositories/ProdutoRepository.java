package br.com.projetodifm.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.projetodifm.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    
    Boolean existsByNomeProdutoAndUserId(String nomeProduto, Long userId);

    Optional<Produto> findByUserIdAndId(Long userId, Long id);

    Optional<Page<Produto>> findByUserId(Long userId, Pageable pageable);

    // Retorna todos os produtos que tem no nome a sequencia passada no Param
    @Query("select p from Produto p where lower(p.nomeProduto) like lower(concat('%', :nomeProduto,'%')) AND p.user.id = :user")
    Optional<Page<Produto>> findProdutosByNames(@Param("nomeProduto") String nomeProduto, @Param("user") Long userId, Pageable pageable); // Search

    // ou Optional<Page<Produto>> findByNomeProdutoContainingIgnoreCaseAndUserId(String name, Long userId, Pageable pageable);
}
