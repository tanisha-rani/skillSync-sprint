import { apiSlice } from '../../services/apiSlice.js';

export const platformApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getMentors: builder.query({
      query: ({ page = 0, size = 20, sortBy = 'id' } = {}) =>
        `/mentors?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Mentors'],
    }),
    getMentorsByStatus: builder.query({
      query: ({ status, page = 0, size = 20, sortBy = 'id' }) =>
        `/mentors/status/${status}?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Mentors'],
    }),
    getMentorByUserId: builder.query({
      query: (userId) => `/mentors/user/${userId}`,
      providesTags: ['Mentors'],
    }),
    applyAsMentor: builder.mutation({
      query: (payload) => ({
        url: '/mentors/apply',
        method: 'POST',
        body: payload,
      }),
      invalidatesTags: ['Mentors', 'Dashboard'],
    }),
    approveMentor: builder.mutation({
      query: (mentorId) => ({
        url: `/mentors/${mentorId}/approve`,
        method: 'PUT',
      }),
      invalidatesTags: ['Mentors', 'Dashboard'],
    }),
    rejectMentor: builder.mutation({
      query: ({ mentorId, reason }) => ({
        url: `/mentors/${mentorId}/reject${reason ? `?reason=${encodeURIComponent(reason)}` : ''}`,
        method: 'PUT',
      }),
      invalidatesTags: ['Mentors', 'Dashboard'],
    }),
    reapplyMentor: builder.mutation({
      query: (mentorId) => ({
        url: `/mentors/${mentorId}/reapply`,
        method: 'PUT',
      }),
      invalidatesTags: ['Mentors', 'Dashboard'],
    }),
    updateMentor: builder.mutation({
      query: ({ mentorId, payload }) => ({
        url: `/mentors/${mentorId}`,
        method: 'PUT',
        body: payload,
      }),
      invalidatesTags: ['Mentors', 'Dashboard'],
    }),
    getUsers: builder.query({
      query: ({ page = 0, size = 200, sortBy = 'id' } = {}) =>
        `/users?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Users'],
    }),
    getUserById: builder.query({
      query: (id) => `/users/${id}`,
      providesTags: ['Users'],
    }),
    getSessionsByUser: builder.query({
      query: (userId) => `/sessions/user/${userId}`,
      providesTags: ['Sessions'],
    }),
    getSessionsByMentor: builder.query({
      query: (mentorId) => `/sessions/mentor/${mentorId}`,
      providesTags: ['Sessions'],
    }),
    getSessions: builder.query({
      query: ({ page = 0, size = 200, sortBy = 'createdAt' } = {}) =>
        `/sessions?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Sessions'],
    }),
    bookSession: builder.mutation({
      query: (payload) => ({
        url: '/sessions',
        method: 'POST',
        body: payload,
      }),
      invalidatesTags: ['Sessions'],
    }),
    acceptSession: builder.mutation({
      query: (sessionId) => ({
        url: `/sessions/${sessionId}/accept`,
        method: 'PUT',
      }),
      invalidatesTags: ['Sessions', 'Notifications'],
    }),
    rejectSession: builder.mutation({
      query: (sessionId) => ({
        url: `/sessions/${sessionId}/reject`,
        method: 'PUT',
      }),
      invalidatesTags: ['Sessions', 'Notifications'],
    }),
    completeSession: builder.mutation({
      query: (sessionId) => ({
        url: `/sessions/${sessionId}/complete`,
        method: 'PUT',
      }),
      invalidatesTags: ['Sessions', 'Dashboard'],
    }),
    getGroups: builder.query({
      query: ({ page = 0, size = 20, sortBy = 'id' } = {}) =>
        `/groups?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Groups'],
    }),
    getMyGroups: builder.query({
      query: ({ userId, page = 0, size = 20, sortBy = 'id' }) =>
        `/groups/my-groups/${userId}?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Groups'],
    }),
    createGroup: builder.mutation({
      query: (payload) => ({
        url: '/groups',
        method: 'POST',
        body: payload,
      }),
      invalidatesTags: ['Groups'],
    }),
    joinGroup: builder.mutation({
      query: ({ groupId, userId }) => ({
        url: `/groups/${groupId}/join?userId=${userId}`,
        method: 'POST',
      }),
      invalidatesTags: ['Groups'],
    }),
    leaveGroup: builder.mutation({
      query: ({ groupId, userId }) => ({
        url: `/groups/${groupId}/leave?userId=${userId}`,
        method: 'POST',
      }),
      invalidatesTags: ['Groups'],
    }),
    getAdminDashboard: builder.query({
      query: () => '/admin/dashboard',
      providesTags: ['Dashboard'],
    }),
    getSkills: builder.query({
      query: ({ page = 0, size = 30, sortBy = 'id' } = {}) =>
        `/skills?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Skills'],
    }),
    searchSkills: builder.query({
      query: (name) => `/skills/search?name=${encodeURIComponent(name)}`,
      providesTags: ['Skills'],
    }),
    createSkill: builder.mutation({
      query: (payload) => ({
        url: '/skills',
        method: 'POST',
        body: payload,
      }),
      invalidatesTags: ['Skills'],
    }),
    updateSkill: builder.mutation({
      query: ({ skillId, payload }) => ({
        url: `/skills/${skillId}`,
        method: 'PUT',
        body: payload,
      }),
      invalidatesTags: ['Skills'],
    }),
    deleteSkill: builder.mutation({
      query: (skillId) => ({
        url: `/skills/${skillId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Skills'],
    }),
    getReviewsByMentor: builder.query({
      query: ({ mentorId, page = 0, size = 10, sortBy = 'id' }) =>
        `/reviews/mentor/${mentorId}?page=${page}&size=${size}&sortBy=${sortBy}`,
      providesTags: ['Reviews'],
    }),
    getReviewsByLearner: builder.query({
      query: (learnerId) => `/reviews/learner/${learnerId}`,
      providesTags: ['Reviews'],
    }),
    createReview: builder.mutation({
      query: (payload) => ({
        url: '/reviews',
        method: 'POST',
        body: payload,
      }),
      invalidatesTags: ['Reviews', 'Mentors'],
    }),
    getMentorRatingSummary: builder.query({
      query: (mentorId) => `/reviews/mentor/${mentorId}/summary`,
      providesTags: ['Reviews'],
    }),
    getUnreadNotificationCount: builder.query({
      query: (userId) => `/notifications/user/${userId}/unread-count`,
      providesTags: ['Notifications'],
    }),
    getNotificationsByUser: builder.query({
      query: (userId) => `/notifications/user/${userId}`,
      providesTags: ['Notifications'],
    }),
    markNotificationRead: builder.mutation({
      query: (notificationId) => ({
        url: `/notifications/${notificationId}/read`,
        method: 'PATCH',
      }),
      invalidatesTags: ['Notifications'],
    }),
  }),
});

export const {
  useGetMentorsQuery,
  useGetMentorsByStatusQuery,
  useGetMentorByUserIdQuery,
  useApplyAsMentorMutation,
  useApproveMentorMutation,
  useRejectMentorMutation,
  useReapplyMentorMutation,
  useUpdateMentorMutation,
  useGetUsersQuery,
  useGetUserByIdQuery,
  useLazyGetUserByIdQuery,
  useGetSessionsByUserQuery,
  useGetSessionsByMentorQuery,
  useGetSessionsQuery,
  useBookSessionMutation,
  useAcceptSessionMutation,
  useRejectSessionMutation,
  useCompleteSessionMutation,
  useGetGroupsQuery,
  useGetMyGroupsQuery,
  useCreateGroupMutation,
  useJoinGroupMutation,
  useLeaveGroupMutation,
  useGetAdminDashboardQuery,
  useGetSkillsQuery,
  useSearchSkillsQuery,
  useCreateSkillMutation,
  useUpdateSkillMutation,
  useDeleteSkillMutation,
  useGetReviewsByMentorQuery,
  useGetReviewsByLearnerQuery,
  useCreateReviewMutation,
  useGetMentorRatingSummaryQuery,
  useGetUnreadNotificationCountQuery,
  useGetNotificationsByUserQuery,
  useMarkNotificationReadMutation,
} = platformApi;
