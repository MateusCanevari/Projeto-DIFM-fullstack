package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.projetodifm.controller.AdminController;
import br.com.projetodifm.data.vo.v1.admin.PermissionForUserVO;
import br.com.projetodifm.data.vo.v1.admin.UserVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.EmailNotFoundException;
import br.com.projetodifm.exceptions.PermissionException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.mapper.DozerMapper;
import br.com.projetodifm.repositories.PermissionRepository;
import br.com.projetodifm.repositories.UserRepository;
import br.com.projetodifm.util.ErrorMessages;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AdminServices {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PagedResourcesAssembler<UserVO> assembler;
    
    public ResponseEntity<?> addPermission(PermissionForUserVO userPermission){

        var user = userRepository.findByEmail(userPermission.getUserEmail())
            .orElseThrow(() -> new EmailNotFoundException(userPermission.getUserEmail()));

        var permission = permissionRepository.findById(userPermission.getPermissionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        if (userRepository.existsByIdAndPermissions(user.getId(), permission))
            throw new ConflictException(ErrorMessages.PERMISSION_CONFLICT); 

        user.getPermissions().add(permission);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<?> removePermission(PermissionForUserVO userPermission){

        var user = userRepository.findByEmail(userPermission.getUserEmail())
            .orElseThrow(() -> new EmailNotFoundException(userPermission.getUserEmail()));

        var permission = permissionRepository.findById(userPermission.getPermissionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        if (permission.getId().equals(3L))
            throw new PermissionException(ErrorMessages.PERMISSION_CANNOT_BE_REMOVED);

        if (!userRepository.existsByIdAndPermissions(user.getId(), permission))
            throw new PermissionException(ErrorMessages.USER_WITHOUT_PERMISSION);

        user.getPermissions().remove(permission);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<PagedModel<EntityModel<UserVO>>> findAllUsers(Integer page, Integer size, String direction) {

        var usersVO = userRepository.findAll(pageable(page, size, direction))
                .map(x -> DozerMapper.parseObject(x, UserVO.class));

        usersVO.map(x -> x.add(linkTo(methodOn(AdminController.class).findUserById(x.getKey())).withSelfRel()));

        var link = linkTo(methodOn(AdminController.class).findAllUsers(page, size, direction)).withSelfRel();

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(usersVO, link));
    }

    public ResponseEntity<UserVO> findUserById(Long userId){
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var userVO = DozerMapper.parseObject(user, UserVO.class);

        userVO.add(linkTo(methodOn(AdminController.class).findUserById(userId)).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(userVO);
    }

    public ResponseEntity<PagedModel<EntityModel<UserVO>>> findAllUsersByPermissionId(Long permissionId, Integer page, Integer size, String direction){

        var permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var userVO = userRepository.findByPermissions(permission, pageable(page, size, direction))
                .map(x -> DozerMapper.parseObject(x, UserVO.class));

        userVO.map(x -> x.add(linkTo(methodOn(AdminController.class).findUserById(x.getKey())).withSelfRel()));

        var link = linkTo(methodOn(AdminController.class).findAllUsersByPermissionId(permissionId, page, size, direction)).withSelfRel();

        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(userVO, link));
    }

    private Pageable pageable(Integer page, Integer size, String direction){

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
    }

}
