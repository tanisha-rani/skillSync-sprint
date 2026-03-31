# SkillSync Endpoint Test Cases

This sheet lists `78` primary API test cases based on the controller endpoints currently present in the project.

Base URLs:

- API Gateway: `http://localhost:8080`
- Auth Service direct: `http://localhost:8087`
- User Service direct: `http://localhost:8081`
- Mentor Service direct: `http://localhost:8082`
- Skill Service direct: `http://localhost:8083`
- Session Service direct: `http://localhost:8084`
- Group Service direct: `http://localhost:8085`
- Review Service direct: `http://localhost:8086`
- Notification Service direct: `http://localhost:8089`

Security note:

- `Authorization: Bearer <token>` is required for all gateway routes except `/auth/**`.
- Auth `/api/*` endpoints are exposed directly by `authservice` and are not routed through the gateway in the current config.

## Auth Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| AUTH-01 | POST | `http://localhost:8080/auth/register` | Register a learner with valid details | Body: `{"name":"Aman Verma","email":"aman@example.com","password":"Password@123","role":"LEARNER"}` | `201 Created`, access token + refresh token returned |
| AUTH-02 | POST | `http://localhost:8080/auth/login` | Login with valid credentials | Body: `{"email":"aman@example.com","password":"Password@123"}` | `200 OK`, JWT tokens returned |
| AUTH-03 | POST | `http://localhost:8080/auth/refresh?refreshToken=<valid_refresh_token>` | Refresh access token using valid refresh token | Query param: valid `refreshToken` | `200 OK`, new `accessToken` returned |
| AUTH-04 | POST | `http://localhost:8080/auth/logout?refreshToken=<valid_refresh_token>` | Logout user and invalidate refresh token | Query param: valid `refreshToken` | `200 OK`, logout success message |
| AUTH-05 | GET | `http://localhost:8087/api/admin/data` | Access admin-only data with admin token | Header: admin bearer token | `200 OK`, response `Admin Access` |
| AUTH-06 | GET | `http://localhost:8087/api/user/data` | Access user data with authenticated user token | Header: learner bearer token | `200 OK`, response `User Access` |
| AUTH-07 | GET | `http://localhost:8080/auth/validate?token=<valid_access_token>` | Validate a valid JWT | Query param: valid `token` | `200 OK`, `email`, `role`, `valid=true` |
| AUTH-08 | GET | `http://localhost:8087/api/test` | Verify authenticated auth-service test endpoint | Header: valid bearer token | `200 OK`, response `You are authenticated!!` |
| AUTH-09 | GET | `http://localhost:8087/api/me` | Fetch currently logged-in user profile | Header: valid bearer token | `200 OK`, current user object returned |

## API Gateway

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| GW-01 | GET | `http://localhost:8080/admin/dashboard` | Fetch admin dashboard aggregation | Header: admin bearer token | `200 OK`, object with `users`, `sessions`, `mentors` stats |

## User Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| USER-01 | POST | `http://localhost:8080/users` | Create a new user from gateway | Header: bearer token, Body: `{"name":"Riya Sharma","email":"riya@example.com","role":"LEARNER"}` | `201 Created`, created user returned |
| USER-02 | GET | `http://localhost:8080/users/1` | Get user by existing ID | Header: bearer token | `200 OK`, user details returned |
| USER-03 | GET | `http://localhost:8080/users?page=0&size=10&sortBy=id` | Get paginated users list | Header: bearer token | `200 OK`, paginated user data |
| USER-04 | PUT | `http://localhost:8080/users/1` | Update existing user details | Header: bearer token, Body: `{"name":"Riya S Sharma","email":"riya@example.com","role":"LEARNER"}` | `200 OK`, updated user returned |
| USER-05 | DELETE | `http://localhost:8080/users/1` | Soft delete an existing user | Header: bearer token | `204 No Content` |
| USER-06 | GET | `http://localhost:8080/users/role/LEARNER?page=0&size=10&sortBy=id` | Filter users by role | Header: bearer token | `200 OK`, only learner users returned |
| USER-07 | GET | `http://localhost:8080/users/active?page=0&size=10&sortBy=id` | Get active users only | Header: bearer token | `200 OK`, only active users returned |
| USER-08 | GET | `http://localhost:8080/users/admin/stats` | Get user count stats | Header: bearer token | `200 OK`, `totalUsers` returned |

## Mentor Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| MENTOR-01 | POST | `http://localhost:8080/mentors/apply` | Apply as mentor with valid profile | Header: bearer token, Body: `{"bio":"Java mentor","experienceYears":5,"skills":["Java","Spring Boot"],"hourlyRate":700,"userId":2}` | `201 Created`, mentor profile created |
| MENTOR-02 | GET | `http://localhost:8080/mentors/1` | Fetch mentor by ID | Header: bearer token | `200 OK`, mentor details returned |
| MENTOR-03 | GET | `http://localhost:8080/mentors?page=0&size=10&sortBy=id` | Get paginated mentor list | Header: bearer token | `200 OK`, paginated mentors returned |
| MENTOR-04 | PUT | `http://localhost:8080/mentors/1` | Update mentor profile | Header: bearer token, Body: `{"bio":"Senior Java mentor","experienceYears":6,"skills":["Java","Spring Boot","Microservices"],"hourlyRate":900,"userId":2}` | `200 OK`, updated mentor returned |
| MENTOR-05 | DELETE | `http://localhost:8080/mentors/1` | Soft delete mentor profile | Header: bearer token | `204 No Content` |
| MENTOR-06 | PUT | `http://localhost:8080/mentors/1/approve` | Approve a pending mentor | Header: admin bearer token | `200 OK`, mentor status updated to approved |
| MENTOR-07 | PUT | `http://localhost:8080/mentors/1/reject` | Reject a pending mentor | Header: admin bearer token | `200 OK`, mentor status updated to rejected |
| MENTOR-08 | GET | `http://localhost:8080/mentors/status/PENDING?page=0&size=10&sortBy=id` | Filter mentors by status | Header: bearer token | `200 OK`, mentors matching status returned |
| MENTOR-09 | GET | `http://localhost:8080/mentors/skill/Java?page=0&size=10&sortBy=id` | Search mentors by skill | Header: bearer token | `200 OK`, mentors with `Java` skill returned |
| MENTOR-10 | GET | `http://localhost:8080/mentors/active?page=0&size=10&sortBy=id` | Get active mentors | Header: bearer token | `200 OK`, active mentors returned |
| MENTOR-11 | PUT | `http://localhost:8080/mentors/toggle/1` | Toggle mentor availability | Header: bearer token | `200 OK`, availability flag toggled |
| MENTOR-12 | PUT | `http://localhost:8082/mentors/1/rating?averageRating=4.8&totalReviews=12` | Update mentor rating from review service integration | Query params: `averageRating`, `totalReviews` | `200 OK` |
| MENTOR-13 | GET | `http://localhost:8080/mentors/admin/stats` | Get mentor count stats | Header: bearer token | `200 OK`, `totalMentors` returned |

## Skill Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| SKILL-01 | POST | `http://localhost:8080/skills` | Create a new skill | Header: bearer token, Body: `{"name":"Spring Boot","category":"Backend","description":"Java microservice framework"}` | `201 Created`, created skill returned |
| SKILL-02 | GET | `http://localhost:8080/skills/1` | Get skill by ID | Header: bearer token | `200 OK`, skill returned |
| SKILL-03 | GET | `http://localhost:8080/skills?page=0&size=10&sortBy=id` | Get all skills with pagination | Header: bearer token | `200 OK`, paginated skills returned |
| SKILL-04 | GET | `http://localhost:8080/skills/search?name=Spring` | Search skills by name | Header: bearer token | `200 OK`, matching skills returned |
| SKILL-05 | PUT | `http://localhost:8080/skills/1` | Update skill by ID | Header: bearer token, Body: `{"name":"Advanced Spring Boot","category":"Backend","description":"Updated skill description"}` | `200 OK`, updated skill returned |
| SKILL-06 | DELETE | `http://localhost:8080/skills/1` | Delete skill by ID | Header: bearer token | `204 No Content` |

## Group Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| GROUP-01 | POST | `http://localhost:8080/groups` | Create a new peer learning group | Header: bearer token, Body: `{"name":"Spring Boot Squad","description":"Backend practice group","topics":["Spring Boot","Java"],"creatorUserId":3,"maxMembers":25}` | `201 Created`, group returned and creator enrolled |
| GROUP-02 | GET | `http://localhost:8080/groups/1` | Fetch group by ID | Header: bearer token | `200 OK`, group returned |
| GROUP-03 | GET | `http://localhost:8080/groups?page=0&size=10&sortBy=id` | List all groups with pagination | Header: bearer token | `200 OK`, paginated groups returned |
| GROUP-04 | PUT | `http://localhost:8080/groups/1?requestingUserId=3` | Update group by creator | Header: bearer token, Body: `{"name":"Spring Boot Masters","description":"Updated backend practice group","topics":["Spring Boot","Java","Docker"],"creatorUserId":3,"maxMembers":30}` | `200 OK`, updated group returned |
| GROUP-05 | DELETE | `http://localhost:8080/groups/1?requestingUserId=3` | Archive group by creator | Header: bearer token | `204 No Content` |
| GROUP-06 | POST | `http://localhost:8080/groups/1/join?userId=4` | Join an open group | Header: bearer token | `200 OK`, user added to members |
| GROUP-07 | POST | `http://localhost:8080/groups/1/leave?userId=4` | Leave a joined group | Header: bearer token | `200 OK`, user removed from members |
| GROUP-08 | GET | `http://localhost:8080/groups/my-groups/4?page=0&size=10&sortBy=id` | Fetch groups joined by user | Header: bearer token | `200 OK`, user's groups returned |
| GROUP-09 | GET | `http://localhost:8080/groups/status/OPEN?page=0&size=10&sortBy=id` | Filter groups by status | Header: bearer token | `200 OK`, open groups returned |
| GROUP-10 | GET | `http://localhost:8080/groups/topic/Java?page=0&size=10&sortBy=id` | Filter groups by topic tag | Header: bearer token | `200 OK`, groups tagged with Java returned |
| GROUP-11 | GET | `http://localhost:8080/groups/creator/3?page=0&size=10&sortBy=id` | Fetch groups created by user | Header: bearer token | `200 OK`, groups created by user returned |
| GROUP-12 | GET | `http://localhost:8080/groups/search?keyword=spring&page=0&size=10&sortBy=id` | Search groups by keyword | Header: bearer token | `200 OK`, matching groups returned |
| GROUP-13 | PUT | `http://localhost:8080/groups/1/status?requestingUserId=3&newStatus=CLOSED` | Change group status by creator | Header: bearer token | `200 OK`, status updated to `CLOSED` |
| GROUP-14 | POST | `http://localhost:8080/groups/1/discussions` | Post discussion message as member | Header: bearer token, Body: `{"authorUserId":4,"message":"Can we cover Spring Security this weekend?"}` | `201 Created`, discussion post returned |
| GROUP-15 | GET | `http://localhost:8080/groups/1/discussions?page=0&size=20` | Fetch discussions for group | Header: bearer token | `200 OK`, paginated discussions returned |

## Session Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| SESSION-01 | POST | `http://localhost:8080/sessions` | Book a new mentoring session | Header: bearer token, Body: `{"mentorId":1,"learnerId":4,"sessionDate":"2026-04-05","duration":60,"topic":"Spring Security Basics"}` | `201 Created`, session returned with initial status |
| SESSION-02 | PUT | `http://localhost:8080/sessions/1/accept` | Mentor accepts session | Header: bearer token | `200 OK`, status changed to accepted |
| SESSION-03 | PUT | `http://localhost:8080/sessions/1/reject` | Mentor rejects session | Header: bearer token | `200 OK`, status changed to rejected |
| SESSION-04 | PUT | `http://localhost:8080/sessions/1/cancel` | Cancel an existing session | Header: bearer token | `200 OK`, status changed to cancelled |
| SESSION-05 | PUT | `http://localhost:8080/sessions/1/complete` | Mark session as completed | Header: bearer token | `200 OK`, status changed to completed |
| SESSION-06 | GET | `http://localhost:8080/sessions/1` | Fetch session by ID | Header: bearer token | `200 OK`, session details returned |
| SESSION-07 | GET | `http://localhost:8080/sessions/user/4` | Fetch sessions for a user | Header: bearer token | `200 OK`, session list returned |
| SESSION-08 | GET | `http://localhost:8080/sessions/mentor/1` | Fetch sessions for a mentor | Header: bearer token | `200 OK`, mentor session list returned |
| SESSION-09 | GET | `http://localhost:8080/sessions?page=0&size=10&sortBy=createdAt` | Fetch all sessions with pagination | Header: bearer token | `200 OK`, paginated sessions returned |
| SESSION-10 | GET | `http://localhost:8080/sessions/status/PENDING?page=0&size=10&sortBy=createdAt` | Filter sessions by status | Header: bearer token | `200 OK`, sessions matching status returned |
| SESSION-11 | GET | `http://localhost:8080/sessions/admin/stats` | Get session count stats | Header: bearer token | `200 OK`, `totalSessions` returned |

## Review Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| REVIEW-01 | POST | `http://localhost:8080/reviews` | Submit review for completed session | Header: bearer token, Body: `{"mentorId":1,"learnerId":4,"sessionId":1,"rating":5,"comment":"Very clear explanation and practical examples."}` | `201 Created`, review returned |
| REVIEW-02 | GET | `http://localhost:8080/reviews/1` | Get review by ID | Header: bearer token | `200 OK`, review returned |
| REVIEW-03 | GET | `http://localhost:8080/reviews/mentor/1?page=0&size=10&sortBy=id` | Fetch paginated reviews for mentor | Header: bearer token | `200 OK`, mentor reviews returned |
| REVIEW-04 | GET | `http://localhost:8080/reviews/learner/4` | Fetch all reviews by learner | Header: bearer token | `200 OK`, learner reviews returned |
| REVIEW-05 | GET | `http://localhost:8080/reviews?page=0&size=10&sortBy=id` | Fetch all reviews with pagination | Header: bearer token | `200 OK`, paginated reviews returned |
| REVIEW-06 | GET | `http://localhost:8080/reviews/mentor/1/summary` | Fetch mentor rating summary | Header: bearer token | `200 OK`, average rating and total review count returned |
| REVIEW-07 | DELETE | `http://localhost:8080/reviews/1` | Delete review by ID | Header: bearer token | `204 No Content` |

## Notification Service

| TC ID | Method | URL | Scenario | Sample Input | Expected |
|---|---|---|---|---|---|
| NOTIF-01 | POST | `http://localhost:8080/notifications` | Publish notification request to RabbitMQ queue | Header: bearer token, Body: `{"userId":4,"recipientEmail":"riya@example.com","type":"SESSION_BOOKED","subject":"Session booked","message":"Your session has been booked.","referenceId":1,"referenceType":"SESSION"}` | `200 OK`, message sent to queue |
| NOTIF-02 | GET | `http://localhost:8080/notifications/1` | Get notification by ID | Header: bearer token | `200 OK`, notification returned |
| NOTIF-03 | GET | `http://localhost:8080/notifications/user/4` | Get all notifications for a user | Header: bearer token | `200 OK`, notification list returned |
| NOTIF-04 | GET | `http://localhost:8080/notifications/user/4/unread` | Get unread notifications for a user | Header: bearer token | `200 OK`, unread notification list returned |
| NOTIF-05 | GET | `http://localhost:8080/notifications/user/4/unread-count` | Get unread notification count | Header: bearer token | `200 OK`, `unreadCount` returned |
| NOTIF-06 | PATCH | `http://localhost:8080/notifications/1/read` | Mark a notification as read | Header: bearer token | `200 OK`, notification status updated |
| NOTIF-07 | PATCH | `http://localhost:8080/notifications/user/4/read-all` | Mark all notifications as read for user | Header: bearer token | `204 No Content` |
| NOTIF-08 | DELETE | `http://localhost:8080/notifications/1` | Delete notification by ID | Header: bearer token | `204 No Content` |

## Recommended Negative Coverage

For strong QA coverage, run these negative patterns against each relevant endpoint in addition to the `78` primary cases above:

- Missing or invalid bearer token on gateway-protected routes should return `401` or `403`.
- Invalid path variable such as unknown `id` should return `404` where handled by service exceptions.
- Invalid enum values such as `PENDINGX`, `OPENED`, or wrong role names should return `400`.
- Validation failures such as blank names, invalid email, negative numeric values, or missing required fields should return `400`.
- Duplicate business operations should be checked where applicable, like duplicate review submission, duplicate mentor application, joining the same group twice, or refreshing with revoked token.
