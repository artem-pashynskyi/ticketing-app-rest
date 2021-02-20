package com.ticketingapp.implementation;

import com.ticketingapp.dto.ProjectDTO;
import com.ticketingapp.entity.Project;
import com.ticketingapp.entity.User;
import com.ticketingapp.enums.Status;
import com.ticketingapp.exception.TicketingProjectException;
import com.ticketingapp.util.MapperUtil;
import com.ticketingapp.repository.ProjectRepository;
import com.ticketingapp.repository.UserRepository;
import com.ticketingapp.service.ProjectService;
import com.ticketingapp.service.TaskService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TaskService taskService;
    private MapperUtil mapperUtil;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, TaskService taskService, MapperUtil mapperUtil) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
        dto.setProjectStatus(Status.OPEN);
        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());
        if(foundProject != null) throw new TicketingProjectException("Project with this code already exists");
        Project obj = mapperUtil.convert(dto, new Project());
        Project createdProject = projectRepository.save(obj);
        return mapperUtil.convert(createdProject, new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());
        if(foundProject == null) throw new TicketingProjectException("Project does not exist");
        Project convertedProject = mapperUtil.convert(dto, new Project());
        Project updatedProject = projectRepository.save(convertedProject);
        return mapperUtil.convert(updatedProject, new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if(project == null) throw new TicketingProjectException("Project does not exist");
        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() +  "-" + project.getId());
        projectRepository.save(project);
        taskService.deleteByProject(mapperUtil.convert(project, new ProjectDTO()));
    }

    @Override
    public ProjectDTO complete(String projectCode) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(projectCode);
        if(project == null) throw new TicketingProjectException("Project does not exist");
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId = Long.parseLong(id);
        User user = userRepository.findById(currentId).orElseThrow(() -> new TicketingProjectException("Manager does not exist"));
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        if(list.size() == 0) throw new TicketingProjectException("Manager does not have any assigned projects");
        return list.stream().map(project -> {
            ProjectDTO obj = mapperUtil.convert(project, new ProjectDTO());
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));
            return obj;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {
        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());
        }
}















