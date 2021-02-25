# Ticketing App

### Spring Boot, ORM, JPA, Hibernate + Spring Security + Spring Rest + new User email confirmation + AOP Logging + log4j2

#### Project Definition
- A ticketingproject is an application that organizes management processes.
- The application will have the opportunity to create users, create projects, assign tasks, to track how the project or task is going.


#### Requirements
Features that are developing on the project are:
- User management:
    - CRUD operations for users.
    - Authentication of users. After we login, we will redirect users to the welcome page. 
    - The user will have a role assigned for it. Provided roles will be ADMIN, MANAGER, EMPLOYEE.
        - ADMIN will be able to use all features of the application.
        - MANAGER will be able to use all the parts related to projects such as creating a project, assign a task to the employee, etc.
        - EMPLOYEE will be able to see all tasks and projects related to him.
- Project:
    - CRUD operationsfortheproject.
    - Every project created will have a manager responsible for it. When creating a project you must assign a manager.
    - The project will be able to have status. Provided project statuses will be OPEN, IN_PROGRESS, UAT, COMPLETED.
    - The manager of the project will be able to create tasks for employees.
    - The manager will be able to complete the project.
    - Employees will be able to see all the archived projects.
- Task:
    - CRUD operations for the task.
    - Every task created will have an employee responsible for it.
    - When a task is created by the manager, it will be assigned to an employee.
    - The task will be able to have status. 
    - Provided task statuses will be OPEN, IN_PROGRESS, UAT, COMPLETED.
    - Employees will be able to change the status of the task depends on which phase it is.
    - Employees will be able to see their own tasks and to start working on it.
   

