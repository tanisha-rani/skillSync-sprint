import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import MentorCard from './MentorCard.jsx';

describe('MentorCard', () => {
  const mentor = {
    initials: 'RS',
    name: 'Riya Sharma',
    experience: '5 yrs experience',
    status: 'Available',
    rating: '4.8',
    reviews: 12,
    skills: ['Java', 'Spring Boot'],
    bio: 'Helps learners build backend projects.',
    price: '₹700/hr',
  };

  it('renders mentor profile information', () => {
    render(<MentorCard mentor={mentor} />);

    expect(screen.getByRole('heading', { name: 'Riya Sharma' })).toBeInTheDocument();
    expect(screen.getByText('5 yrs experience')).toBeInTheDocument();
    expect(screen.getByText('Spring Boot')).toBeInTheDocument();
    expect(screen.getByText('₹700/hr')).toBeInTheDocument();
  });

  it('calls action handlers with the mentor payload', async () => {
    const onPrimaryAction = vi.fn();
    const onSecondaryAction = vi.fn();
    const user = userEvent.setup();

    render(
      <MentorCard
        mentor={mentor}
        onPrimaryAction={onPrimaryAction}
        onSecondaryAction={onSecondaryAction}
      />,
    );

    await user.click(screen.getByRole('button', { name: 'Book' }));
    await user.click(screen.getByRole('button', { name: 'View' }));

    expect(onPrimaryAction).toHaveBeenCalledWith(mentor);
    expect(onSecondaryAction).toHaveBeenCalledWith(mentor);
  });

  it('hides the bio when compact mode is enabled', () => {
    render(<MentorCard mentor={mentor} compact />);

    expect(screen.queryByText('Helps learners build backend projects.')).not.toBeInTheDocument();
  });
});
