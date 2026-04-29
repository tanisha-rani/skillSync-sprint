const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 0,
});

export function buildUserMap(users = []) {
  return new Map((users || []).filter(Boolean).map((user) => [String(user.id), user]));
}

export function getInitials(name = '') {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() || '')
    .join('');
}

function getDisplayNameFromEmail(email = '') {
  const localPart = String(email || '').split('@')[0]?.trim();

  if (!localPart) {
    return '';
  }

  return localPart
    .split(/[._-]+/)
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase())
    .join(' ');
}

export function formatCurrency(amount = 0) {
  return currency.format(Number(amount) || 0);
}

export function formatSessionDate(dateValue) {
  if (!dateValue) {
    return 'TBD';
  }

  const date = new Date(dateValue);

  if (Number.isNaN(date.getTime())) {
    return String(dateValue);
  }

  return date.toLocaleDateString('en-IN', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
}

export function mapMentorToCard(mentor, userMap) {
  const user = userMap.get(String(mentor.userId));
  const profileStatus = String(mentor.status || '').toUpperCase();
  const displayName =
    mentor.applicantName ||
    user?.name ||
    getDisplayNameFromEmail(mentor.applicantEmail) ||
    'Expert Mentor';
  const experienceLabel =
    mentor.experienceYears === 1 ? '1 year experience' : `${mentor.experienceYears} yrs experience`;

  return {
    id: mentor.id,
    userId: mentor.userId,
    name: displayName,
    initials: getInitials(displayName),
    experience: experienceLabel,
    rating: mentor.averageRating ? mentor.averageRating.toFixed(1) : 'New',
    reviews: mentor.totalReviews ?? 0,
    price: `${formatCurrency(mentor.hourlyRate)}/hr`,
    status: profileStatus === 'APPROVED' ? 'Available' : profileStatus || 'Not available',
    bio: mentor.bio || 'Mentor profile is still being updated.',
    skills: mentor.skills || [],
    raw: mentor,
  };
}

export function mapGroupToCard(group, userMap, currentUserId) {
  const creator = userMap.get(String(group.creatorUserId));
  const topics = group.topics || [];
  const leadTopic = topics[0]?.toLowerCase() || '';
  const icon =
    leadTopic.includes('spring') ? 'rocket' : leadTopic.includes('ai') || leadTopic.includes('ml') ? 'bot' : 'chart';

  return {
    id: group.id,
    icon,
    name: group.name,
    members: group.memberCount,
    host: creator?.name || `User #${group.creatorUserId}`,
    description: group.description || 'No group description added yet.',
    tags: topics,
    activity: `Created ${formatSessionDate(group.createdAt)}`,
    posts: `${topics.length} topic tags`,
    access: group.status,
    joined: (group.memberUserIds || []).includes(Number(currentUserId)),
    raw: group,
  };
}

export function mapSessionToRow(session, mentorMap, learnerMap, roleLabel) {
  const backendMentorName = String(session.mentorName || '').trim();
  const mentorName =
    backendMentorName && !backendMentorName.startsWith('Mentor #')
      ? backendMentorName
      : mentorMap.get(String(session.mentorId))?.name || 'Mentor';
  const learnerName = session.learnerName || learnerMap.get(String(session.learnerId))?.name || `Learner #${session.learnerId}`;

  return {
    id: session.id,
    raw: session,
    mentorId: session.mentorId,
    learnerId: session.learnerId,
    date: formatSessionDate(session.sessionDate),
    time: 'Scheduled',
    mentor: roleLabel === 'Mentor' ? learnerName : mentorName,
    topic: session.requiredSkill ? `${session.topic} (${session.requiredSkill})` : session.topic,
    duration: `${session.duration} min`,
    status: session.status,
  };
}
