package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.projetodifm.controller.ProdutoController;
import br.com.projetodifm.data.vo.v1.ProdutoVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.mapper.DozerMapper;
import br.com.projetodifm.model.Produto;
import br.com.projetodifm.repositories.ProdutoRepository;
import br.com.projetodifm.util.ErrorMessages;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

@Service
public class ProdutoServices {

    private static final Logger logger = Logger.getLogger(ProdutoServices.class.getName());

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private PagedResourcesAssembler<ProdutoVO> assembler;

    public ResponseEntity<PagedModel<EntityModel<ProdutoVO>>> findAll(Pageable pageable) {
        logger.info("Finding all Products!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

//        var user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new EmailNotFoundException(email));

        var produtosPage = repository.findByUserId(user.getId(), pageable)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var produtosPageVO = produtosPage.map(x -> DozerMapper.parseObject(x, ProdutoVO.class));

        produtosPageVO.map(x -> x.add(linkTo(methodOn(ProdutoController.class).findById(x.getKey())).withSelfRel()));

        var link = linkTo(methodOn(ProdutoController.class)
                .findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(produtosPageVO, link));
    }

    public ResponseEntity<PagedModel<EntityModel<ProdutoVO>>> findProdutosByName(String nomeProduto, Pageable pageable) {
        logger.info("Finding all Products by Name!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

        var produtosPage = repository.findProdutosByNames(nomeProduto, user.getId(), pageable)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var produtosPageVO = produtosPage.map(x -> DozerMapper.parseObject(x, ProdutoVO.class));

        produtosPageVO.map(x -> x.add(linkTo(methodOn(ProdutoController.class).findById(x.getKey())).withSelfRel()));

        var link = linkTo(methodOn(ProdutoController.class)
                .findProdutosByName(nomeProduto, pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(produtosPageVO, link));
    }

    public ResponseEntity<ProdutoVO> findById(Long idProduct) {
        logger.info("Finding one Product!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var vo = DozerMapper.parseObject(produto, ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(idProduct)).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public ResponseEntity<ProdutoVO> create(ProdutoVO produto) {
        logger.info("Creating one Product!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

        if (repository.existsByNomeProdutoAndUserId(produto.getNomeProduto(), user.getId()))
            throw new ConflictException(ErrorMessages.PRODUCT_CONFLICT);

        var produtos = DozerMapper.parseObject(produto, Produto.class);

        produtos.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(produtos), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    public ResponseEntity<ProdutoVO> update(ProdutoVO produto) {
        logger.info("Updating one Product!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

        var userProduto = repository.findByUserIdAndId(user.getId(), produto.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        if (repository.existsByNomeProdutoAndUserId(produto.getNomeProduto(), user.getId())
                && !userProduto.getNomeProduto().equals(produto.getNomeProduto()))
            throw new ConflictException(ErrorMessages.PRODUCT_CONFLICT);

        userProduto = DozerMapper.parseObject(produto, Produto.class);

        userProduto.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(userProduto), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public void delete(Long idProduct) {
        logger.info("Deleting one Product!");

        var user = AuthenticatedUsersServices.getAuthenticatedUser();

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        repository.delete(produto);
    }
}
