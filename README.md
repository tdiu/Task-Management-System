A REST API for a Task Management System using Java, SpringBoot, and Hibernate. Allows for user registration, authentication through basic HTTP login and JWT bearer tokens, as well as task creation and management. 

## /api/accounts
### POST /api/accounts
Registration: Takes a POST request body in the following format containing a unique email and password for user registration:
```
{
  "email": "address@domain.net",
  "password": "password"
}
```

## /api/tasks
### GET /api/tasks
A GET request to /api/tasks endpoint with valid user credentials returns a list of all tasks.
```
[
  {
    "id": <string>,
    "title": <string>,
    "description": <string>,
    "status": <string>,
    "author": <string>,
    "assignee": <string>,
    "total_comments": <integer>
  },
  // other tasks
]
```
A GET request containing an optional `author` parameter returns an array of tasks authored by a particular user: GET /api/tasks?author=user1@mail.com
Similarly, an optional `assignee` parameter can be passed to return an array filtered by the specified assignee.
```
[
  {
    "id": "1",
    "title": "new task",
    "description": "a task for anyone",
    "status": "CREATED",
    "author": "user1@mail.com"
  }
]
```


Otherwise returns a `404 UNAUTHORIZED` error.


### POST /api/tasks
A valid POST request to /api/tasks endpoint by a registered user with valid credentials to create a task:
```
{
  "title": "new task",
  "description": "a task for anyone"
}
```
A successful request returns a response body containing the following:
```
{
  "id": <string>,
  "title": <string>,
  "description": <string>,
  "status": "CREATED",
  "author": <string>,
  "assignee": "none"
}
```


### PUT /api/tasks/{taskId}/assign
Accepts a JSON request body in the following format to update and assign a user to a task:
```
{ 
  "assignee": <email address|"none"> 
}
```


### PUT /api/tasks/{taskId}/status
Accepts the following JSON request body to update the `STATUS` of a given task:
```
{
  "status": <"CREATED"|"IN_PROGRESS"|"COMPLETED">
}
```


### PUT /api/tasks/{taskId}/comments
Accepts the following JSON request format to create a comment for a given task.
```
{
  "text": <string, not blank>
}
```


### GET /api/tasks/{taskId}/comments
Fetches all comments for the given task id:
```
[
  {
    "id": <string>,
    "task_id": <string>,
    "text": <string>,
    "author": <string>
  },
  // other comments
]
```

## POST /api/auth/token
Accessing this endpoint while validated through basic HTTP sign in returns a JSON response body containing a JWT bearer token:
```
{
  "token": <string>
}
```
While valid, the token can then be used to authenticate and access other secured endpoints as the user.
