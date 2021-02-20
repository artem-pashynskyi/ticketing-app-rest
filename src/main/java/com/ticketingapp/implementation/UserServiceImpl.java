package com.ticketingapp.implementation;

import com.ticketingapp.dto.ProjectDTO;
import com.ticketingapp.dto.TaskDTO;
import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.entity.User;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.util.MapperUtil;
import com.ticketingapp.repository.UserRepository;
import com.ticketingapp.service.ProjectService;
import com.ticketingapp.service.TaskService;
import com.ticketingapp.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ProjectService projectService;
    private TaskService taskService;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, @Lazy ProjectService projectService, TaskService taskService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> mapperUtil.convert(obj,new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) throws AccessDeniedException {
        User user = userRepository.findByUsername(username);
        checkForAuthorities(user);
        return mapperUtil.convert(user,new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketingProjectException {
        User foundUser = userRepository.findByUsername(dto.getUsername());
        if(foundUser != null) throw new TicketingProjectException("User already exists");
        User user =  mapperUtil.convert(dto,new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User save = userRepository.save(user);
        return mapperUtil.convert(save,new UserDTO());
    }

    @Override
    public UserDTO update(UserDTO dto) throws TicketingProjectException, AccessDeniedException {
        User user = userRepository.findByUsername(dto.getUsername());
        User convertedUser = mapperUtil.convert(dto,new User());
        convertedUser.setPassword(passwordEncoder.encode(convertedUser.getPassword()));
        if(!user.getEnabled()) throw new TicketingProjectException("User is not confirmed");
        checkForAuthorities(user);
        convertedUser.setEnabled(true);
        convertedUser.setId(user.getId());
        userRepository.save(convertedUser);
        return findByUserName(dto.getUsername());
    }

    @Override
    public void delete(String username) throws TicketingProjectException {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new TicketingProjectException("User Does Not Exists");
        if(!checkIfUserCanBeDeleted(user)) throw new TicketingProjectException("User can not be deleted. It is linked by a project ot task");
        user.setUsername(user.getUsername() + "-" + user.getId());
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    //hard delete
    @Override
    public void deleteByUserName(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {return mapperUtil.convert(obj,new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {
        switch(user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projectList = projectService.readAllByAssignedManager(user);
                return projectList.size() == 0;
            case "Employee":
                List<TaskDTO> taskList = taskService.readAllByEmployee(user);
                return taskList.size() == 0;
            default:
                return true;
        }
    }

    @Override
    public UserDTO confirm(User user) {
        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);
        return mapperUtil.convert(confirmedUser,new UserDTO());
    }

    private void checkForAuthorities(User user) throws AccessDeniedException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !authentication.getName().equals("anonymousUser")) {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if(!(authentication.getName().equals(user.getId().toString()) || roles.contains("Admin")))
                throw new AccessDeniedException("Access is denied");
        }
    }
}
