export const navigationByRole = {
  ROLE_LEARNER: [
    { id: 'dashboard', label: '🏠 Dashboard', icon: 'home' },
    { id: 'mentors', label: '👨‍🏫 Find Mentors', icon: 'search' },
    { id: 'booking', label: '📅 Book Session', icon: 'calendar' },
    { id: 'groups', label: '👥 Learning Groups', icon: 'users' },
  ],
  ROLE_MENTOR: [
    { id: 'dashboard', label: '🏠 Dashboard', icon: 'home' },
    { id: 'groups', label: '👥 Communities', icon: 'users' },
  ],
  ROLE_ADMIN: [
    { id: 'admin', label: '🛡️ Admin Console', icon: 'shield' },
    { id: 'skills-admin', label: '⭐ Skills', icon: 'star' },
    { id: 'groups', label: '👥 Groups', icon: 'users' },
  ],
};

export const appData = {
  users: {
    ROLE_LEARNER: {
      name: 'Rahul Sharma',
      initials: 'RS',
      roleLabel: 'Learner',
      headline: 'Building full-stack confidence with structured mentor support.',
    },
    ROLE_MENTOR: {
      name: 'Priya Sharma',
      initials: 'PS',
      roleLabel: 'Mentor',
      headline: 'Helping learners turn concepts into projects and interview wins.',
    },
    ROLE_ADMIN: {
      name: 'Admin Team',
      initials: 'AD',
      roleLabel: 'Admin',
      headline: 'Keeping mentor quality, session reliability, and community health strong.',
    },
  },
  stats: {
    learner: [
      { label: 'Upcoming Sessions', value: '2', change: '+1 this week', icon: 'calendar' },
      { label: 'Connected Mentors', value: '4', change: '+1 this month', icon: 'mentor' },
      { label: 'Active Groups', value: '3', change: '2 new posts', icon: 'group' },
      { label: 'Sessions Completed', value: '12', change: '+3 this month', icon: 'star' },
    ],
    mentor: [
      { label: 'Pending Requests', value: '6', change: '3 require response today', icon: 'calendar' },
      { label: 'Repeat Learners', value: '18', change: '+4 this month', icon: 'mentor' },
      { label: 'Average Rating', value: '4.9', change: '96 reviews', icon: 'star' },
      { label: 'Monthly Earnings', value: '₹42K', change: '+12% vs last month', icon: 'money' },
    ],
    admin: [
      { label: 'Total Users', value: '1,248', change: '+12% this month', icon: 'users' },
      { label: 'Active Mentors', value: '318', change: '+8% this month', icon: 'mentor' },
      { label: 'Sessions Booked', value: '4,521', change: '+24% this month', icon: 'calendar' },
      { label: 'Platform Revenue', value: '₹2.1M', change: '+18% this month', icon: 'money' },
    ],
  },
  mentors: [
    { id: 1, name: 'Priya Sharma', initials: 'PS', experience: '8 yrs experience', rating: '4.9', reviews: 42, price: '₹800/hr', status: 'Available today', bio: 'Backend mentor for Java, Spring Boot, API design and interview preparation.', skills: ['Spring Boot', 'Java', 'REST APIs'] },
    { id: 2, name: 'Arjun Mehta', initials: 'AM', experience: '5 yrs experience', rating: '4.7', reviews: 28, price: '₹600/hr', status: 'Available tomorrow', bio: 'Helps learners break down ML fundamentals into project-friendly steps.', skills: ['Machine Learning', 'Python', 'TensorFlow'] },
    { id: 3, name: 'Neha Kapoor', initials: 'NK', experience: '6 yrs experience', rating: '4.8', reviews: 35, price: '₹750/hr', status: 'Available today', bio: 'Frontend and Node.js mentor focused on product thinking and clean architecture.', skills: ['React', 'Node.js', 'TypeScript'] },
    { id: 4, name: 'Rahul Gupta', initials: 'RG', experience: '4 yrs experience', rating: '4.6', reviews: 19, price: '₹500/hr', status: 'Available this week', bio: 'DSA mentor with structured practice plans and mock interview support.', skills: ['DSA', 'Java', 'LeetCode'] },
    { id: 5, name: 'Sanjay Kumar', initials: 'SK', experience: '10 yrs experience', rating: '4.95', reviews: 87, price: '₹950/hr', status: 'High demand', bio: 'Senior architect guiding system design, Docker, and scalable microservices.', skills: ['Microservices', 'Docker', 'System Design'] },
    { id: 6, name: 'Divya Verma', initials: 'DV', experience: '7 yrs experience', rating: '4.7', reviews: 31, price: '₹700/hr', status: 'Available today', bio: 'Angular and RxJS specialist who also coaches career transitions into frontend.', skills: ['Angular', 'RxJS', 'NgRx'] },
  ],
  sessions: [
    { date: 'Mar 15', time: '10:00 AM', mentor: 'Priya Sharma', topic: 'Spring Boot REST APIs', duration: '60 min', status: 'Accepted' },
    { date: 'Mar 17', time: '3:00 PM', mentor: 'Arjun Mehta', topic: 'ML Fundamentals', duration: '60 min', status: 'Pending' },
    { date: 'Mar 21', time: '7:30 PM', mentor: 'Neha Kapoor', topic: 'React Component Patterns', duration: '45 min', status: 'Completed' },
  ],
  dashboard: {
    learner: {
      focus: [
        'Complete your upcoming Spring Boot session prep',
        'Review mentor notes from your last completed session',
        'Stay active in your DSA preparation group this week',
      ],
    },
    mentor: {
      batches: [
        { name: 'Spring Boot Cohort A', learners: 18, nextSession: 'Today, 6:00 PM', topic: 'REST API design' },
        { name: 'Interview Prep Batch', learners: 12, nextSession: 'Tomorrow, 8:30 PM', topic: 'System design mock review' },
        { name: 'Backend Mentorship Circle', learners: 9, nextSession: 'Friday, 7:00 PM', topic: 'JPA relationships and queries' },
      ],
      learners: [
        { name: 'Rahul Sharma', track: 'Spring Boot', progress: 'Module 6 of 8', status: 'Needs feedback' },
        { name: 'Ananya Gupta', track: 'System Design', progress: 'Project review pending', status: 'On track' },
        { name: 'Mohit Verma', track: 'Java Core', progress: 'Practice sheet submitted', status: 'Ready for next batch' },
      ],
      profile: {
        speciality: 'Java, Spring Boot, REST APIs',
        availability: 'Mon-Fri, 6 PM to 9 PM',
        batchCapacity: '39 / 50 seats filled',
        responseTime: 'Usually within 2 hours',
      },
    },
  },
  groups: [
    { id: 1, icon: 'rocket', name: 'Spring Boot Beginners', members: 48, host: 'Priya Sharma', description: 'A practical group for Java developers learning Spring Boot from scratch with weekly practice sessions.', tags: ['Java', 'Spring Boot', 'REST APIs', 'JPA'], activity: 'Active 2h ago', posts: '128 posts this week', access: 'Public Group', joined: false },
    { id: 2, icon: 'bot', name: 'Machine Learning Study Circle', members: 72, host: 'Arjun Mehta', description: 'Weekly discussions on ML algorithms, research papers, and hands-on implementation labs.', tags: ['Python', 'TensorFlow', 'Scikit-learn', 'Statistics'], activity: 'Active 5h ago', posts: '95 posts this week', access: 'Public Group', joined: false },
    { id: 3, icon: 'chart', name: 'DSA Interview Preparation', members: 103, host: 'Rahul Gupta', description: 'Daily LeetCode problems, mock interviews, and solution walkthroughs for interview readiness.', tags: ['Data Structures', 'Algorithms', 'LeetCode'], activity: 'Active 1h ago', posts: '146 posts this week', access: 'Members Only', joined: true },
  ],
  admin: {
    approvals: [
      { name: 'Vivek Singh', email: 'vivek@gmail.com', skills: ['React', 'TypeScript'], experience: '5 yrs' },
      { name: 'Sneha Patel', email: 'sneha@gmail.com', skills: ['Data Science', 'R'], experience: '3 yrs' },
      { name: 'Karan Mehta', email: 'karan@gmail.com', skills: ['DevOps', 'Kubernetes'], experience: '7 yrs' },
    ],
    activity: [
      'Mentor Divya Verma approved 5 min ago',
      'New session booked by Rahul S. 12 min ago',
      'Group DSA Prep reached 100 members 1h ago',
      'Priya Sharma received 5-star review 2h ago',
      '14 new users registered today',
    ],
    performance: [
      { label: 'Session Completion Rate', value: '82%', color: 'var(--accent)' },
      { label: 'Mentor Satisfaction Score', value: '91%', color: 'var(--success)' },
      { label: 'Group Engagement Rate', value: '65%', color: 'var(--orange)' },
      { label: 'Learner Retention', value: '78%', color: 'var(--info)' },
    ],
  },
  booking: {
    month: 'March 2025',
    selectedDate: 'March 18, 2025',
    selectedTime: '11:00 AM IST',
    durations: ['30 min', '60 min', '90 min'],
    slots: ['09:00 AM', '10:30 AM', '11:00 AM', '02:00 PM', '04:30 PM'],
    topic: 'Spring Boot REST APIs & JPA Relationships',
    format: 'Video Call',
    total: '₹800',
  },
};
