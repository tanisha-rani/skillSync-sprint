import { describe, expect, it } from 'vitest';

import {
  buildUserMap,
  formatCurrency,
  formatSessionDate,
  getInitials,
  mapGroupToCard,
  mapMentorToCard,
  mapSessionToRow,
} from './viewMappers.js';

describe('viewMappers', () => {
  it('builds a user map and ignores empty entries', () => {
    const users = [{ id: 1, name: 'Aman' }, null, { id: 2, name: 'Riya' }];

    const userMap = buildUserMap(users);

    expect(userMap.get('1').name).toBe('Aman');
    expect(userMap.get('2').name).toBe('Riya');
    expect(userMap.size).toBe(2);
  });

  it('formats initials, currency, and fallback dates', () => {
    expect(getInitials('Riya Sharma')).toBe('RS');
    expect(formatCurrency(700)).toContain('700');
    expect(formatSessionDate()).toBe('TBD');
    expect(formatSessionDate('not-a-date')).toBe('not-a-date');
  });

  it('maps approved mentors to available cards', () => {
    const userMap = buildUserMap([{ id: 10, name: 'Nisha Verma' }]);

    const card = mapMentorToCard(
      {
        id: 1,
        userId: 10,
        status: 'APPROVED',
        experienceYears: 1,
        averageRating: 4.75,
        totalReviews: 8,
        hourlyRate: 500,
        skills: ['React'],
      },
      userMap,
    );

    expect(card.name).toBe('Nisha Verma');
    expect(card.initials).toBe('NV');
    expect(card.experience).toBe('1 year experience');
    expect(card.rating).toBe('4.8');
    expect(card.status).toBe('Available');
  });

  it('maps groups and session rows for display', () => {
    const userMap = buildUserMap([{ id: 3, name: 'Host User' }]);
    const groupCard = mapGroupToCard(
      {
        id: 7,
        name: 'Spring Learners',
        creatorUserId: 3,
        memberCount: 12,
        topics: ['Spring Boot'],
        status: 'OPEN',
        memberUserIds: [5],
      },
      userMap,
      5,
    );

    expect(groupCard.icon).toBe('rocket');
    expect(groupCard.host).toBe('Host User');
    expect(groupCard.joined).toBe(true);

    const sessionRow = mapSessionToRow(
      {
        id: 9,
        mentorId: 2,
        learnerId: 5,
        mentorName: 'Mentor #2',
        learnerName: 'Aman',
        topic: 'REST APIs',
        requiredSkill: 'Spring Boot',
        duration: 60,
        status: 'REQUESTED',
        sessionDate: '2026-05-10',
      },
      buildUserMap([{ id: 2, name: 'Riya Mentor' }]),
      buildUserMap([{ id: 5, name: 'Aman' }]),
      'Learner',
    );

    expect(sessionRow.mentor).toBe('Riya Mentor');
    expect(sessionRow.topic).toBe('REST APIs (Spring Boot)');
    expect(sessionRow.duration).toBe('60 min');
  });
});
